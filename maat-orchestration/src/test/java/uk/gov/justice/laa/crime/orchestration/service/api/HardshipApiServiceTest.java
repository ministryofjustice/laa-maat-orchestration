package uk.gov.justice.laa.crime.orchestration.service.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.orchestration.client.HardshipApiClient;
import uk.gov.justice.laa.crime.orchestration.data.Constants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HardshipApiServiceTest {

    @Mock
    private HardshipApiClient hardshipApiClient;

    @InjectMocks
    private HardshipApiService hardshipApiService;

    @Test
    void givenValidHardshipReviewId_whenFindIsInvoked_thenHardshipIsRetrieved() {
        hardshipApiService.find(Constants.HARDSHIP_REVIEW_ID);

        verify(hardshipApiClient).getHardshipReview(anyInt());
    }

    @Test
    void givenValidHardshipDto_whenCreateIsInvoked_thenHardshipIsPersisted() {
        hardshipApiService.create(new ApiPerformHardshipRequest());

        verify(hardshipApiClient).createHardshipReview(any(ApiPerformHardshipRequest.class));
    }

    @Test
    void givenValidHardshipDto_whenUpdateIsInvoked_thenHardshipIsPersisted() {
        hardshipApiService.update(new ApiPerformHardshipRequest());

        verify(hardshipApiClient).updateHardshipReview(any(ApiPerformHardshipRequest.class));
    }

    @Test
    void givenValidHardshipDto_whenRollbackIsInvoked_thenHardshipIsPersisted() {
        hardshipApiService.rollback(Constants.HARDSHIP_REVIEW_ID.longValue());
        verify(hardshipApiClient).rollback(anyLong());
    }
}
