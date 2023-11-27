package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipRequest;

import static org.mockito.ArgumentMatchers.*;
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
        hardshipApiService.find(Constants.TEST_HARDSHIP_ID);

        verify(hardshipApiClient)
                .get(any(), anyString(), anyInt());
    }

    @Test
    void givenValidHardshipDto_whenCreateIsInvoked_thenHardshipIsPersisted() {
        hardshipApiService.create(new ApiPerformHardshipRequest());

        verify(hardshipApiClient)
                .post(any(ApiPerformHardshipRequest.class), any(), anyString(), anyMap());
    }
}
