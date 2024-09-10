package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.exception.MAATServerException;
import uk.gov.justice.laa.crime.enums.orchestration.AssessmentResult;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.service.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentOrchestrationService {

    private final ProceedingsService proceedingsService;
    private final MeansAssessmentService meansAssessmentService;
    private final MaatCourtDataService maatCourtDataService;
    private final AssessmentSummaryService assessmentSummaryService;
    private final MeansContributionServiceFactory contributionServiceFactory;

    public FinancialAssessmentDTO find(int assessmentId, int applicantId) {
        return meansAssessmentService.find(assessmentId, applicantId);
    }

    public ApplicationDTO create(WorkflowRequest request) {

        ApplicationDTO application = request.getApplicationDTO();
        try {
            Long repId = application.getRepId();
            log.debug("Creating Means assessment for applicationId = " + repId);
            log.info("Creating Means assessment for applicationId = " + repId);
            log.info("MeansAssessmentOrchestrationService.create = " + request);
            printStatus(request.getApplicationDTO(), "Before calling processCrownCourtProceedings");
            meansAssessmentService.create(request);
            printStatus(request.getApplicationDTO(), "after calling processCrownCourtProceedings");
            application = processCrownCourtProceedings(request);
            log.debug("Created Means assessment for applicationId = " + repId);
        } catch (ValidationException | CrimeValidationException exception) {
            throw exception;
        } catch (MAATServerException exception) {
            meansAssessmentService.rollback(request);
            throw new ValidationException(exception.getMessage());
        } catch (Exception ex) {
            log.warn("Create Means assessment failed with the exception: {}", ex);
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
            log.info("MeansAssessmentOrchestrationService.update = " + request);
            printStatus(request.getApplicationDTO(), "Before calling processCrownCourtProceedings");
            meansAssessmentService.update(request);
            application = processCrownCourtProceedings(request);
            log.debug("Updated Means assessment for applicationId = " + repId);
        } catch (ValidationException | CrimeValidationException exception) {
            throw exception;
        } catch (MAATServerException exception) {
            meansAssessmentService.rollback(request);
            throw new ValidationException(exception.getMessage());
        } catch (Exception ex) {
            log.warn("Update Means assessment failed with the exception: {}", ex);
            meansAssessmentService.rollback(request);
            Sentry.captureException(ex);
            throw new MaatOrchestrationException(request.getApplicationDTO());
        }
        return application;
    }

    private void printStatus(ApplicationDTO applicationDTO, String message) {
        log.info(message);
        FinancialAssessmentDTO financialAssessmentDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        log.info("printStatus.initialAssessmentDTO.status-->" + initialAssessmentDTO.getAssessmnentStatusDTO().getStatus());
        if (Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())) {
            log.info("printStatus.fullAssessmentDTO.status-->" + fullAssessmentDTO.getAssessmnentStatusDTO().getStatus());
            log.info("printStatus.fullAssessmentDTO.Result-->" + AssessmentResult.getFrom(fullAssessmentDTO.getResult()));
        }
    }

    private ApplicationDTO processCrownCourtProceedings(WorkflowRequest request) {

        MeansContributionService service = contributionServiceFactory.getService(request);
        service.processContributions(request);

        proceedingsService.updateApplication(request);

        log.info("MeansAssessmentOrchestrationService.updateApplication = " + request);

        // call post_processing_part_2
        ApplicationDTO application = maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(),
                request.getUserDTO(),
                StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_2);

        log.info("MeansAssessmentOrchestrationService.ASSESSMENT_POST_PROCESSING_PART_2 = " + request);

        AssessmentSummaryDTO assessmentSummaryDTO = assessmentSummaryService.getSummary(request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO());
        assessmentSummaryService.updateApplication(application, assessmentSummaryDTO);

        application.setTransactionId(null);
        return application;
    }

}
