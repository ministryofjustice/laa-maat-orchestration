package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtContributionsApiClient;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContributionApiService {

    private final CrownCourtContributionsApiClient contributionApiClient;

    public ApiMaatCalculateContributionResponse calculate(ApiMaatCalculateContributionRequest request) {
        return contributionApiClient.calculateContribution(request);
    }

    public Boolean isContributionRule(ApiMaatCheckContributionRuleRequest request) {
        return contributionApiClient.isContributionRule(request);
    }

    public List<ApiContributionSummary> getContributionSummary(Long repId) {
        return contributionApiClient.getContributionSummary(repId);
    }
}
