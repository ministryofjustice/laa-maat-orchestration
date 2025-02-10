package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.UserMapper;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.IncomeEvidenceService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDateTime;
import java.util.stream.Stream;

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
    @Mock
    private ContributionService contributionService;

    @Test
    void givenWorkflowRequest_whenUpdateIncomeEvidenceIsInvoked_thenApplicationDTOisUpdatedWithEvidence() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO expected = workflowRequest.getApplicationDTO();
        var incomeEvidenceSummaryDTO = expected.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();
        RepOrderDTO repOrder = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();
        FinancialAssessmentDTO financialAssessmentDTO = repOrder.getFinancialAssessments().get(0);
        financialAssessmentDTO.setIncomeUpliftApplyDate(DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftAppliedDate()));
        financialAssessmentDTO.setIncomeUpliftRemoveDate(DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftRemovedDate()));

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrder);
        UserActionDTO userActionDTO = new UserActionDTO();
        when(userMapper.getUserActionDTO(any(), any(), any())).thenReturn(userActionDTO);
        doNothing().when(workflowPreProcessorService).preProcessEvidenceRequest(userActionDTO, false);
        when(incomeEvidenceService.updateEvidence(workflowRequest, repOrder)).thenReturn(expected);

        ApplicationDTO actual = evidenceOrchestrationService.updateIncomeEvidence(workflowRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("upliftChangeSet")
    void givenUpliftIsChanged_whenUpdateIncomeEvidenceIsInvoked_thenContributionsAreCalculated(WorkflowRequest workflowRequest, RepOrderDTO repOrder) {
        ApplicationDTO expected = workflowRequest.getApplicationDTO();
        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrder);
        UserActionDTO userActionDTO = new UserActionDTO();
        when(userMapper.getUserActionDTO(any(), any(), any())).thenReturn(userActionDTO);
        doNothing().when(workflowPreProcessorService).preProcessEvidenceRequest(userActionDTO, true);
        when(incomeEvidenceService.updateEvidence(workflowRequest, repOrder)).thenReturn(expected);
        when(contributionService.calculate(workflowRequest)).thenReturn(expected);

        ApplicationDTO actual = evidenceOrchestrationService.updateIncomeEvidence(workflowRequest);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> upliftChangeSet() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        var incomeEvidenceSummaryDTO = workflowRequest.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();
        RepOrderDTO repOrderApplyUplift = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();
        repOrderApplyUplift.getFinancialAssessments().get(0).setIncomeUpliftApplyDate(LocalDateTime.now());
        repOrderApplyUplift.getFinancialAssessments().get(0).setIncomeUpliftRemoveDate(
                DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftRemovedDate()));

        RepOrderDTO repOrderRemoveUplift = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();
        repOrderApplyUplift.getFinancialAssessments().get(0).setIncomeUpliftApplyDate(
                DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftAppliedDate()));
        repOrderApplyUplift.getFinancialAssessments().get(0).setIncomeUpliftRemoveDate(LocalDateTime.now());
        return Stream.of(
                Arguments.of(workflowRequest, repOrderApplyUplift),
                Arguments.of(workflowRequest, repOrderRemoveUplift)
        );
    }
}
