package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;


import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProceedingsApiService {

    @Qualifier("ccpApiClient")
    private final RestAPIClient crownCourtApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Proceedings Service: {}";
    private static final String REQUEST_STRING = "Request to Proceedings Service: {}";

    public ApiUpdateApplicationResponse updateApplication(ApiUpdateApplicationRequest request) {
        log.info(REQUEST_STRING, request);
        ApiUpdateApplicationResponse response = crownCourtApiClient.put(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getCrownCourtApi().getEndpoints().getUpdateApplicationUrl(),
                Collections.emptyMap()
        );

        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ApiUpdateCrownCourtOutcomeResponse updateCrownCourt(ApiUpdateApplicationRequest request) {
        log.info(REQUEST_STRING, request);
        ApiUpdateCrownCourtOutcomeResponse response = crownCourtApiClient.put(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getCrownCourtApi().getEndpoints().getUpdateCrownCourtUrl(),
                Collections.emptyMap()
        );

        log.info(RESPONSE_STRING, response);
        return response;
    }
}
