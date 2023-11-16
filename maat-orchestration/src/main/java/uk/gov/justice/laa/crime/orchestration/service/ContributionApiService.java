package uk.gov.justice.laa.crime.orchestration.service;

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

import java.util.Collections;

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
                configuration.getContributionApi().getContributionEndpoints().getCalculateContributionUrl(),
                Collections.emptyMap()
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public Boolean isContributionRule(ApiMaatCheckContributionRuleRequest request) {
        Boolean response = contributionApiClient.put(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getContributionApi().getContributionEndpoints().getCheckContributionRule(),
                Collections.emptyMap()
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}
