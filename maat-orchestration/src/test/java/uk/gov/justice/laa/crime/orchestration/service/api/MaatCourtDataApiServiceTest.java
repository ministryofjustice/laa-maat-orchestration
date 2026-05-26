package uk.gov.justice.laa.crime.orchestration.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.orchestration.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.SendToCCLFDTO;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataApiServiceTest {

    @Mock
    private MaatCourtDataApiClient maatCourtDataApiClient;

    @InjectMocks
    private MaatCourtDataApiService maatCourtDataApiService;

    @Test
    void givenValidRequest_whenExecuteStoredProcedureIsInvoked_thenApplicationIsReturned() {
        maatCourtDataApiService.executeStoredProcedure(new StoredProcedureRequest());
        verify(maatCourtDataApiClient).executeStoredProcedure(any(StoredProcedureRequest.class));
    }

    @Test
    void givenValidRequest_whenGetRepOrderByRepIdIsInvoked_thenRepOrderDTOIsReturned() {
        maatCourtDataApiService.getRepOrderByRepId(1000);
        verify(maatCourtDataApiClient).getRepOrderByRepId(anyInt());
    }

    @Test
    void givenValidRequest_whenGetUserSummaryIsInvoked_thenUserSummaryDTOIsReturned() {
        maatCourtDataApiService.getUserSummary("test");
        verify(maatCourtDataApiClient).getUserSummary(anyString());
    }

    @Test
    void givenValidApplicantId_whenGetApplicantIsInvoked_thenApplicantDTOIsReturned() {
        ApplicantDTO expected = new ApplicantDTO();
        when(maatCourtDataApiClient.getApplicant(Constants.APPLICANT_ID)).thenReturn(expected);

        ApplicantDTO actual = maatCourtDataApiService.getApplicant(Constants.APPLICANT_ID);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenInvalidApplicantId_whenGetApplicantIsInvoked_thenExceptionIsThrown() {
        when(maatCourtDataApiClient.getApplicant(Constants.APPLICANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> maatCourtDataApiService.getApplicant(Constants.APPLICANT_ID))
                .isInstanceOf(WebClientResponseException.class);
    }

    @Test
    void givenValidRequest_whenUpdateSendToCCLFIsInvoked_thenApplicationDTOIsReturned() {
        maatCourtDataApiService.updateSendToCCLF(SendToCCLFDTO.builder().build());
        verify(maatCourtDataApiClient).updateSendToCCLF(any(SendToCCLFDTO.class));
    }

    @Test
    void givenValidRequest_whenGetFinancialAssessmentIsInvoked_thenFinancialAssessmentDTOIsReturned() {
        maatCourtDataApiService.getFinancialAssessment(1000);
        verify(maatCourtDataApiClient).getFinancialAssessment(anyInt());
    }

    @Test
    void givenValidRequest_whenUpdateFinancialAssessmentIsInvoked_thenMaatApiAssessmentResponseIsReturned() {
        maatCourtDataApiService.updateFinancialAssessment(new MaatApiUpdateAssessment());
        verify(maatCourtDataApiClient).updateFinancialAssessment(any(MaatApiUpdateAssessment.class));
    }

    @Test
    void givenValidRequest_whenPatchRepOrderIsInvoked_thenIsUpdated() {
        int repOrderId = 1234;
        LocalDateTime dateModified = LocalDateTime.now();
        Map<String, Object> fieldsToUpdate = Map.of("dateModified", dateModified);

        maatCourtDataApiService.patchRepOrder(1234, fieldsToUpdate);
        verify(maatCourtDataApiClient).patchRepOrder(eq(repOrderId), anyMap());
    }
}
