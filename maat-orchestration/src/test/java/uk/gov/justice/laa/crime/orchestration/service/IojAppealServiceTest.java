package uk.gov.justice.laa.crime.orchestration.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class IojAppealServiceTest {

    private static final int EXISTING_APPEAL_ID = 1;

    @Mock
    private IojAppealMapper iojAppealMapper;

    @Mock
    private AssessmentApiService assessmentApiService;

    @InjectMocks
    private IojAppealService iojAppealService;

    @Test
    void givenAppealId_whenFindIsInvoked_thenApiServiceIsCalledAndResponseMapped() {
        ApiGetIojAppealResponse response = TestModelDataBuilder.getIojAppealResponse();
        when(assessmentApiService.find(EXISTING_APPEAL_ID)).thenReturn(response);

        iojAppealService.find(EXISTING_APPEAL_ID);

        verify(iojAppealMapper).apiGetIojAppealResponseToIojAppealDTO(response);
    }
}
