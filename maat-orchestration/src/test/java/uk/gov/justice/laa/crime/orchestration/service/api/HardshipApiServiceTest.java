package uk.gov.justice.laa.crime.orchestration.service.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.Constants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HardshipApiServiceTest {

    @Mock
    private RestAPIClient hardshipApiClient;

    @InjectMocks
    private HardshipApiService hardshipApiService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenValidHardshipReviewId_whenFindIsInvoked_thenHardshipIsRetrieved() {
        hardshipApiService.find(Constants.HARDSHIP_REVIEW_ID);

        verify(hardshipApiClient)
                .get(any(), anyString(), anyInt());
    }

    @Test
    void givenValidHardshipDto_whenCreateIsInvoked_thenHardshipIsPersisted() {
        hardshipApiService.create(new ApiPerformHardshipRequest());

        verify(hardshipApiClient)
                .post(any(ApiPerformHardshipRequest.class), any(), anyString(), anyMap());
    }

    @Test
    void givenValidHardshipDto_whenUpdateIsInvoked_thenHardshipIsPersisted() {
        hardshipApiService.update(new ApiPerformHardshipRequest());

        verify(hardshipApiClient)
                .put(any(ApiPerformHardshipRequest.class), any(), anyString(), anyMap());
    }

    @Test
    void givenValidHardshipDto_whenRollbackIsInvoked_thenHardshipIsPersisted() {
        hardshipApiService.rollback(Constants.HARDSHIP_REVIEW_ID.longValue());
        verify(hardshipApiClient)
                .patch(any(), any(), anyString(), anyMap(), any());
    }
}
