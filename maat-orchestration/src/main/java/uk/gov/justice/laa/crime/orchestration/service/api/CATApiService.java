package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CATApiService {

    private static final String REQUEST_STRING = "Request to Application Tracking Service: {}";
    @Qualifier("catApiClient")
    private final RestAPIClient catApiClient;
    private final ServicesConfiguration configuration;

    public void handleCrimeApplyResult(ApplicationTrackingOutputResult request) {
        log.debug(REQUEST_STRING, request);
        catApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getCatApi().getEndpoints().getHandleCrimeApplyUrl(),
                Collections.emptyMap()
        );
    }
}
