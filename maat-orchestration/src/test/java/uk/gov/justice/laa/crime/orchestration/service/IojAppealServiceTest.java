package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class IojAppealServiceTest {

    private static final int EXISTING_APPEAL_ID = 44;

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

    @Test
    void givenWorkflowRequest_whenCreateIsInvoked_thenApiServiceIsCalledAndLegacyAppealIdMapped() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        ApiCreateIojAppealRequest request = new ApiCreateIojAppealRequest();
        ApiCreateIojAppealResponse response = new ApiCreateIojAppealResponse().withLegacyAppealId(EXISTING_APPEAL_ID);

        when(iojAppealMapper.mapIojAppealDtoToApiCreateIojAppealRequest(workflowRequest))
                .thenReturn(request);
        when(assessmentApiService.create(request)).thenReturn(response);

        ApplicationDTO applicationDTO = iojAppealService.create(workflowRequest);

        verify(iojAppealMapper).mapIojAppealDtoToApiCreateIojAppealRequest(workflowRequest);
        verify(assessmentApiService).create(request);
        assertThat(applicationDTO.getAssessmentDTO().getIojAppeal().getIojId()).isEqualTo(EXISTING_APPEAL_ID);
    }
}
