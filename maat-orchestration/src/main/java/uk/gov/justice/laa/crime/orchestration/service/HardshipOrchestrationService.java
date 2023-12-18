package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipOrchestrationService implements AssessmentOrchestrator<HardshipReviewDTO> {

    private static final String DB_PACKAGE_ASSESSMENTS = "assessments";
    private static final String DB_PACKAGE_APPLICATION = "application";
    private static final String DB_PACKAGE_CROWN_COURT = "crown_court";
    private static final String DB_DETERMINE_MAGS_REP_DECISION = "determine_mags_rep_decision";
    private static final String DB_PRE_UPDATE_CC_APPLICATION = "pre_update_cc_application";
    private static final String DB_PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE = "xx_process_activity_and_get_correspondence";
    private final HardshipService hardshipService;
    private final ContributionService contributionService;
    private final ProceedingsService proceedingsService;
    private final AssessmentSummaryService assessmentSummaryService;
    private final MaatCourtDataService maatCourtDataService;

    public HardshipReviewDTO find(int hardshipReviewId) {
        return hardshipService.find(hardshipReviewId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        ApplicationDTO application = request.getApplicationDTO();

        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();

        ApiPerformHardshipResponse performHardshipResponse = hardshipService.createHardship(request);
        // Need to refresh from DB as HardshipDetail ids may have changed
        HardshipReviewDTO newHardship = hardshipService.find(performHardshipResponse.getHardshipReviewId());

        if (application.getCourtType() == CourtType.MAGISTRATE) {
            hardshipOverview.setMagCourtHardship(newHardship);
            application = processMagCourtHardshipRules(request);
        } else {
            hardshipOverview.setCrownCourtHardship(newHardship);
            application = checkActionsAndUpdateApplication(request);
        }

        // Update assessment summary view - displayed on the application tab
        AssessmentSummaryDTO hardshipSummary = assessmentSummaryService.getSummary(newHardship, application.getCourtType());
        assessmentSummaryService.updateApplication(application, hardshipSummary);

        return application;
    }

    public ApplicationDTO update(WorkflowRequest request) {
        // invoke the validation service to check that data has not been modified by another user
        // invoke the validation service to Check user has rep order reserved

        hardshipService.updateHardship(request);

        if (request.getApplicationDTO().getCourtType() == CourtType.MAGISTRATE) {
            AssessmentStatusDTO assessmentStatusDTO = request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO()
                    .getHardship().getMagCourtHardship().getAsessmentStatus();
            if (assessmentStatusDTO != null && CurrentStatus.COMPLETE.getValue().equals(assessmentStatusDTO.getStatus())) {
                request.setApplicationDTO(processMagCourtHardshipRules(request));
            }
        } else {
            AssessmentStatusDTO assessmentStatusDTO = request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO()
                    .getHardship().getCrownCourtHardship().getAsessmentStatus();
            if (assessmentStatusDTO != null && CurrentStatus.COMPLETE.getValue().equals(assessmentStatusDTO.getStatus())) {
                request.setApplicationDTO(checkActionsAndUpdateApplication(request));
            }
        }

        return request.getApplicationDTO();
    }

    private ApplicationDTO processMagCourtHardshipRules(WorkflowRequest request) {
        // call assessments.determine_mags_rep_decision stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(request.getApplicationDTO(),
                request.getUserDTO(),
                DB_PACKAGE_ASSESSMENTS,
                DB_DETERMINE_MAGS_REP_DECISION));
        if (contributionService.isVariationRequired(request.getApplicationDTO())) {
            return contributionService.calculateContribution(request);
        }
        return request.getApplicationDTO();
    }

    /**
     * This method performs the logic from the following stored procedures:
     * crown_court.check_crown_court_actions(p_application_object => p_application_object);
     * application.update_cc_application(p_application_object => p_application_object);
     */
    protected ApplicationDTO checkActionsAndUpdateApplication(WorkflowRequest request) {
        request.setApplicationDTO(contributionService.calculateContribution(request));

        // call application.pre_update_cc_application stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(request.getApplicationDTO(),
                request.getUserDTO(),
                DB_PACKAGE_APPLICATION,
                DB_PRE_UPDATE_CC_APPLICATION));

        proceedingsService.updateApplication(request);

        // Call application.handle_eform_result stored procedure OR Equivalent ATS service endpoint

        // Call crown_court.xx_process_activity_and_get_correspondence stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(request.getApplicationDTO(),
                request.getUserDTO(),
                DB_PACKAGE_CROWN_COURT,
                DB_PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE));

        return request.getApplicationDTO();
    }

}
