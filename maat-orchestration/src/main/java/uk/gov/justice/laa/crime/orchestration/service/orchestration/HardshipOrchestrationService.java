package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.service.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipOrchestrationService implements AssessmentOrchestrator<HardshipReviewDTO> {

    private final HardshipService hardshipService;
    private final ContributionService contributionService;
    private final ProceedingsService proceedingsService;
    private final AssessmentSummaryService assessmentSummaryService;
    private final MaatCourtDataService maatCourtDataService;

    public HardshipReviewDTO find(int hardshipReviewId) {
        return hardshipService.find(hardshipReviewId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        // invoke the validation service to Check user has rep order reserved
        ApplicationDTO application;

        application = request.getApplicationDTO();

        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();

        ApiPerformHardshipResponse performHardshipResponse = hardshipService.create(request);
        try {
            // Need to refresh from DB as HardshipDetail ids may have changed
            HardshipReviewDTO newHardship = hardshipService.find(performHardshipResponse.getHardshipReviewId());

            CourtType courtType = request.getCourtType();
            if (courtType == CourtType.MAGISTRATE) {
                hardshipOverview.setMagCourtHardship(newHardship);
                AssessmentStatusDTO assessmentStatusDTO = newHardship.getAsessmentStatus();
                if (assessmentStatusDTO != null && CurrentStatus.COMPLETE.getStatus().equals(assessmentStatusDTO.getStatus())) {
                    application = processMagCourtHardshipRules(request);
                }
            } else if (courtType == CourtType.CROWN_COURT) {
                hardshipOverview.setCrownCourtHardship(newHardship);
                AssessmentStatusDTO assessmentStatusDTO = newHardship.getAsessmentStatus();
                if (assessmentStatusDTO != null && CurrentStatus.COMPLETE.getStatus().equals(assessmentStatusDTO.getStatus())) {
                    application = checkActionsAndUpdateApplication(request);
                }
            }

            // Update assessment summary view - displayed on the application tab
            AssessmentSummaryDTO hardshipSummary = assessmentSummaryService.getSummary(newHardship, courtType);
            assessmentSummaryService.updateApplication(application, hardshipSummary);
        } catch (Exception ex) {
            hardshipService.rollback(request);
            throw new APIClientException(ex.getMessage());
        }
        return application;
    }

    public ApplicationDTO update(WorkflowRequest request) {
        // invoke the validation service to check that data has not been modified by another user
        // invoke the validation service to Check user has rep order reserved

        hardshipService.update(request);
        try {
            CourtType courtType = request.getCourtType();
            if (courtType == CourtType.MAGISTRATE) {
                AssessmentStatusDTO assessmentStatusDTO = request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO()
                        .getHardship().getMagCourtHardship().getAsessmentStatus();
                if (assessmentStatusDTO != null && CurrentStatus.COMPLETE.getStatus().equals(assessmentStatusDTO.getStatus())) {
                    request.setApplicationDTO(processMagCourtHardshipRules(request));
                }
            } else if (courtType == CourtType.CROWN_COURT) {
                AssessmentStatusDTO assessmentStatusDTO = request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO()
                        .getHardship().getCrownCourtHardship().getAsessmentStatus();
                if (assessmentStatusDTO != null && CurrentStatus.COMPLETE.getStatus().equals(assessmentStatusDTO.getStatus())) {
                    request.setApplicationDTO(checkActionsAndUpdateApplication(request));
                }
            }
        } catch (Exception ex) {
            hardshipService.rollback(request);
            throw new APIClientException(ex.getMessage());
        }
        return request.getApplicationDTO();
    }

    private ApplicationDTO processMagCourtHardshipRules(WorkflowRequest request) {
        // call assessments.determine_mags_rep_decision stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(), StoredProcedure.DETERMINE_MAGS_REP_DECISION
        ));
        if (contributionService.isVariationRequired(request.getApplicationDTO())) {
            return contributionService.calculate(request);
        }
        return request.getApplicationDTO();
    }

    /**
     * This method performs the logic from the following stored procedures:
     * crown_court.check_crown_court_actions(p_application_object => p_application_object);
     * application.update_cc_application(p_application_object => p_application_object);
     */
    private ApplicationDTO checkActionsAndUpdateApplication(WorkflowRequest request) {
        request.setApplicationDTO(contributionService.calculate(request));

        // call application.pre_update_cc_application stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(), StoredProcedure.PRE_UPDATE_CC_APPLICATION
        ));

        proceedingsService.updateApplication(request);

        // Call application.handle_eform_result stored procedure OR Equivalent ATS service endpoint

        // Call crown_court.xx_process_activity_and_get_correspondence stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(),
                StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE
        ));

        return request.getApplicationDTO();
    }

}
