package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.model.application_tracking.ApiCrimeApplicationTrackingRequest;
import uk.gov.justice.laa.crime.orchestration.service.api.CATApiService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class CATDataServiceTest {

    @Mock
    private CATApiService catApiService;

    @InjectMocks
    private CATDataService catDataService;

    @Test
    void givenAValidInput_whenHandleEformResultIsInvoked_thenCATServiceIsCalled() {
        catDataService.handleEformResult(new ApiCrimeApplicationTrackingRequest());
        verify(catApiService).handleEformResult(any(ApiCrimeApplicationTrackingRequest.class));
    }

}