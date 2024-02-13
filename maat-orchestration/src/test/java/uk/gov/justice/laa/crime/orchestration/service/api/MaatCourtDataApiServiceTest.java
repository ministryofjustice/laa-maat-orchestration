package uk.gov.justice.laa.crime.orchestration.service.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataApiServiceTest {

    @Mock
    private RestAPIClient cmaApiClient;

    @InjectMocks
    private MaatCourtDataApiService maatCourtDataApiService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenValidRequest_whenExecuteStoredProcedureIsInvoked_thenApplicationIsReturned() {
        maatCourtDataApiService.executeStoredProcedure(new StoredProcedureRequest());
        verify(cmaApiClient)
                .post(any(StoredProcedureRequest.class), any(), anyString(), anyMap());
    }

    @Test
    void givenValidRequest_whenGetRepOrderByRepIdIsInvoked_thenRepOrderDTOIsReturned() {
        maatCourtDataApiService.getRepOrderByRepId(1000);
        verify(cmaApiClient)
                .get(any(), any(), anyInt());
    }

    @Test
    void givenValidRequest_whenGetUserSummaryIsInvoked_thenUserSummaryDTOIsReturned() {
        maatCourtDataApiService.getUserSummary("test");
        verify(cmaApiClient)
                .get(any(), any(), anyString());
    }

    @Test
    void givenValidRequest_whenUpdateApplicantByIdIsInvoked_thenApplicationDTOIsReturned() {
        maatCourtDataApiService.updateApplicantById(1000, new HashMap<String, Object>());
        verify(cmaApiClient)
                .patch(any(HashMap.class), any(), anyString(), anyMap(), any());
    }

    @Test
    void givenValidRequest_whenUpdateApplicantHistoryByIdIsInvoked_thenApplicationDTOIsReturned() {
        maatCourtDataApiService.updateApplicantHistoryById(1000, new HashMap<String, Object>());
        verify(cmaApiClient)
                .patch(any(HashMap.class), any(), anyString(), anyMap(), any());
    }

    @Test
    void givenValidRequest_whenUpdateRepOrderByRepIdIsInvoked_thenApplicationDTOIsReturned() {
        maatCourtDataApiService.updateRepOrderByRepId(1000, new HashMap<String, Object>());
        verify(cmaApiClient)
                .patch(any(HashMap.class), any(), anyString(), anyMap(), any());
    }
}
