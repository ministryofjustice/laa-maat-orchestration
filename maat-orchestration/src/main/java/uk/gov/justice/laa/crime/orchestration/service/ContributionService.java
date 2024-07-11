package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;

import uk.gov.justice.laa.crime.orchestration.service.api.ContributionApiService;

import java.util.List;

import static uk.gov.justice.laa.crime.enums.CurrentStatus.COMPLETE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionMapper contributionMapper;
    private final ContributionApiService contributionApiService;
    private final MaatCourtDataService maatCourtDataService;

    public ApplicationDTO calculate(WorkflowRequest request) {
        log.info("start ContributionService.calculate --->");
        ApplicationDTO application = request.getApplicationDTO();
        if (isRecalculationRequired(application)) {
            log.info("---isRecalculationRequired() --->");
            ApiMaatCalculateContributionRequest calculateContributionRequest =
                    contributionMapper.workflowRequestToMaatCalculateContributionRequest(request);
            log.info("..Before Calling ContributionService.calculate()..");
            ApiMaatCalculateContributionResponse calculateContributionResponse =
                    contributionApiService.calculate(calculateContributionRequest);
            log.info("calculateContributionResponse --->" +calculateContributionResponse);
            if (calculateContributionResponse != null && null != calculateContributionResponse.getContributionId()) {
                if (calculateContributionResponse.getContributionId() != null) {
                    application.getCrownCourtOverviewDTO().setContribution(
                            contributionMapper.maatCalculateContributionResponseToContributionsDto(
                                    calculateContributionResponse)
                    );
                }
                log.info("ProcessActivity --->" + calculateContributionResponse.getProcessActivity());
                if (Boolean.TRUE.equals(calculateContributionResponse.getProcessActivity())) {
                    // invoke MATRIX stored procedure
                    application = maatCourtDataService.invokeStoredProcedure(
                            application, request.getUserDTO(), StoredProcedure.PROCESS_ACTIVITY
                    );
                }
                log.info("completed ProcessActivity --->");
                List<ApiContributionSummary> contributionSummaries =
                        contributionApiService.getContributionSummary(application.getRepId());
                application.getCrownCourtOverviewDTO().setContributionSummary(
                        contributionMapper.contributionSummaryToDto(contributionSummaries)
                );
                log.info("calling GET_APPLICATION_CORRESPONDENCE --->");
                // correspondence_pkg.get_application_correspondence
                application = maatCourtDataService.invokeStoredProcedure(
                        application, request.getUserDTO(), StoredProcedure.GET_APPLICATION_CORRESPONDENCE
                );
                log.info("Completed GET_APPLICATION_CORRESPONDENCE --->");
            }
        }
        log.info("End ContributionService.calculate --->");
        return application;
    }

    protected boolean isRecalculationRequired(final ApplicationDTO applicationDTO) {
        PassportedDTO passportedDTO = applicationDTO.getPassportedDTO();
        if (passportedDTO != null && passportedDTO.getAssessementStatusDTO() != null &&
                COMPLETE.getStatus().equals(passportedDTO.getAssessementStatusDTO().getStatus())) {
            return true;
        }

        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        if (assessmentDTO != null) {
            FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
            if (financialAssessmentDTO != null) {
                InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
                if (initialAssessmentDTO != null && initialAssessmentDTO.getAssessmnentStatusDTO() != null &&
                        COMPLETE.getStatus().equals(initialAssessmentDTO.getAssessmnentStatusDTO().getStatus())) {
                    CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
                    String initResult = initialAssessmentDTO.getResult();
                    boolean isFullComplete = financialAssessmentDTO.getFull() != null &&
                            financialAssessmentDTO.getFull().getAssessmnentStatusDTO() != null &&
                            COMPLETE.getStatus().equals(financialAssessmentDTO.getFull().getAssessmnentStatusDTO().getStatus());
                    return isFullComplete || InitAssessmentResult.PASS.getResult().equals(initResult) ||
                            (caseDetailsDTO != null && CaseType.APPEAL_CC.getCaseType()
                                    .equals(caseDetailsDTO.getCaseType()) &&
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
