package uk.gov.justice.laa.crime.orchestration.service.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrimeAssessmentApiClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class AssessmentServiceApiServiceTest {
    private static final int EXISTING_APPEAL_ID = 1;

    @Mock
    private CrimeAssessmentApiClient assessmentApiClient;

    @InjectMocks
    private AssessmentApiService assessmentApiService;

    @Test
    void givenAppealId_whenFindIsInvoked_thenApiServiceIsCalledAndResponseMapped() {
        when(assessmentApiClient.getIojAppeal(EXISTING_APPEAL_ID)).thenReturn(new ApiGetIojAppealResponse());

        assessmentApiService.find(EXISTING_APPEAL_ID);

        verify(assessmentApiClient).getIojAppeal(EXISTING_APPEAL_ID);
    }
}
