package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.client.HardshipApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipApiService {

    private final HardshipApiClient hardshipApiClient;
    private static final String REQUEST_STRING = "Request to Hardship Service: {}";

    public ApiFindHardshipResponse find(Integer hardshipReviewId) {
        log.info(REQUEST_STRING, hardshipReviewId);
        return hardshipApiClient.getHardshipReview(hardshipReviewId);
    }

    public ApiPerformHardshipResponse create(ApiPerformHardshipRequest request) {
        log.debug(REQUEST_STRING, request);
        return hardshipApiClient.createHardshipReview(request);
    }

    public ApiPerformHardshipResponse update(ApiPerformHardshipRequest request) {
        log.debug(REQUEST_STRING, request);
        return hardshipApiClient.updateHardshipReview(request);
    }

    public void rollback(Integer hardshipReviewId) {
        log.info(REQUEST_STRING, hardshipReviewId);
        hardshipApiClient.rollback(hardshipReviewId);
    }
}
