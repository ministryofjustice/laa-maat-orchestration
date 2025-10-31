package uk.gov.justice.laa.crime.orchestration.service.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.client.ApplicationTrackingApiClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class ApplicationTrackingApiServiceTest {

    @Mock
    private ApplicationTrackingApiClient applicationTrackingApiClient;

    @InjectMocks
    private ApplicationTrackingApiService applicationTrackingApiService;

    @Test
    void givenValidRequest_whenHandleCrimeApplyResultIsInvoked_thenApplicationTrackingServiceIsCalled() {
        applicationTrackingApiService.sendTrackingOutputResult(new ApplicationTrackingOutputResult());
        verify(applicationTrackingApiClient).sendTrackingOutputResult(any(ApplicationTrackingOutputResult.class));
    }
}
