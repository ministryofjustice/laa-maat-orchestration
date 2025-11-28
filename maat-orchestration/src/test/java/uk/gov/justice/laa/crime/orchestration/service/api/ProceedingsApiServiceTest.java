package uk.gov.justice.laa.crime.orchestration.service.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiDetermineMagsRepDecisionRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtProceedingApiClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProceedingsApiServiceTest {

    @Mock
    private CrownCourtProceedingApiClient crownCourtApiClient;

    @InjectMocks
    private ProceedingsApiService proceedingsApiService;

    @Test
    void givenValidRequest_whenDetermineMagsRepDecisionIsInvoked_ThenAPIRequestIsSent() {
        proceedingsApiService.determineMagsRepDecision(new ApiDetermineMagsRepDecisionRequest());

        verify(crownCourtApiClient).determineMagsRepDecision(any(ApiDetermineMagsRepDecisionRequest.class));
    }

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenAPIRequestIsSent() {
        proceedingsApiService.updateApplication(new ApiUpdateApplicationRequest());

        verify(crownCourtApiClient).updateApplication(any(ApiUpdateApplicationRequest.class));
    }

    @Test
    void givenValidRequest_whenUpdateCrownCourtIsInvoked_thenAPIRequestIsSent() {
        proceedingsApiService.updateCrownCourt(new ApiUpdateCrownCourtRequest());

        verify(crownCourtApiClient).updateCrownCourt(any(ApiUpdateCrownCourtRequest.class));
    }
}
