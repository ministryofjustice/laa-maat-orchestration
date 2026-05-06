package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import io.sentry.Sentry;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationService;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationTrackingDataService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.PassportAssessmentService;

import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassportAssessmentOrchestrationService {

    private final RepOrderService repOrderService;
    private final PassportAssessmentMapper passportAssessmentMapper;
    private final WorkflowPreProcessorService workflowPreProcessorService;
    private final PassportAssessmentService passportAssessmentService;
    private final ProceedingsService proceedingsService;
    private final ContributionService contributionService;
    private final MaatCourtDataService maatCourtDataService;
    private final AssessmentSummaryService assessmentSummaryService;
    private final ApplicationService applicationService;
    private final ApplicationTrackingMapper applicationTrackingMapper;
    private final ApplicationTrackingDataService applicationTrackingDataService;


    public PassportedDTO find(int id) {
        return passportAssessmentService.find(id);
    }

    public ApplicationDTO create(WorkflowRequest workflowRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        RepOrderDTO repOrderDTO = repOrderService.getRepOrder(workflowRequest);
        if (repOrderDTO == null) {
            log.error("Could not find rep order for request {}", workflowRequest);
            throw new MaatOrchestrationException(applicationDTO);
        }
        UserActionDTO userActionDTO = passportAssessmentMapper.getUserActionDTO(workflowRequest);

        workflowPreProcessorService.preProcessPassportRequest(workflowRequest, repOrderDTO, userActionDTO);

        String assessmentId = passportAssessmentService.create(workflowRequest);
        repOrderDTO = repOrderService.updateRepOrderAssessmentDateCompleted(workflowRequest, repOrderDTO, LocalDateTime.now());

        try {
            workflowRequest.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                applicationDTO, workflowRequest.getUserDTO(),
                StoredProcedure.MANAGE_PASSPORT_EVIDENCE));
            proceedingsService.determineMagsRepDecision(workflowRequest);
            workflowRequest.setApplicationDTO(contributionService.calculate(workflowRequest));
            proceedingsService.updateApplication(workflowRequest, repOrderDTO);

            if (!NewWorkReason.FMA.equals(NewWorkReason.getFrom(applicationDTO.getPassportedDTO().getNewWorkReason().getCode()))) {
                workflowRequest.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    applicationDTO, workflowRequest.getUserDTO(),
                    StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE));
            }

            AssessmentSummaryDTO assessmentSummaryDTO = assessmentSummaryService.getSummary(
                applicationDTO.getAssessmentDTO().getIojAppeal());
            assessmentSummaryService.updateApplication(applicationDTO, assessmentSummaryDTO);

            applicationService.updateDateModified(workflowRequest, applicationDTO);

            ApplicationTrackingOutputResult applicationTrackingOutputResult = applicationTrackingMapper.build(
                workflowRequest, repOrderDTO, AssessmentType.PASSPORT, RequestSource.PASSPORT_IOJ);
            if (null != applicationTrackingOutputResult.getUsn()) {
                applicationTrackingDataService.sendTrackingOutputResult(applicationTrackingOutputResult);
            }
        } catch (Exception exception) {
            log.error("Create passport assessment post processing failed, rolling back...", exception);
            Sentry.captureException(exception);

            // TODO: Need to call rollback passport assessment and handle failure when ticket LCAM-1987 and follow on has been completed.

            throw new MaatOrchestrationException(applicationDTO);
        }

        return applicationDTO;
    }
}
