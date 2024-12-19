package uk.gov.justice.laa.crime.orchestration.service.api;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvidenceApiServiceTest {

    @Mock
    private RestAPIClient evidenceApiClient;
    @InjectMocks
    private EvidenceApiService evidenceApiService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenValidRequest_whenCreateEvidenceIsInvoked_thenApiCreateIncomeEvidenceResponseIsReturned() {
        evidenceApiService.createEvidence(new ApiCreateIncomeEvidenceRequest());
        verify(evidenceApiClient).post(any(ApiCreateIncomeEvidenceRequest.class), any(), any(), any());
    }

    @Test
    void givenValidRequest_whenUpdateEvidenceIsInvoked_thenApiUpdateIncomeEvidenceResponseIsReturned() {
        ApiUpdateIncomeEvidenceResponse expectedResponse = new ApiUpdateIncomeEvidenceResponse()
                .withAllEvidenceReceivedDate(LocalDate.now());
        when(evidenceApiClient.put(any(ApiUpdateIncomeEvidenceRequest.class), any(), any(), any()))
                .thenReturn(expectedResponse);
        var actualResponse = evidenceApiService.updateEvidence(new ApiUpdateIncomeEvidenceRequest());
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
