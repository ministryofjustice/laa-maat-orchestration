package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.orchestration.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.orchestration.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;


import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipApiService {

    @Qualifier("hardshipApiClient")
    private final RestAPIClient hardshipApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Hardship Service: {}";
    private static final String REQUEST_STRING = "Request to Hardship Service: {}";

    public ApiFindHardshipResponse find(Integer hardshipReviewId) {
        log.info(REQUEST_STRING, hardshipReviewId);
        ApiFindHardshipResponse response = hardshipApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getHardshipApi().getEndpoints().getFindUrl(),
                hardshipReviewId
        );

        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ApiPerformHardshipResponse create(ApiPerformHardshipRequest request) {
        log.info(REQUEST_STRING, request);
        ApiPerformHardshipResponse response = hardshipApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getHardshipApi().getEndpoints().getCreateUrl(),
                Collections.emptyMap()
        );

        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ApiPerformHardshipResponse update(ApiPerformHardshipRequest request) {
        log.info(REQUEST_STRING, request);
        ApiPerformHardshipResponse response = hardshipApiClient.put(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getHardshipApi().getEndpoints().getUpdateUrl(),
                Collections.emptyMap()
        );

        log.info(RESPONSE_STRING, response);
        return response;
    }

    public void rollback(Long hardshipReviewId) {
        log.info(REQUEST_STRING, hardshipReviewId);
        hardshipApiClient.patch(
                "",
                new ParameterizedTypeReference<>() {
                },
                configuration.getHardshipApi().getEndpoints().getRollbackUrl(),
                Collections.emptyMap(),
                hardshipReviewId
        );
    }

}
