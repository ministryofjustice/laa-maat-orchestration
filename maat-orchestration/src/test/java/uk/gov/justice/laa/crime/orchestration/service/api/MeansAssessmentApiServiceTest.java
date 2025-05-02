package uk.gov.justice.laa.crime.orchestration.service.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.client.CrimeMeansAssessmentApiClient;
import uk.gov.justice.laa.crime.orchestration.data.Constants;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MeansAssessmentApiServiceTest {

    @Mock
    private CrimeMeansAssessmentApiClient cmaApiClient;

    @InjectMocks
    private MeansAssessmentApiService meansAssessmentApiService;

    @Test
    void givenValidFinancialAssessmentId_whenFindIsInvoked_thenAssessmentIsRetrieved() {
        meansAssessmentApiService.find(Constants.FINANCIAL_ASSESSMENT_ID);

        verify(cmaApiClient).findMeansAssessment(anyInt());
    }

    @Test
    void givenValidApplicationDto_whenCreateIsInvoked_thenAssessmentIsPersisted() {
        meansAssessmentApiService.create(new ApiCreateMeansAssessmentRequest());

        verify(cmaApiClient).createMeansAssessment(any(ApiCreateMeansAssessmentRequest.class));
    }

    @Test
    void givenValidApplicationDto_whenUpdateIsInvoked_thenAssessmentIsPersisted() {
        meansAssessmentApiService.update(new ApiUpdateMeansAssessmentRequest());

        verify(cmaApiClient).updateMeansAssessment(any(ApiUpdateMeansAssessmentRequest.class));
    }
}
