package uk.gov.justice.laa.crime.orchestration.service.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class CATApiServiceTest {


    @Mock
    private RestAPIClient catApiClient;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @InjectMocks
    private CATApiService catApiService;

    @Test
    void givenValidRequest_whenHandleCrimeApplyResultIsInvoked_thenApplicationTrackingServiceIsCalled() {
        catApiService.handleCrimeApplyResult(new ApplicationTrackingOutputResult());
        verify(catApiClient).post(any(ApplicationTrackingOutputResult.class), any(), anyString(), anyMap());
    }

}