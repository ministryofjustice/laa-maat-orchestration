package uk.gov.justice.laa.maat.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.maat.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.maat.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.maat.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.maat.orchestration.model.ApiFindHardshipResponse;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HardshipServiceTest {

    @Mock
    private RestAPIClient hardshipApiClient;
    @InjectMocks
    private HardshipService hardshipService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenValidHardshipReviewId_whenFindIsInvoked_thenHardshipIsRetrieved() {
        ApiFindHardshipResponse expected = new ApiFindHardshipResponse();
        when(hardshipApiClient.get(any(), anyString(), anyInt())).thenReturn(expected);

        hardshipService.getHardship(TestModelDataBuilder.HARDSHIP_ID);
        verify(hardshipApiClient).get(any(), anyString(), anyInt());
    }
}
