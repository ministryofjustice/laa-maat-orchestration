package uk.gov.justice.laa.crime.orchestration.service.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrimeAssessmentApiClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ExtendWith({MockitoExtension.class})
class AssessmentApiServiceTest {
    private static final int EXISTING_APPEAL_ID = 1;

    @Mock
    private CrimeAssessmentApiClient assessmentApiClient;

    @InjectMocks
    private AssessmentApiService assessmentApiService;

    @Test
    void givenAppealId_whenFindIsInvoked_thenApiClientReturnsResponse() {
        when(assessmentApiClient.getIojAppeal(EXISTING_APPEAL_ID)).thenReturn(new ApiGetIojAppealResponse());

        assessmentApiService.find(EXISTING_APPEAL_ID);

        verify(assessmentApiClient).getIojAppeal(EXISTING_APPEAL_ID);
    }

    @Test
    void givenNullResponse_whenFindIsInvoked_thenExceptionThrown() {
        when(assessmentApiClient.getIojAppeal(EXISTING_APPEAL_ID)).thenReturn(null);

        assertThatThrownBy(() -> assessmentApiService.find(EXISTING_APPEAL_ID))
                .isInstanceOf(WebClientResponseException.class);
    }

    @Test
    void givenCreateIojAppealRequest_whenCreateIsInvoked_thenApiClientReturnsResponse() {
        ApiCreateIojAppealRequest request = new ApiCreateIojAppealRequest();
        when(assessmentApiClient.createIojAppeal(request)).thenReturn(new ApiCreateIojAppealResponse());

        assessmentApiService.create(request);

        verify(assessmentApiClient).createIojAppeal(request);
    }
}
