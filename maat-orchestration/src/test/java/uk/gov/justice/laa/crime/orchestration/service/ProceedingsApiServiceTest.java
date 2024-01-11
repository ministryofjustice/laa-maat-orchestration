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
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.service.api.ProceedingsApiService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProceedingsApiServiceTest {

    @Mock
    private RestAPIClient crownCourtApiClient;

    @InjectMocks
    private ProceedingsApiService proceedingsApiService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenCrownCourtApplicationIsUpdated() {
        proceedingsApiService.updateApplication(new ApiUpdateApplicationRequest());

        verify(crownCourtApiClient)
                .put(any(ApiUpdateApplicationRequest.class), any(), anyString(), anyMap());
    }
}
