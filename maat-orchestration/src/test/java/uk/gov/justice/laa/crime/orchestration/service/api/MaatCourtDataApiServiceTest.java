package uk.gov.justice.laa.crime.orchestration.service.api;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.SendToCCLFDTO;

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
    void givenValidRequest_whenUpdateSendToCCLFIsInvoked_thenApplicationDTOIsReturned() {
        maatCourtDataApiService.updateSendToCCLF(SendToCCLFDTO.builder().build());
        verify(cmaApiClient)
                .put(any(), any(), anyString(), anyMap());
    }

    @Test
    void givenValidRequest_whenGetFinancialAssessmentIsInvoked_thenFinancialAssessmentDTOIsReturned() {
        maatCourtDataApiService.getFinancialAssessment(1000);
        verify(cmaApiClient)
                .get(any(), any(), anyInt());
    }

    @Test
    void givenValidRequest_whenUpdateFinancialAssessmentIsInvoked_thenMaatApiAssessmentResponseIsReturned() {
        maatCourtDataApiService.updateFinancialAssessment(new MaatApiUpdateAssessment());
        verify(cmaApiClient).put(any(MaatApiUpdateAssessment.class), any(), any(), any());
    }

    @Test
    void givenValidRequest_whenPatchRepOrderIsInvoked_thenIsUpdated() {
        int repOrderId = 1234;
        LocalDateTime dateModified = LocalDateTime.now();
        Map<String, Object> fieldsToUpdate = Map.of("dateModified", dateModified);

        maatCourtDataApiService.patchRepOrder(1234, fieldsToUpdate);
        verify(cmaApiClient).patch(eq(fieldsToUpdate), any(), anyString(), anyMap(), eq(repOrderId));
    }
}
