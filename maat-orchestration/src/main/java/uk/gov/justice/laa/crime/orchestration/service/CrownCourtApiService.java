package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationResponse;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrownCourtApiService {

    @Qualifier("ccpApiClient")
    private final RestAPIClient crownCourtApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Crown Court API Service: %s";

    public ApiUpdateApplicationResponse update(ApiUpdateApplicationRequest request) {
        ApiUpdateApplicationResponse response = crownCourtApiClient.put(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getCrownCourtApi().getCrownCourtEndpoints().getUpdateUrl(),
                Collections.emptyMap()
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}