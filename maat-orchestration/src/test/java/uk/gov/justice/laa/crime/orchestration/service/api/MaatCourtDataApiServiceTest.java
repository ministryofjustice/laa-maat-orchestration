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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataApiServiceTest {

    @Mock
    private RestAPIClient restAPIClient;

    @InjectMocks
    private MaatCourtDataApiService maatCourtDataApiService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenValidRequest_whenExecuteStoredProcedureIsInvoked_thenApplicationIsReturned() {
        maatCourtDataApiService.executeStoredProcedure(new StoredProcedureRequest());

        verify(restAPIClient)
                .post(any(StoredProcedureRequest.class), any(), anyString(), anyMap());
    }

    @Test
    void givenValidRequest_whenGetRepOrderByRepIdIsInvoked_thenRepOrderDTOIsReturned() {
        maatCourtDataApiService.getRepOrderByRepId(1000);

        verify(restAPIClient)
                .get(any(), any(), anyInt());
    }

    @Test
    void givenValidRequest_whenGetHardshipIsInvoked_thenApiFindHardshipResponseIsReturned() {
        maatCourtDataApiService.getHardship(1000);

        verify(restAPIClient)
                .get(any(), any(), anyInt());
    }

    @Test
    void givenValidRequest_whenGetFinancialAssessmentIsInvoked_thenFinancialAssessmentDTOIsReturned() {
        maatCourtDataApiService.getFinancialAssessment(1000);

        verify(restAPIClient)
                .get(any(), any(), anyInt());
    }
}
