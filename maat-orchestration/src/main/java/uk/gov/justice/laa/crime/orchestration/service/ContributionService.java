package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionsDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCheckContributionRuleRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionMapper contributionMapper;
    private final ContributionApiService contributionApiService;

    public ContributionsDTO calculateContribution(WorkflowRequest request) {
        ApiMaatCalculateContributionRequest calculateContributionRequest =
                contributionMapper.workflowRequestToMaatCalculateContributionRequest(request);
        ApiMaatCalculateContributionResponse calculateContributionResponse =
                contributionApiService.calculate(calculateContributionRequest);
        return contributionMapper.maatCalculateContributionResponseToContributionsDto(calculateContributionResponse);
    }

    public boolean isVariationRequired(ApplicationDTO application) {
        ApiMaatCheckContributionRuleRequest apiMaatCheckContributionRuleRequest =
                contributionMapper.applicationDtoToCheckContributionRuleRequest(application);
        return contributionApiService.isContributionRule(apiMaatCheckContributionRuleRequest);
    }
}
