package uk.gov.justice.laa.crime.orchestration.service;

import static uk.gov.justice.laa.crime.enums.CurrentStatus.COMPLETE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CaseDetailDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.InitialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.ContributionApiService;

import java.util.List;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionMapper contributionMapper;
    private final ContributionApiService contributionApiService;
    private final MaatCourtDataService maatCourtDataService;

    public ApplicationDTO calculate(WorkflowRequest request) {

        ApplicationDTO application = request.getApplicationDTO();
        if (isRecalculationRequired(application)) {

            ApiMaatCalculateContributionRequest calculateContributionRequest =
                    contributionMapper.workflowRequestToMaatCalculateContributionRequest(request);

            ApiMaatCalculateContributionResponse calculateContributionResponse =
                    contributionApiService.calculate(calculateContributionRequest);

            if (calculateContributionResponse != null && null != calculateContributionResponse.getContributionId()) {
                if (calculateContributionResponse.getContributionId() != null) {
                    application
                            .getCrownCourtOverviewDTO()
                            .setContribution(contributionMapper.maatCalculateContributionResponseToContributionsDto(
                                    calculateContributionResponse));
                }

                if (Boolean.TRUE.equals(calculateContributionResponse.getProcessActivity())) {
                    // invoke MATRIX stored procedure
                    application = maatCourtDataService.invokeStoredProcedure(
                            application, request.getUserDTO(), StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE);
                }
                List<ApiContributionSummary> contributionSummaries =
                        contributionApiService.getContributionSummary(application.getRepId());
                application
                        .getCrownCourtOverviewDTO()
                        .setContributionSummary(contributionMapper.contributionSummaryToDto(contributionSummaries));
            }
        }

        return application;
    }

    protected boolean isRecalculationRequired(final ApplicationDTO applicationDTO) {
        PassportedDTO passportedDTO = applicationDTO.getPassportedDTO();
        if (passportedDTO != null
                && passportedDTO.getAssessementStatusDTO() != null
                && COMPLETE.getStatus()
                        .equals(passportedDTO.getAssessementStatusDTO().getStatus())) {
            return true;
        }

        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        if (assessmentDTO != null) {
            FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
            if (financialAssessmentDTO != null) {
                InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
                if (initialAssessmentDTO != null
                        && initialAssessmentDTO.getAssessmnentStatusDTO() != null
                        && COMPLETE.getStatus()
                                .equals(initialAssessmentDTO
                                        .getAssessmnentStatusDTO()
                                        .getStatus())) {
                    CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
                    String initResult = initialAssessmentDTO.getResult();
                    boolean isFullComplete = financialAssessmentDTO.getFull() != null
                            && financialAssessmentDTO.getFull().getAssessmnentStatusDTO() != null
                            && COMPLETE.getStatus()
                                    .equals(financialAssessmentDTO
                                            .getFull()
                                            .getAssessmnentStatusDTO()
                                            .getStatus());
                    return isFullComplete
                            || InitAssessmentResult.PASS.getResult().equals(initResult)
                            || (caseDetailsDTO != null
                                    && CaseType.APPEAL_CC.getCaseType().equals(caseDetailsDTO.getCaseType())
                                    && InitAssessmentResult.FAIL.getResult().equals(initResult));
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
