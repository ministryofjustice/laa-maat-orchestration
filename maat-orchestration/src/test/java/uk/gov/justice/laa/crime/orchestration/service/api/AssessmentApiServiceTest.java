package uk.gov.justice.laa.crime.orchestration.service.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.*;

import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrimeAssessmentApiClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;

@ExtendWith({MockitoExtension.class})
class AssessmentApiServiceTest {

    private static final int EXISTING_APPEAL_ID = 1;

    @Mock
    private CrimeAssessmentApiClient assessmentApiClient;

    @InjectMocks
    private AssessmentApiService assessmentApiService;

    @Test
    void givenAppealId_whenFindIojAppealIsInvoked_thenApiClientReturnsResponse() {
        when(assessmentApiClient.getIojAppeal(EXISTING_APPEAL_ID)).thenReturn(
            new ApiGetIojAppealResponse());

        assessmentApiService.findIojAppeal(EXISTING_APPEAL_ID);

        verify(assessmentApiClient).getIojAppeal(EXISTING_APPEAL_ID);
    }

    @Test
    void givenNullResponse_whenFindIojAppealIsInvoked_thenExceptionThrown() {
        when(assessmentApiClient.getIojAppeal(EXISTING_APPEAL_ID)).thenReturn(null);

        assertThatThrownBy(() -> assessmentApiService.findIojAppeal(EXISTING_APPEAL_ID))
            .isInstanceOf(WebClientResponseException.class);
    }

    @Test
    void givenCreateIojAppealRequest_whenCreateIojAppealIsInvoked_thenApiClientReturnsResponse() {
        ApiCreateIojAppealRequest request = new ApiCreateIojAppealRequest();
        when(assessmentApiClient.createIojAppeal(request)).thenReturn(
            new ApiCreateIojAppealResponse());

        assessmentApiService.createIojAppeal(request);

        verify(assessmentApiClient).createIojAppeal(request);
    }

    @Test
    void givenValidLegacyId_whenFindPassportAssessmentIsInvoked_thenApiClientReturnsResponse() {
        ApiGetPassportedAssessmentResponse response =
            PassportAssessmentDataBuilder.getPassportedAssessmentResponse();

        when(assessmentApiClient.getPassportAssessment(PASSPORT_ASSESSMENT_ID))
            .thenReturn(response);

        assertThat(assessmentApiService.findPassportAssessment(PASSPORT_ASSESSMENT_ID))
            .isEqualTo(response);
    }

    @Test
    void givenInvalidLegacyId_whenFindPassportAssessmentIsInvoked_thenExceptionThrown() {
        when(assessmentApiClient.getPassportAssessment(PASSPORT_ASSESSMENT_ID))
            .thenReturn(null);

        assertThatThrownBy(() -> assessmentApiService.findPassportAssessment(
            PASSPORT_ASSESSMENT_ID)).isInstanceOf(WebClientResponseException.class);
    }
}
