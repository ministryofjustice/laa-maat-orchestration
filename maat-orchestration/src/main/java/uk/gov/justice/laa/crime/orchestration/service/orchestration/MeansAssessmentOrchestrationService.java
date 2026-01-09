package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static uk.gov.justice.laa.crime.orchestration.common.Constants.WRN_MSG_INCOMPLETE_ASSESSMENT;
import static uk.gov.justice.laa.crime.orchestration.common.Constants.WRN_MSG_REASSESSMENT;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.exception.StoredProcedureValidationException;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.MeansAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationService;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationTrackingDataService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.FeatureDecisionService;
import uk.gov.justice.laa.crime.orchestration.service.IncomeEvidenceService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentOrchestrationService {
    private final ContributionService contributionService;
    private final ProceedingsService proceedingsService;
    private final MeansAssessmentService meansAssessmentService;
    private final MaatCourtDataService maatCourtDataService;
    private final AssessmentSummaryService assessmentSummaryService;
    private final FeatureDecisionService featureDecisionService;
    private final RepOrderService repOrderService;
    private final ApplicationService applicationService;
    private final WorkflowPreProcessorService workflowPreProcessorService;
    private final IncomeEvidenceService incomeEvidenceService;
    private final ApplicationTrackingDataService applicationTrackingDataService;
    private final ApplicationTrackingMapper applicationTrackingMapper;
    private final MeansAssessmentMapper meansAssessmentMapper;
    private final MaatCourtDataApiService maatCourtDataApiService;

    public FinancialAssessmentDTO find(int assessmentId, int applicantId) {
        return meansAssessmentService.find(assessmentId, applicantId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        ApplicationDTO application = request.getApplicationDTO();
        try {
            Long repId = application.getRepId();
            log.debug("Creating Means assessment for applicationId = {}", repId);

            preProcessRequest(request, Action.CREATE_ASSESSMENT);
            meansAssessmentService.create(request);
            application = processCrownCourtProceedings(request, Action.CREATE_ASSESSMENT);
            applicationService.updateDateModified(request, application);

            log.debug("Created Means assessment for applicationId = {}", repId);
        } catch (ValidationException | CrimeValidationException exception) {
            throw exception;
        } catch (StoredProcedureValidationException exception) {
            meansAssessmentService.rollback(request);
            throw new ValidationException(exception.getMessage());
        } catch (Exception ex) {
            log.warn("Create Means assessment failed with the exception: {}", ex.getMessage(), ex);
            meansAssessmentService.rollback(request);
            Sentry.captureException(ex);
            throw new MaatOrchestrationException(request.getApplicationDTO());
        }

        return application;
    }

    public ApplicationDTO update(WorkflowRequest request) {

        ApplicationDTO application = request.getApplicationDTO();
        try {
            Long repId = application.getRepId();
            log.debug("Updating Means assessment for applicationId = {}", repId);

            preProcessRequest(request, Action.UPDATE_ASSESSMENT);
            meansAssessmentService.update(request);
            application = processCrownCourtProceedings(request, Action.UPDATE_ASSESSMENT);
            applicationService.updateDateModified(request, application);

            log.debug("Updated Means assessment for applicationId = {}", repId);
        } catch (ValidationException | CrimeValidationException exception) {
            throw exception;
        } catch (StoredProcedureValidationException exception) {
            meansAssessmentService.rollback(request);
            throw new ValidationException(exception.getMessage());
        } catch (Exception ex) {
            log.warn("Update Means assessment failed with the exception: {}", ex.getMessage(), ex);
            meansAssessmentService.rollback(request);
            Sentry.captureException(ex);
            throw new MaatOrchestrationException(request.getApplicationDTO());
        }

        return application;
    }

    private void preProcessRequest(WorkflowRequest request, Action action) {
        if (featureDecisionService.isMaatPostAssessmentProcessingEnabled(request)) {
            RepOrderDTO repOrderDTO = repOrderService.getRepOrder(request);
            UserActionDTO userActionDTO = meansAssessmentMapper.getUserActionDto(request, action);

            workflowPreProcessorService.preProcessRequest(request, repOrderDTO, userActionDTO);
        }
    }

    private ApplicationDTO processCrownCourtProceedings(WorkflowRequest request, Action action) {
        request.getApplicationDTO().setAlertMessage("");

        // call post_processing_part_1_c3 and map the application
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(),
                request.getUserDTO(),
                StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3));

        RepOrderDTO repOrderDTO = maatCourtDataApiService.getRepOrderByRepId(
                request.getApplicationDTO().getRepId().intValue());

        if (featureDecisionService.isMaatPostAssessmentProcessingEnabled(request)) {
            meansAssessmentPostProcessingWorkflow(request, action, repOrderDTO);
        } else {
            // check feature flag here - only need to do this for the new workflow, not for the old way of doing
            // things
            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    contributionService.calculate(request),
                    request.getUserDTO(),
                    StoredProcedure.PRE_UPDATE_CC_APPLICATION));
        }

        // Check for any validation alerts resulted as part of the pre_update_checks and raise exception
        String alertMessage = request.getApplicationDTO().getAlertMessage();
        if (StringUtils.isNotBlank(alertMessage)
                && (alertMessage.contains(WRN_MSG_REASSESSMENT)
                        || alertMessage.contains(WRN_MSG_INCOMPLETE_ASSESSMENT))) {
            throw new StoredProcedureValidationException(alertMessage);
        }

        // call CCP service
        proceedingsService.updateApplication(request, repOrderDTO);

        // call post_processing_part_2
        ApplicationDTO application = maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(), StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_2);

        AssessmentSummaryDTO assessmentSummaryDTO = assessmentSummaryService.getSummary(
                request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO());
        assessmentSummaryService.updateApplication(application, assessmentSummaryDTO);

        application.setTransactionId(null);
        return application;
    }

    private void meansAssessmentPostProcessingWorkflow(
            WorkflowRequest request, Action action, RepOrderDTO repOrderDTO) {
        if (action == Action.CREATE_ASSESSMENT) {
            incomeEvidenceService.createEvidence(request, repOrderDTO);
        }

        proceedingsService.determineMagsRepDecision(request);

        request.setApplicationDTO(contributionService.calculate(request));

        ApplicationTrackingOutputResult applicationTrackingOutputResult =
                applicationTrackingMapper.build(request, repOrderDTO);

        if (applicationTrackingOutputResult.getUsn() != null) {
            applicationTrackingDataService.sendTrackingOutputResult(applicationTrackingOutputResult);
        }
    }
}
