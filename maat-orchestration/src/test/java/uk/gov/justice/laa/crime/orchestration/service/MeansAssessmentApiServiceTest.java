package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiUpdateMeansAssessmentRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MeansAssessmentApiServiceTest {

    @Mock
    private RestAPIClient cmaApiClient;

    @InjectMocks
    private MeansAssessmentApiService meansAssessmentApiService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenValidFinancialAssessmentId_whenFindIsInvoked_thenAssessmentIsRetrieved() {
        meansAssessmentApiService.find(Constants.FINANCIAL_ASSESSMENT_ID);

        verify(cmaApiClient)
                .get(any(), anyString(), anyInt());
    }

    @Test
    void givenValidApplicationDto_whenCreateIsInvoked_thenAssessmentIsPersisted() {
        meansAssessmentApiService.create(new ApiCreateMeansAssessmentRequest());

        verify(cmaApiClient)
                .post(any(ApiCreateMeansAssessmentRequest.class), any(), anyString(), anyMap());
    }

    @Test
    void givenValidApplicationDto_whenUpdateIsInvoked_thenAssessmentIsPersisted() {
        meansAssessmentApiService.update(new ApiUpdateMeansAssessmentRequest());

        verify(cmaApiClient)
                .put(any(ApiUpdateMeansAssessmentRequest.class), any(), anyString(), anyMap());
    }
}
