package uk.gov.justice.laa.crime.orchestration.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.service.api.ApplicationTrackingApiService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class ApplicationTrackingDataServiceTest {

    @Mock
    private ApplicationTrackingApiService applicationTrackingApiService;

    @InjectMocks
    private ApplicationTrackingDataService applicationTrackingDataService;

    @Test
    void givenAValidInput_whenSendTrackingOutputResultIsInvoked_thenApplicationTrackingApiServiceIsCalled() {
        applicationTrackingDataService.sendTrackingOutputResult(new ApplicationTrackingOutputResult());
        verify(applicationTrackingApiService).sendTrackingOutputResult(any(ApplicationTrackingOutputResult.class));
    }
}
