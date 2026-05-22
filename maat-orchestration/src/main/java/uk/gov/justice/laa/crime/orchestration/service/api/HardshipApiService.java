package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.client.HardshipApiClient;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HardshipApiService {

    private final HardshipApiClient hardshipApiClient;

    public ApiFindHardshipResponse find(Integer hardshipReviewId) {
        return hardshipApiClient.getHardshipReview(hardshipReviewId);
    }

    public ApiPerformHardshipResponse create(ApiPerformHardshipRequest request) {
        return hardshipApiClient.createHardshipReview(request);
    }

    public ApiPerformHardshipResponse update(ApiPerformHardshipRequest request) {
        return hardshipApiClient.updateHardshipReview(request);
    }

    public void rollback(Integer hardshipReviewId) {
        hardshipApiClient.rollback(hardshipReviewId);
    }
}
