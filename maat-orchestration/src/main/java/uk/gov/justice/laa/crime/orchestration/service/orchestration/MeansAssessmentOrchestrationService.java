package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.exception.MAATServerException;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.FeatureDecisionService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;

import static uk.gov.justice.laa.crime.orchestration.common.Constants.WRN_MSG_INCOMPLETE_ASSESSMENT;
import static uk.gov.justice.laa.crime.orchestration.common.Constants.WRN_MSG_REASSESSMENT;

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

    public FinancialAssessmentDTO find(int assessmentId, int applicantId) {
        return meansAssessmentService.find(assessmentId, applicantId);
    }

    public ApplicationDTO create(WorkflowRequest request) {

        ApplicationDTO application = request.getApplicationDTO();
        try {
            Long repId = application.getRepId();
            log.debug("Creating Means assessment for applicationId = " + repId);
            meansAssessmentService.create(request);
            application = processCrownCourtProceedings(request);
            log.debug("Created Means assessment for applicationId = " + repId);
        } catch (ValidationException | CrimeValidationException exception) {
            throw exception;
        } catch (MAATServerException exception) {
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
            log.debug("Updating Means assessment for applicationId = " + repId);
            meansAssessmentService.update(request);
            application = processCrownCourtProceedings(request);
            log.debug("Updated Means assessment for applicationId = " + repId);
        } catch (ValidationException | CrimeValidationException exception) {
            throw exception;
        } catch (MAATServerException exception) {
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

    private ApplicationDTO processCrownCourtProceedings(WorkflowRequest request) {

        request.getApplicationDTO().setAlertMessage("");

        if (featureDecisionService.isC3Enabled(request)) {
            // call post_processing_part_1_c3 and map the application
            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    request.getApplicationDTO(),
                    request.getUserDTO(),
                    StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3)
            );

            // call pre_update_cc_application with the calculated contribution and map the application
            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    contributionService.calculate(request),
                    request.getUserDTO(),
                    StoredProcedure.PRE_UPDATE_CC_APPLICATION)
            );
        } else {
            // call post_processing_part1 and map the application
            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    request.getApplicationDTO(),
                    request.getUserDTO(),
                    StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1)
            );
        }

        // Check for any validation alerts resulted as part of the pre_update_checks and raise exception
        String alertMessage = request.getApplicationDTO().getAlertMessage();
        if (StringUtils.isNotBlank(alertMessage) &&
                (alertMessage.contains(WRN_MSG_REASSESSMENT) || alertMessage.contains(WRN_MSG_INCOMPLETE_ASSESSMENT))) {
            throw new MAATServerException(alertMessage);
        }

        // call CCP service
        proceedingsService.updateApplication(request);

        // call post_processing_part_2
        ApplicationDTO application = maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(),
                request.getUserDTO(),
                StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_2);

        AssessmentSummaryDTO assessmentSummaryDTO = assessmentSummaryService
                .getSummary(request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO());
        assessmentSummaryService.updateApplication(application, assessmentSummaryDTO);

        application.setTransactionId(null);
        return application;
    }

}
