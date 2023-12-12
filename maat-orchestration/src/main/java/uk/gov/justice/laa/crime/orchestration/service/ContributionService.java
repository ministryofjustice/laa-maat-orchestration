package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.common.ApiContributionSummary;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    public static final String COMPLETE = "COMPLETE";
    private final ContributionMapper contributionMapper;
    private final ContributionApiService contributionApiService;
    private final MaatCourtDataApiService maatCourtDataApiService;

    public ApplicationDTO calculateContribution(WorkflowRequest request) {
        ApplicationDTO application = request.getApplicationDTO();
        if (isCalculateContributionReqd(application)) {
            ApiMaatCalculateContributionRequest calculateContributionRequest =
                    contributionMapper.workflowRequestToMaatCalculateContributionRequest(request);
            ApiMaatCalculateContributionResponse calculateContributionResponse =
                    contributionApiService.calculate(calculateContributionRequest);
            if (calculateContributionResponse != null) {
                if (calculateContributionResponse.getContributionId() != null) {
                    application.getCrownCourtOverviewDTO().setContribution(
                            contributionMapper.maatCalculateContributionResponseToContributionsDto(calculateContributionResponse)
                    );
                }
                if (Boolean.TRUE.equals(calculateContributionResponse.getProcessActivity())) {
                    // invoke MATRIX stored procedure
                }
                List<ApiContributionSummary> contributionSummaries = contributionApiService.getContributionSummary(application.getRepId());
                application.getCrownCourtOverviewDTO().setContributionSummary(
                        contributionMapper.contributionSummaryToDto(contributionSummaries)
                );
                // correspondence_pkg.get_application_correspondence
            }
        }
        return application;
    }

    protected boolean isCalculateContributionReqd(final ApplicationDTO applicationDTO) {
        PassportedDTO passportedDTO = applicationDTO.getPassportedDTO();
        if (passportedDTO != null && passportedDTO.getAssessementStatusDTO() != null &&
                COMPLETE.equals(passportedDTO.getAssessementStatusDTO().getStatus())) {
            return true;
        }

        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        if (assessmentDTO != null) {
            FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
            if (financialAssessmentDTO != null) {
                InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
                if (initialAssessmentDTO != null && initialAssessmentDTO.getAssessmnentStatusDTO() != null &&
                        COMPLETE.equals(initialAssessmentDTO.getAssessmnentStatusDTO().getStatus())) {
                    CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
                    String initResult = initialAssessmentDTO.getResult();
                    boolean isFullComplete = financialAssessmentDTO.getFull() != null &&
                            financialAssessmentDTO.getFull().getAssessmnentStatusDTO() != null &&
                            COMPLETE.equals(financialAssessmentDTO.getFull().getAssessmnentStatusDTO().getStatus());
                    return isFullComplete || InitAssessmentResult.PASS.getResult().equals(initResult) ||
                            (caseDetailsDTO != null && CaseType.APPEAL_CC.getCaseType().equals(caseDetailsDTO.getCaseType()) &&
                                    InitAssessmentResult.FAIL.getResult().equals(initResult));
                }
            }
        }
        return false;
    }

    public boolean isVariationRequired(ApplicationDTO application) {
        ApiMaatCheckContributionRuleRequest apiMaatCheckContributionRuleRequest =
                contributionMapper.applicationDtoToCheckContributionRuleRequest(application);
        return contributionApiService.isContributionRule(apiMaatCheckContributionRuleRequest);
    }
}
