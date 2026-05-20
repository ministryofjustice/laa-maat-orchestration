package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepOrderServiceTest {
    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private RepOrderService repOrderService;

    @Test
    void givenUnknownRepOrder_whenGetRepOrderIsInvoked_thenExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        when(maatCourtDataService.findRepOrder(any(Integer.class))).thenReturn(null);

        assertThatThrownBy(() -> repOrderService.getRepOrder(workflowRequest))
                .isInstanceOf(MaatOrchestrationException.class);
    }

    @Test
    void givenExistingRepOrder_whenGetRepOrderIsInvoked_thenRepOrderIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        int repOrderId = workflowRequest.getApplicationDTO().getRepId().intValue();
        RepOrderDTO expectedRepOrder = RepOrderDTO.builder().id(repOrderId).build();

        when(maatCourtDataService.findRepOrder(repOrderId)).thenReturn(expectedRepOrder);

        RepOrderDTO actualRepOrder = repOrderService.getRepOrder(workflowRequest);

        assertThat(actualRepOrder).isEqualTo(expectedRepOrder);
    }

    @Test
    void givenValidDateModified_whenUpdateRepOrderDateModified_thenRepOrderIsUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        int repOrderId = workflowRequest.getApplicationDTO().getRepId().intValue();
        RepOrderDTO updatedRepOrder = RepOrderDTO.builder().id(repOrderId).build();

        when(maatCourtDataService.findRepOrder(repOrderId)).thenReturn(updatedRepOrder);

        RepOrderDTO actualRepOrder = repOrderService.updateRepOrderDateModified(workflowRequest, LocalDateTime.now());

        assertThat(actualRepOrder).isEqualTo(updatedRepOrder);
        verify(maatCourtDataService).updateRepOrder(any(), any());
    }

    @Test
    void givenIndictableCase_whenUpdateAssessmentDateCompletedIsCalled_thenDateIsUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().getCaseDetailsDTO().setCaseType(CaseType.INDICTABLE.getCaseType());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        LocalDateTime dateCompleted = Constants.ASSESSMENT_COMPLETED_DATETIME;
        RepOrderDTO updatedRepOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        updatedRepOrderDTO.setAssessmentDateCompleted(DateUtil.parseLocalDate(dateCompleted));

        when(maatCourtDataService.findRepOrder(
                        workflowRequest.getApplicationDTO().getRepId().intValue()))
                .thenReturn(updatedRepOrderDTO);

        assertThat(repOrderService.updateRepOrderAssessmentDateCompleted(workflowRequest, repOrderDTO, dateCompleted))
                .isEqualTo(updatedRepOrderDTO);
    }

    @Test
    void givenInProgressEitherWayCase_whenUpdateAssessmentDateCompletedIsCalled_thenDateIsNotUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getFull()
                .getAssessmnentStatusDTO()
                .setStatus(CurrentStatus.IN_PROGRESS.getStatus());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        LocalDateTime dateCompleted = Constants.ASSESSMENT_COMPLETED_DATETIME;

        verifyNoInteractions(maatCourtDataService);
        assertThat(repOrderService.updateRepOrderAssessmentDateCompleted(workflowRequest, repOrderDTO, dateCompleted))
                .isEqualTo(repOrderDTO);
    }
}
