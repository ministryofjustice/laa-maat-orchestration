package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationService;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationTrackingDataService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.PassportAssessmentService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

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
    private final ApplicationTrackingDataService applicationTrackingDataService;

    private void preProcessPassportRequest(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {
        UserActionDTO userActionDTO = passportAssessmentMapper.getUserActionDTO(workflowRequest);

        workflowPreProcessorService.preProcessRequest(workflowRequest, repOrderDTO, userActionDTO);
    }

    private boolean shouldProcessActivityAndGetCorrespondence(ApplicationDTO applicationDTO) {
        return !NewWorkReason.FMA.equals(NewWorkReason.getFrom(
                applicationDTO.getPassportedDTO().getNewWorkReason().getCode()));
    }

    private void updateAssessmentSummary(ApplicationDTO applicationDTO) {
        AssessmentSummaryDTO assessmentSummaryDTO =
                assessmentSummaryService.getSummary(applicationDTO.getPassportedDTO());

        assessmentSummaryService.updateApplication(applicationDTO, assessmentSummaryDTO);
    }

    private void updateApplicationTracking(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {
        applicationTrackingDataService.sendTrackingOutputResult(
                workflowRequest, repOrderDTO, AssessmentType.PASSPORT, RequestSource.PASSPORT_IOJ);
    }

    private void performPostProcessing(
            WorkflowRequest workflowRequest, ApplicationDTO applicationDTO, RepOrderDTO repOrderDTO) {
        workflowRequest.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                applicationDTO, workflowRequest.getUserDTO(), StoredProcedure.MANAGE_PASSPORT_EVIDENCE));
        proceedingsService.determineMagsRepDecision(workflowRequest);
        workflowRequest.setApplicationDTO(contributionService.calculate(workflowRequest));
        workflowRequest.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                workflowRequest.getApplicationDTO(),
                workflowRequest.getUserDTO(),
                StoredProcedure.PRE_UPDATE_CC_APPLICATION));
        proceedingsService.updateApplication(workflowRequest, repOrderDTO);

        if (shouldProcessActivityAndGetCorrespondence(applicationDTO)) {
            workflowRequest.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    applicationDTO,
                    workflowRequest.getUserDTO(),
                    StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE));
        }

        updateAssessmentSummary(applicationDTO);
        applicationService.updateDateModified(workflowRequest, applicationDTO);
        updateApplicationTracking(workflowRequest, repOrderDTO);
    }

    public PassportedDTO find(int id) {
        return passportAssessmentService.find(id);
    }

    public ApplicationDTO create(WorkflowRequest workflowRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        RepOrderDTO repOrderDTO = repOrderService.getRepOrder(workflowRequest);

        preProcessPassportRequest(workflowRequest, repOrderDTO);

        Integer assessmentId = passportAssessmentService.create(workflowRequest);
        applicationDTO.getPassportedDTO().setPassportedId(Long.valueOf(assessmentId));

        repOrderDTO = repOrderService.updateRepOrderAssessmentDateCompleted(
                workflowRequest, repOrderDTO, LocalDateTime.now());

        try {
            performPostProcessing(workflowRequest, applicationDTO, repOrderDTO);
        } catch (Exception exception) {
            log.error("Create passport assessment post processing failed, rolling back...", exception);
            Sentry.captureException(exception);

            // TODO: Need to call rollback passport assessment and handle failure when ticket LCAM-1987 completed

            throw new MaatOrchestrationException(applicationDTO);
        }

        return applicationDTO;
    }
}
