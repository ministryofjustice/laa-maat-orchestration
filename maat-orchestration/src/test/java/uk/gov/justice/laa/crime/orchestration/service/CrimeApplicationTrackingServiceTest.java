package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.service.api.CrimeApplicationTrackingApiService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class CrimeApplicationTrackingServiceTest {

    @Mock
    private CrimeApplicationTrackingApiService crimeApplicationTrackingApiService;

    @InjectMocks
    private CrimeApplicationTrackingService crimeApplicationTrackingService;

    @Test
    void givenAValidInput_whenSendApplicationTrackingDataIsInvoked_thenTrackingServiceIsCalled() {
        crimeApplicationTrackingService.sendApplicationTrackingData(TestModelDataBuilder.getApplicationTrackingOutputResult());
        verify(crimeApplicationTrackingApiService).sendApplicationTrackingData(any(ApplicationTrackingOutputResult.class));
    }

    @Test
    void givenAUSNIsEmpty_whenSendApplicationTrackingDataIsInvoked_thenShouldNotTrackingServiceCalled() {
        crimeApplicationTrackingService.sendApplicationTrackingData(new ApplicationTrackingOutputResult());
        verify(crimeApplicationTrackingApiService, times(0)).sendApplicationTrackingData(any(ApplicationTrackingOutputResult.class));
    }

}