package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.helper.CrownCourtHelper;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipOrchestrationService implements AssessmentOrchestrator<HardshipReviewDTO> {

    private final HardshipService hardshipService;
    private final ContributionService contributionService;
    private final ProceedingsService proceedingsService;
    private final CrownCourtHelper crownCourtHelper;
    private final AssessmentSummaryService assessmentSummaryService;

    public HardshipReviewDTO find(int hardshipReviewId) {
        return hardshipService.find(hardshipReviewId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        ApplicationDTO application = request.getApplicationDTO();

        CourtType courtType = crownCourtHelper.getCourtType(application);
        // Set the courtType, as this will be needed in the mapping logic
        application.setCourtType(courtType);
        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();

        ApiPerformHardshipResponse performHardshipResponse = hardshipService.createHardship(request);
        // Need to refresh from DB as HardshipDetail ids may have changed
        HardshipReviewDTO newHardship = hardshipService.find(performHardshipResponse.getHardshipReviewId());

        if (courtType == CourtType.MAGISTRATE) {
            hardshipOverview.setMagCourtHardship(newHardship);
            // TODO: Call assessments.determine_mags_rep_decision stored procedure
            boolean isVariationRequired = contributionService.isVariationRequired(application);
            if (isVariationRequired) {
                ContributionsDTO contributionsDTO = contributionService.calculateContribution(request);
                application.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
            }
        } else {
            hardshipOverview.setCrownCourtHardship(newHardship);
            ContributionsDTO contributionsDTO = contributionService.calculateContribution(request);
            application.getCrownCourtOverviewDTO().setContribution(contributionsDTO);

            // TODO: Call application.pre_update checks stored procedure

            proceedingsService.updateApplication(request);

            // TODO: Call application.handle_eform_result stored procedure

            // TODO: Call crown_court.xx_process_activity_and_get_correspondence stored procedure
        }

        // Update assessment summary view - displayed on the application tab
        AssessmentSummaryDTO hardshipSummary = assessmentSummaryService.getSummary(newHardship, courtType);
        assessmentSummaryService.updateApplication(application, hardshipSummary);

        return application;
    }

    public ApplicationDTO update(WorkflowRequest request) {
        return request.getApplicationDTO();
    }

}
