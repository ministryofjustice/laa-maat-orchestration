package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiRollbackIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.exception.RollbackException;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class IojAppealServiceTest {

    private static final int LEGACY_APPEAL_ID = 44;
    private static final String APPEAL_ID = "55";

    @Mock
    private IojAppealMapper iojAppealMapper;

    @Mock
    private AssessmentApiService assessmentApiService;

    @InjectMocks
    private IojAppealService iojAppealService;

    @Test
    void givenAppealId_whenFindIsInvoked_thenApiServiceIsCalledAndResponseMapped() {
        ApiGetIojAppealResponse response = TestModelDataBuilder.getIojAppealResponse();
        when(assessmentApiService.findIojAppeal(LEGACY_APPEAL_ID)).thenReturn(response);

        iojAppealService.find(LEGACY_APPEAL_ID);

        verify(iojAppealMapper).apiGetIojAppealResponseToIojAppealDTO(response);
    }

    @Test
    void givenWorkflowRequest_whenCreateIsInvoked_thenApiServiceIsCalledAndLegacyAppealIdMapped() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        ApiCreateIojAppealRequest request = new ApiCreateIojAppealRequest();
        ApiCreateIojAppealResponse response = new ApiCreateIojAppealResponse()
                .withLegacyAppealId(LEGACY_APPEAL_ID)
                .withAppealId(APPEAL_ID);

        when(iojAppealMapper.mapIojAppealDtoToApiCreateIojAppealRequest(workflowRequest))
                .thenReturn(request);
        when(assessmentApiService.createIojAppeal(request)).thenReturn(response);

        String appealId = iojAppealService.create(workflowRequest);

        verify(iojAppealMapper).mapIojAppealDtoToApiCreateIojAppealRequest(workflowRequest);
        verify(assessmentApiService).createIojAppeal(request);
        assertThat(workflowRequest
                        .getApplicationDTO()
                        .getAssessmentDTO()
                        .getIojAppeal()
                        .getIojId())
                .isEqualTo(LEGACY_APPEAL_ID);
        assertThat(appealId).isEqualTo(APPEAL_ID);
    }

    @ParameterizedTest
    @MethodSource("rollbackCases")
    void givenWorkflowRequest_whenRollbackIsInvoked_rollbackBehaviourIsCorrectAndAppealIdIsReset(
            boolean rollbackSuccessful) {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ApiRollbackIojAppealResponse response =
                new ApiRollbackIojAppealResponse().withRollbackSuccessful(rollbackSuccessful);
        when(assessmentApiService.rollback(APPEAL_ID)).thenReturn(response);
        if (rollbackSuccessful) {
            iojAppealService.rollback(APPEAL_ID, workflowRequest);
        } else {
            assertThatThrownBy(() -> iojAppealService.rollback(APPEAL_ID, workflowRequest))
                    .isInstanceOf(RollbackException.class);
        }
        assertThat(workflowRequest
                        .getApplicationDTO()
                        .getAssessmentDTO()
                        .getIojAppeal()
                        .getIojId())
                .isNull();
    }

    static Stream<Boolean> rollbackCases() {
        return Stream.of(true, false);
    }
}
