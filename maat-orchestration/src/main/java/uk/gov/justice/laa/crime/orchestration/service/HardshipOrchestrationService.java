package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipOverviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.CrownCourtMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipOrchestrationService implements AssessmentOrchestrator<HardshipReviewDTO> {

    private final FindHardshipMapper hardshipMapper;
    private final CrownCourtMapper crownCourtMapper;
    private final ContributionMapper contributionMapper;

    private final HardshipApiService hardshipApiService;
    private final CrownCourtApiService crownCourtApiService;
    private final ContributionApiService contributionApiService;

    public static final List<CaseType> CC_CASE_TYPES =
            List.of(CaseType.INDICTABLE, CaseType.CC_ALREADY, CaseType.APPEAL_CC, CaseType.COMMITAL);

    public static final List<MagCourtOutcome> MAG_COURT_OUTCOMES =
            List.of(MagCourtOutcome.COMMITTED_FOR_TRIAL, MagCourtOutcome.SENT_FOR_TRIAL,
                    MagCourtOutcome.COMMITTED, MagCourtOutcome.APPEAL_TO_CC
            );

    public HardshipReviewDTO find(int assessmentId) {
        return null;
    }

    public ApplicationDTO create(ApplicationDTO application) {
        HardshipReviewDTO current;
        CourtType courtType = isCrownCourt(application) ? CourtType.CROWN_COURT : CourtType.MAGISTRATE;
        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();
        if (courtType == CourtType.MAGISTRATE) {
            current = hardshipOverview.getMagCourtHardship();
        } else {
            current = hardshipOverview.getCrownCourtHardship();
        }
        ApiPerformHardshipRequest createRequest = hardshipMapper.fromDto(current);

        createRequest.getHardship().setCourtType(courtType);
        ApiPerformHardshipResponse response = hardshipApiService.create(createRequest);

        // Need to refresh from DB as HardshipDetail ids may have changed
        // This is not captured in the response from the Hardship service, as it has its own schema
        ApiFindHardshipResponse hardship = hardshipApiService.getHardship(response.getHardshipReviewId());
        HardshipReviewDTO newHardship = new HardshipReviewDTO();
        hardshipMapper.toDto(hardship, newHardship);

        if (courtType == CourtType.MAGISTRATE) {
            hardshipOverview.setMagCourtHardship(newHardship);
            // TODO: Call assessments.determine_mags_rep_decision stored procedure
            boolean isVariationRequired = false;
            // Based on calling contribution.contribution_rule_applies (in the process of being migrated to C3)
            if (isVariationRequired) {
                calculateContribution(application);
            }
        } else {
            hardshipOverview.setCrownCourtHardship(newHardship);
            calculateContribution(application);

            // TODO: Call application.pre_update checks stored procedure

            ApiUpdateApplicationRequest apiUpdateApplicationRequest = crownCourtMapper.fromDto(application);
            crownCourtApiService.update(apiUpdateApplicationRequest);

            // TODO: Call application.handle_eform_result stored procedure

            // TODO: Call crown_court.xx_process_activity_and_get_correspondence stored procedure
        }

        // Update assessment summary - displayed on the application tab
        AssessmentSummaryDTO hardshipSummary = AssessmentSummaryDTO.builder()
                .id(newHardship.getId())
                .status(newHardship.getAsessmentStatus().getStatus())
                .type(courtType == CourtType.CROWN_COURT
                              ? "Hardship Review - Crown Court"
                              : "Hardship Review - Magistrate"
                )
                .result(newHardship.getReviewResult())
                .assessmentDate(newHardship.getReviewDate()).build();

        // TODO: Looks like this DTO might be missing a field - modified_date

        updateAssessmentSummary(application, hardshipSummary);

        return application;

    }

    public ApplicationDTO update(ApplicationDTO application) {

        return application;
    }

    private void calculateContribution(ApplicationDTO application) {
        ApiMaatCalculateContributionRequest calculateContributionRequest = contributionMapper.fromDto(application);
        ApiMaatCalculateContributionResponse calculateContributionResponse =
                contributionApiService.calculate(calculateContributionRequest);
        contributionMapper.toDto(calculateContributionResponse, application);
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
