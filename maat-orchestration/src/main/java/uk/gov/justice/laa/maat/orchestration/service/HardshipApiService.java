package uk.gov.justice.laa.maat.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.maat.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.maat.orchestration.model.ApiFindHardshipResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipApiService {

    @Qualifier("hardshipApiClient")
    private final RestAPIClient hardshipApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Hardship Service: %s";

    public ApiFindHardshipResponse getHardship(Integer hardshipReviewId) {
        ApiFindHardshipResponse response = hardshipApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getHardshipApi().getHardshipEndpoints().getFindUrl(),
                hardshipReviewId
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

}
