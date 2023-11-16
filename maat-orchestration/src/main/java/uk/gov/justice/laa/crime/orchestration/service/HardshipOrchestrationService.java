package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.ProceedingsMapper;
import uk.gov.justice.laa.crime.orchestration.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationResponse;
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

    private final HardshipMapper hardshipMapper;
    private final ContributionMapper contributionMapper;
    private final ProceedingsMapper proceedingsMapper;


    public static final List<CaseType> CC_CASE_TYPES =
            List.of(CaseType.INDICTABLE, CaseType.CC_ALREADY, CaseType.APPEAL_CC, CaseType.COMMITAL);

    public static final List<MagCourtOutcome> MAG_COURT_OUTCOMES =
            List.of(MagCourtOutcome.COMMITTED_FOR_TRIAL, MagCourtOutcome.SENT_FOR_TRIAL,
                    MagCourtOutcome.COMMITTED, MagCourtOutcome.APPEAL_TO_CC
            );

    public HardshipReviewDTO find(int hardshipReviewId) {
        ApiFindHardshipResponse hardship = hardshipApiService.getHardship(hardshipReviewId);
        return hardshipMapper.findHardshipResponseToHardshipDto(hardship);
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

        ApiPerformHardshipRequest createRequest = hardshipMapper.workflowRequestToPerformHardshipRequest(request);
        ApiPerformHardshipResponse response = hardshipApiService.create(createRequest);

        // Need to refresh from DB as HardshipDetail ids may have changed
        // This information is not currently captured in the response from the Hardship service
        ApiFindHardshipResponse hardship = hardshipApiService.getHardship(response.getHardshipReviewId());
        HardshipReviewDTO newHardship = hardshipMapper.findHardshipResponseToHardshipDto(hardship);

        if (courtType == CourtType.MAGISTRATE) {
            hardshipOverview.setMagCourtHardship(newHardship);
            // TODO: Call assessments.determine_mags_rep_decision stored procedure
            boolean isVariationRequired = false;
            // Based on calling contribution.contribution_rule_applies (in the process of being migrated to C3)
            if (isVariationRequired) {
                calculateContribution(request);
            }
        } else {
            hardshipOverview.setCrownCourtHardship(newHardship);
            calculateContribution(request);

            // TODO: Call application.pre_update checks stored procedure

            ApiUpdateApplicationRequest apiUpdateApplicationRequest =
                    proceedingsMapper.workflowRequestToUpdateApplicationRequest(request);
            ApiUpdateApplicationResponse updateApplicationResponse =
                    crownCourtApiService.update(apiUpdateApplicationRequest);
            application =
                    proceedingsMapper.updateApplicationResponseToApplicationDto(updateApplicationResponse, application);

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

    private void calculateContribution(WorkflowRequest request) {
        ApiMaatCalculateContributionRequest calculateContributionRequest =
                contributionMapper.workflowRequestToMaatCalculateContributionRequest(request);
        ApiMaatCalculateContributionResponse calculateContributionResponse =
                contributionApiService.calculate(calculateContributionRequest);
        ContributionsDTO contributionsDTO =
                contributionMapper.maatCalculateContributionResponseToContributionsDto(calculateContributionResponse);
        request.getApplicationDTO().getCrownCourtOverviewDTO().setContribution(contributionsDTO);
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
