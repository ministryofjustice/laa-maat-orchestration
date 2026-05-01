package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
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


    public PassportedDTO find(int id) {
        return passportAssessmentService.find(id);
    }

    public ApplicationDTO create(WorkflowRequest workflowRequest) {
        RepOrderDTO repOrderDTO = repOrderService.getRepOrder(workflowRequest);
        if (repOrderDTO == null) {
            log.error("Could not find rep order for request {}", workflowRequest);
            throw new MaatOrchestrationException(workflowRequest.getApplicationDTO());
        }
        UserActionDTO userActionDTO = passportAssessmentMapper.getUserActionDTO(workflowRequest);

        workflowPreProcessorService.preProcessPassportRequest(workflowRequest, repOrderDTO, userActionDTO);

        String assessmentId = passportAssessmentService.create(workflowRequest);

        try {
            // TODO: Check what is being done about manage passport evidence, no ticket on backlog and evidence endpoint geared towards income???

            proceedingsService.determineMagsRepDecision(workflowRequest);

            // TODO: Check crown repord called in stored procedure, should use CCP process rep order which looks very similar or CCP update application???

            contributionService.calculate(workflowRequest);

            proceedingsService.updateApplication(workflowRequest, repOrderDTO);

            // TODO: Do we need to do this only if work reason isn't FMA as it is in the stored procedure???
            workflowRequest.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                workflowRequest.getApplicationDTO(),
                workflowRequest.getUserDTO(),
                StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE));

            AssessmentSummaryDTO assessmentSummaryDTO = assessmentSummaryService.getSummary(
                workflowRequest.getApplicationDTO().getAssessmentDTO().getIojAppeal());
            assessmentSummaryService.updateApplication(workflowRequest.getApplicationDTO(), assessmentSummaryDTO);

            // TODO: Do we need to update date modified (app date completed in SP) and call ATS here similar to the IOJ flow???
        } catch (Exception exception) {
            log.error("Create passport assessment post processing failed, rolling back...", exception);
            Sentry.captureException(exception);

            // TODO: Need to call rollback passport assessment and handle failure when ticket LCAM-1987 and follow on has been completed.

            throw new MaatOrchestrationException(workflowRequest.getApplicationDTO());
        }

        return workflowRequest.getApplicationDTO();
    }
}
