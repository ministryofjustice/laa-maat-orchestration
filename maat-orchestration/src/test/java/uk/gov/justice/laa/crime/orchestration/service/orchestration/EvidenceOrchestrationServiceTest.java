package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.UserMapper;
import uk.gov.justice.laa.crime.orchestration.service.IncomeEvidenceService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvidenceOrchestrationServiceTest {
    @InjectMocks
    private EvidenceOrchestrationService evidenceOrchestrationService;
    @Mock
    private IncomeEvidenceService incomeEvidenceService;
    @Mock
    private RepOrderService repOrderService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private WorkflowPreProcessorService workflowPreProcessorService;

    @Test
    void givenWorkflowRequest_whenUpdateIncomeEvidenceIsInvoked_thenApplicationDTOisUpdatedWithEvidence() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrder = TestModelDataBuilder.buildRepOrderDTO("CURR");
        ApplicationDTO expected = workflowRequest.getApplicationDTO();

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrder);
        UserActionDTO userActionDTO = new UserActionDTO();
        when(userMapper.getUserActionDTO(any(), any(), any())).thenReturn(userActionDTO);
        doNothing().when(workflowPreProcessorService).preProcessEvidenceRequest(userActionDTO);
        when(incomeEvidenceService.updateEvidence(workflowRequest, repOrder)).thenReturn(expected);

        ApplicationDTO actual = evidenceOrchestrationService.updateIncomeEvidence(workflowRequest);

        assertThat(actual).isEqualTo(expected);
    }
}
