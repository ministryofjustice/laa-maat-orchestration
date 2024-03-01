package uk.gov.justice.laa.crime.orchestration.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.justice.laa.crime.enums.CaseType.INDICTABLE;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getApplicationDTO;

@ExtendWith({MockitoExtension.class})
class WorkflowPreProcessorServiceTest {
    @Mock
    private ContributionService contributionService;

    @InjectMocks
    private WorkflowPreProcessorService workflowPreProcessorService;

    @Test
    void givenApplicationStatusHasChanged_whenWorkflowPreProcessorServiceIsInvoked_thenUpdatedWorkflowRequestWithContributionIsReturned() {
        WorkflowRequest request = getWorkflowRequestWithCaseType(INDICTABLE.getCaseType());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        when(contributionService.calculate(any()))
                .thenReturn(getApplicationDTO());
        workflowPreProcessorService.preProcessRequest(request, repOrderDTO);
        verify(contributionService).calculate(any());
    }

    @ParameterizedTest
    @MethodSource("workflowRequestWithNoApplicationStatusChange")
    void givenApplicationStatusHasNotChanged_whenWorkflowPreProcessorServiceIsInvoked_thenWorkflowRequestIsReturned(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        workflowPreProcessorService.preProcessRequest(request, repOrderDTO);
        verify(contributionService, times(0)).calculate(any());
    }

    private static Stream<Arguments> workflowRequestWithNoApplicationStatusChange() {
        return Stream.of(
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequest(),
                        TestModelDataBuilder
                                .buildRepOrderDTO(RepOrderStatus.CURR.getCode())),
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequest(),
                        TestModelDataBuilder
                                .buildRepOrderDTO(null)),
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequest(),
                        TestModelDataBuilder
                                .buildRepOrderDTO("RepStatus")),
                Arguments.of(
                        WorkflowRequest.builder()
                                .applicationDTO(ApplicationDTO.builder()
                                        .statusDTO(null)
                                        .caseDetailsDTO(null)
                                        .build())
                                .build(),
                        TestModelDataBuilder
                                .buildRepOrderDTO("RepStatus")),
                Arguments.of(
                        getWorkflowRequestWithCaseType(INDICTABLE.getCaseType()),
                        null),
                Arguments.of(
                        getWorkflowRequestWithCaseType(INDICTABLE.getCaseType()),
                        TestModelDataBuilder
                                .buildRepOrderDTO(null)),
                Arguments.of(
                        getWorkflowRequestWithCaseType(INDICTABLE.getCaseType()),
                        TestModelDataBuilder
                                .buildRepOrderDTO("RepStatus"))
        );
    }

    private static WorkflowRequest getWorkflowRequestWithCaseType(String caseType) {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        request.getApplicationDTO().getCaseDetailsDTO().setCaseType(caseType);
        return request;
    }

}
