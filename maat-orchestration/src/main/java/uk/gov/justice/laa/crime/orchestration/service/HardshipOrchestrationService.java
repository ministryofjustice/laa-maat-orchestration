package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipOrchestrationService implements AssessmentOrchestrator<HardshipReviewDTO> {

    private final HardshipService hardshipService;
    private final ContributionService contributionService;
    private final ProceedingsService proceedingsService;

    public static final List<CaseType> CC_CASE_TYPES =
            List.of(CaseType.INDICTABLE, CaseType.CC_ALREADY, CaseType.APPEAL_CC, CaseType.COMMITAL);

    public static final List<MagCourtOutcome> MAG_COURT_OUTCOMES =
            List.of(MagCourtOutcome.COMMITTED_FOR_TRIAL, MagCourtOutcome.SENT_FOR_TRIAL,
                    MagCourtOutcome.COMMITTED, MagCourtOutcome.APPEAL_TO_CC
            );

    public HardshipReviewDTO find(int hardshipReviewId) {
        return hardshipService.find(hardshipReviewId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        ApplicationDTO application = request.getApplicationDTO();
        CourtType courtType = isCrownCourt(application) ? CourtType.CROWN_COURT : CourtType.MAGISTRATE;
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
        AssessmentSummaryDTO hardshipSummary = AssessmentSummaryDTO.builder()
                .id(newHardship.getId())
                .status(newHardship.getAsessmentStatus().getStatus())
                .type(courtType == CourtType.CROWN_COURT
                              ? "Hardship Review - Crown Court"
                              : "Hardship Review - Magistrate"
                )
                .result(newHardship.getReviewResult())
                .assessmentDate(newHardship.getReviewDate()).build();

        updateAssessmentSummary(application, hardshipSummary);

        return application;

    }

    public ApplicationDTO update(WorkflowRequest request) {
        return request.getApplicationDTO();
    }

    private boolean isCrownCourt(ApplicationDTO application) {
        String caseType = application.getCaseDetailsDTO().getCaseType();
        if (CC_CASE_TYPES.contains(CaseType.getFrom(caseType))) {
            return true;
        }
        String magsOutcome = application.getMagsOutcomeDTO().getOutcome();
        return CaseType.getFrom(caseType) == CaseType.EITHER_WAY
                && MAG_COURT_OUTCOMES.contains(MagCourtOutcome.getFrom(magsOutcome));
    }

}
