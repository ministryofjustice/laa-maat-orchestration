package uk.gov.justice.laa.crime.orchestration.service.api;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EvidenceApiServiceTest {

    @Mock
    private RestAPIClient evidenceApiClient;
    @InjectMocks
    private EvidenceApiService evidenceApiService;

    @Test
    void givenValidRequest_whenCreateEvidenceIsInvoked_thenApiCreateIncomeEvidenceResponseIsReturned() {
        evidenceApiService.createEvidence(new ApiCreateIncomeEvidenceRequest());
        verify(evidenceApiClient).post(any(ApiCreateIncomeEvidenceRequest.class), any(), any(), any());
    }
}
