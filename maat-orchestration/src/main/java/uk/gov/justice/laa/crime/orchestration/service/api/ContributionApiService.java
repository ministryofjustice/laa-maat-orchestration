package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.common.ApiContributionSummary;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionApiService {

    @Qualifier("cccApiClient")
    private final RestAPIClient contributionApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Contribution Service: %s";

    public ApiMaatCalculateContributionResponse calculate(ApiMaatCalculateContributionRequest request) {
        ApiMaatCalculateContributionResponse response = contributionApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getContributionApi().getEndpoints().getCalculateContributionUrl(),
                Collections.emptyMap()
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public Boolean isContributionRule(ApiMaatCheckContributionRuleRequest request) {
        Boolean response = contributionApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getContributionApi().getEndpoints().getCheckContributionRuleUrl(),
                Collections.emptyMap()
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public List<ApiContributionSummary> getContributionSummary(Long repId) {
        List<ApiContributionSummary> response = contributionApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getContributionApi().getEndpoints().getContributionSummariesUrl(),
                Collections.emptyMap(),
                repId
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}
