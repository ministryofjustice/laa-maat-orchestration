package uk.gov.justice.laa.crime.orchestration.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.orchestration.client.EvidenceApiClient;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EvidenceApiServiceTest {

    @Mock
    private EvidenceApiClient evidenceApiClient;

    @InjectMocks
    private EvidenceApiService evidenceApiService;

    @Test
    void givenValidRequest_whenCreateEvidenceIsInvoked_thenApiCreateIncomeEvidenceResponseIsReturned() {
        evidenceApiService.createEvidence(new ApiCreateIncomeEvidenceRequest());
        verify(evidenceApiClient).createEvidence(any(ApiCreateIncomeEvidenceRequest.class));
    }

    @Test
    void givenValidRequest_whenUpdateEvidenceIsInvoked_thenApiUpdateIncomeEvidenceResponseIsReturned() {
        ApiUpdateIncomeEvidenceResponse expectedResponse =
                new ApiUpdateIncomeEvidenceResponse().withAllEvidenceReceivedDate(LocalDate.now());
        when(evidenceApiClient.updateEvidence(any(ApiUpdateIncomeEvidenceRequest.class)))
                .thenReturn(expectedResponse);
        var actualResponse = evidenceApiService.updateEvidence(new ApiUpdateIncomeEvidenceRequest());
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
