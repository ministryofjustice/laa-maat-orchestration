package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.mapper.CalculateContributionMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.FindHardshipMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.PerformHardshipMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.UpdateCrownCourtApplicationMapper;
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

    private final HardshipApiService hardshipApiService;
    private final CrownCourtApiService crownCourtApiService;
    private final ContributionApiService contributionApiService;

    private final FindHardshipMapper findHardshipMapper;
    private final PerformHardshipMapper performHardshipMapper;
    private final CalculateContributionMapper calculateContributionMapper;
    private final UpdateCrownCourtApplicationMapper updateCrownCourtApplicationMapper;


    public static final List<CaseType> CC_CASE_TYPES =
            List.of(CaseType.INDICTABLE, CaseType.CC_ALREADY, CaseType.APPEAL_CC, CaseType.COMMITAL);

    public static final List<MagCourtOutcome> MAG_COURT_OUTCOMES =
            List.of(MagCourtOutcome.COMMITTED_FOR_TRIAL, MagCourtOutcome.SENT_FOR_TRIAL,
                    MagCourtOutcome.COMMITTED, MagCourtOutcome.APPEAL_TO_CC
            );

    public HardshipReviewDTO find(int assessmentId) {
        return null;
    }

    public ApplicationDTO create(WorkflowRequestDTO workflowRequest) {
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        CourtType courtType = isCrownCourt(application) ? CourtType.CROWN_COURT : CourtType.MAGISTRATE;
        // Set the courtType, as this will be needed in the mapping logic
        application.setCourtType(courtType);
        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();

        ApiPerformHardshipRequest createRequest = performHardshipMapper.fromDto(workflowRequest);
        ApiPerformHardshipResponse response = hardshipApiService.create(createRequest);

        // Need to refresh from DB as HardshipDetail ids may have changed
        // This information is not currently captured in the response from the Hardship service
        ApiFindHardshipResponse hardship = hardshipApiService.getHardship(response.getHardshipReviewId());
        HardshipReviewDTO newHardship = new HardshipReviewDTO();
        findHardshipMapper.toDto(hardship, newHardship);

        if (courtType == CourtType.MAGISTRATE) {
            hardshipOverview.setMagCourtHardship(newHardship);
            // TODO: Call assessments.determine_mags_rep_decision stored procedure
            boolean isVariationRequired = false;
            // Based on calling contribution.contribution_rule_applies (in the process of being migrated to C3)
            if (isVariationRequired) {
                calculateContribution(workflowRequest);
            }
        } else {
            hardshipOverview.setCrownCourtHardship(newHardship);
            calculateContribution(workflowRequest);

            // TODO: Call application.pre_update checks stored procedure

            ApiUpdateApplicationRequest apiUpdateApplicationRequest =
                    updateCrownCourtApplicationMapper.fromDto(workflowRequest);
            crownCourtApiService.update(apiUpdateApplicationRequest);

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

        // TODO: Looks like this DTO might be missing a field - modified_date

        updateAssessmentSummary(application, hardshipSummary);

        return application;

    }

    public ApplicationDTO update(WorkflowRequestDTO workflowRequest) {

        return workflowRequest.getApplicationDTO();
    }

    private void calculateContribution(WorkflowRequestDTO workflowRequest) {
        ApiMaatCalculateContributionRequest calculateContributionRequest =
                calculateContributionMapper.fromDto(workflowRequest);
        ApiMaatCalculateContributionResponse calculateContributionResponse =
                contributionApiService.calculate(calculateContributionRequest);
        calculateContributionMapper.toDto(calculateContributionResponse, workflowRequest.getApplicationDTO());
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
