package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentOrchestrationService implements AssessmentOrchestrator<FinancialAssessmentDTO> {

    private final MeansAssessmentService meansAssessmentService;
    private final ContributionService contributionService;
    private final ProceedingsService proceedingsService;
    private final MaatCourtDataService maatCourtDataService;
    private static final String DB_ASSESSMENT_POST_PROCESSING_PART_1 = "post_assessment_processing_part_1";
    private static final String DB_ASSESSMENT_POST_PROCESSING_PART_2 = "post_assessment_processing_part_2";
    private static final String DB_ASSESSMENT_POST_PROCESSING_PART_1_C3 = "post_assessment_processing_part_1_c3";
    private static final String DB_PRE_UPDATE_CC_APPLICATION = "pre_update_cc_application";
    private static final String DB_PACKAGE_ASSESSMENTS = "assessments";
    private static final String DB_PACKAGE_APPLICATION = "application";

    public FinancialAssessmentDTO find(int assessmentId) {
        return meansAssessmentService.find(assessmentId);
    }

    public ApplicationDTO create(WorkflowRequest request) {

        Long repId = request.getApplicationDTO().getRepId();
        log.debug("Creating Means assessment for applicationId = " + repId);
        meansAssessmentService.create(request);
        ApplicationDTO application = processCrownCourtProceedings(request);
        log.debug("Created Means assessment for applicationId = " + repId);
        return application;
    }

    public ApplicationDTO update(WorkflowRequest request) {

        Long repId = request.getApplicationDTO().getRepId();
        log.debug("Updating Means assessment for applicationId = " + repId);
        meansAssessmentService.update(request);
        ApplicationDTO application = processCrownCourtProceedings(request);
        log.debug("Updated Means assessment for applicationId = " + repId);
        return application;
    }

    private ApplicationDTO processCrownCourtProceedings(WorkflowRequest request) {
        if (request.isC3Enabled()) {
            // call post_processing_part_1_c3 and map the application
            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    request.getApplicationDTO(),
                    request.getUserDTO(),
                    DB_PACKAGE_ASSESSMENTS,
                    DB_ASSESSMENT_POST_PROCESSING_PART_1_C3)
            );

            // call pre_update_cc_application with the calculated contribution and map the application
            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    contributionService.calculateContribution(request),
                    request.getUserDTO(),
                    DB_PACKAGE_APPLICATION,
                    DB_PRE_UPDATE_CC_APPLICATION)
            );
        } else {
            // call post_processing_part1 and map the application
            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    request.getApplicationDTO(),
                    request.getUserDTO(),
                    DB_PACKAGE_ASSESSMENTS,
                    DB_ASSESSMENT_POST_PROCESSING_PART_1)
            );
        }
        proceedingsService.updateApplication(request);

        // call post_processing_part_2
        ApplicationDTO application = maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(),
                request.getUserDTO(),
                DB_PACKAGE_ASSESSMENTS,
                DB_ASSESSMENT_POST_PROCESSING_PART_2);
        application.setTransactionId(null);
        return application;
    }

}
