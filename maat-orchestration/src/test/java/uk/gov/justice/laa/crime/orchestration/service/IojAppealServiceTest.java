package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrimeAssessmentApiClient;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ExtendWith({MockitoExtension.class})
class IojAppealServiceTest {

    private static final Integer EXISTING_APPEAL_ID = 1;

    @Mock
    private IojAppealMapper iojAppealMapper;

    @Mock
    private CrimeAssessmentApiClient assessmentServiceApi;

    @InjectMocks
    private IojAppealService iojAppealService;

    @Test
    void givenAppealId_whenFindIsInvoked_thenApiServiceIsCalledAndResponseMapped() {
        ApiGetIojAppealResponse response = TestModelDataBuilder.getIojAppealResponse();
        when(assessmentServiceApi.getIojAppeal(EXISTING_APPEAL_ID)).thenReturn(response);

        iojAppealService.find(EXISTING_APPEAL_ID);

        verify(iojAppealMapper).apiGetIojAppealResponseToIojAppealDTO(response);
    }

    @Test
    void givenNullResponse_whenFindIsInvoked_thenExceptionThrownAndMapperNotCalled() {
        when(assessmentServiceApi.getIojAppeal(any())).thenReturn(null);

        assertThatThrownBy(() -> iojAppealService.find(any())).isInstanceOf(WebClientResponseException.class);

        verify(iojAppealMapper, times(0)).apiGetIojAppealResponseToIojAppealDTO(any());
    }
}
