package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        ApiFindHardshipResponse expected = new ApiFindHardshipResponse();
        when(hardshipApiClient.get(any(), anyString(), anyInt())).thenReturn(expected);

        hardshipApiService.getHardship(TestModelDataBuilder.HARDSHIP_ID);
        verify(hardshipApiClient).get(any(), anyString(), anyInt());
    }
}
