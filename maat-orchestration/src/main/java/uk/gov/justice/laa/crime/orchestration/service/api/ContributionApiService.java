package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtContributionsApiClient;

import java.util.List;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionApiService {

    private final CrownCourtContributionsApiClient contributionApiClient;
    private static final String REQUEST_STRING = "Request to Contribution Service: {}";

    public ApiMaatCalculateContributionResponse calculate(ApiMaatCalculateContributionRequest request) {
        log.debug(REQUEST_STRING, request);
        return contributionApiClient.calculateContribution(request);
    }

    public Boolean isContributionRule(ApiMaatCheckContributionRuleRequest request) {
        log.debug(REQUEST_STRING, request);
        return contributionApiClient.isContributionRule(request);
    }

    public List<ApiContributionSummary> getContributionSummary(Long repId) {
        return contributionApiClient.getContributionSummary(repId);
    }
}
