package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.client.HardshipApiClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipApiService {
    
    private final HardshipApiClient hardshipApiClient;
    private static final String RESPONSE_STRING = "Response from Hardship Service: {}";
    private static final String REQUEST_STRING = "Request to Hardship Service: {}";

    public ApiFindHardshipResponse find(Integer hardshipReviewId) {
        log.info(REQUEST_STRING, hardshipReviewId);
        ApiFindHardshipResponse response = hardshipApiClient.getHardshipReview(hardshipReviewId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public ApiPerformHardshipResponse create(ApiPerformHardshipRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiPerformHardshipResponse response = hardshipApiClient.createHardshipReview(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public ApiPerformHardshipResponse update(ApiPerformHardshipRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiPerformHardshipResponse response = hardshipApiClient.updateHardshipReview(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public void rollback(Integer hardshipReviewId) {
        log.info(REQUEST_STRING, hardshipReviewId);
        hardshipApiClient.rollback(hardshipReviewId);
    }

}
