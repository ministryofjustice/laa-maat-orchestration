package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtContributionsApiClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionApiService {

    private final CrownCourtContributionsApiClient contributionApiClient;
    private static final String RESPONSE_STRING = "Response from Contribution Service: {}";
    private static final String REQUEST_STRING = "Request to Contribution Service: {}";

    public ApiMaatCalculateContributionResponse calculate(ApiMaatCalculateContributionRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiMaatCalculateContributionResponse response = contributionApiClient.calculate(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public Boolean isContributionRule(ApiMaatCheckContributionRuleRequest request) {
        log.debug(REQUEST_STRING, request);
        Boolean response = contributionApiClient.isContributionRule(request);
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public List<ApiContributionSummary> getContributionSummary(Long repId) {
        List<ApiContributionSummary> response = contributionApiClient.getContributionSummary(repId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }
}
