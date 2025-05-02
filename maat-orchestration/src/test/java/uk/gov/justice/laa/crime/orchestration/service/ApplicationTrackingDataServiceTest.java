package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.service.api.ApplicationTrackingApiService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class ApplicationTrackingDataServiceTest {

    @Mock
    private ApplicationTrackingApiService applicationTrackingApiService;

    @InjectMocks
    private ApplicationTrackingDataService applicationTrackingDataService;

    @Test
    void givenAValidInput_whenHandleCrimeApplyResultIsInvoked_thenCATServiceIsCalled() {
        applicationTrackingDataService.handleCrimeApplyResult(new ApplicationTrackingOutputResult());
        verify(applicationTrackingApiService).handleCrimeApplyResult(any(ApplicationTrackingOutputResult.class));
    }

}