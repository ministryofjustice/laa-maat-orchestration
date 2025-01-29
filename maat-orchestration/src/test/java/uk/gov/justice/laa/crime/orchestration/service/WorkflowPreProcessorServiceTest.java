package uk.gov.justice.laa.crime.orchestration.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.util.stream.Stream;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.justice.laa.crime.enums.CaseType.INDICTABLE;

@ExtendWith({MockitoExtension.class})
class WorkflowPreProcessorServiceTest {
    @Mock
    private ContributionService contributionService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private WorkflowPreProcessorService workflowPreProcessorService;

    @Test
    void givenRequestValidationFails_whenWorkflowPreProcessorServiceIsInvoked_thenExceptionIsThrown() {
        WorkflowRequest request = getWorkflowRequestWithCaseType(INDICTABLE.getCaseType());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();

        doThrow(new ValidationException(ValidationService.CANNOT_MODIFY_APPLICATION_ERROR))
            .when(validationService).validate(request, repOrderDTO);

        assertThatThrownBy(() -> workflowPreProcessorService.preProcessRequest(request, repOrderDTO, userActionDTO))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void givenUserIsNotAuthorisedToPerformRequestedAction_whenWorkflowPreProcessorServiceIsInvoked_thenExceptionIsThrown() {
        WorkflowRequest request = getWorkflowRequestWithCaseType(INDICTABLE.getCaseType());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        List<String> validationErrors = List.of(
            ValidationService.USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE);

        when(maatCourtDataService.getUserSummary(userActionDTO.getUsername())).thenReturn(userSummaryDTO);
        doThrow(new CrimeValidationException(validationErrors)).when(validationService).isUserActionValid(userActionDTO, userSummaryDTO);

        assertThatThrownBy(() -> workflowPreProcessorService.preProcessRequest(request, repOrderDTO, userActionDTO))
            .isInstanceOf(CrimeValidationException.class);
    }

    @Test
    void givenApplicationStatusHasChanged_whenWorkflowPreProcessorServiceIsInvoked_thenUpdatedWorkflowRequestWithContributionIsReturned() {
        WorkflowRequest request = getWorkflowRequestWithCaseType(INDICTABLE.getCaseType());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        when(contributionService.calculate(any())).thenReturn(TestModelDataBuilder.getApplicationDTO());
        when(maatCourtDataService.getUserSummary(userActionDTO.getUsername())).thenReturn(userSummaryDTO);
        when(validationService.isUserActionValid(userActionDTO, userSummaryDTO)).thenReturn(true);

        workflowPreProcessorService.preProcessRequest(request, repOrderDTO, userActionDTO);

        verify(contributionService).calculate(any());
    }

    @ParameterizedTest
    @MethodSource("workflowRequestWithNoApplicationStatusChange")
    void givenApplicationStatusHasNotChanged_whenWorkflowPreProcessorServiceIsInvoked_thenWorkflowRequestIsReturned(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        when(maatCourtDataService.getUserSummary(userActionDTO.getUsername())).thenReturn(userSummaryDTO);
        when(validationService.isUserActionValid(userActionDTO, userSummaryDTO)).thenReturn(true);

        workflowPreProcessorService.preProcessRequest(request, repOrderDTO, userActionDTO);

        verify(contributionService, times(0)).calculate(any());
    }

    private static Stream<Arguments> workflowRequestWithNoApplicationStatusChange() {
        return Stream.of(
            Arguments.of(
                TestModelDataBuilder.buildWorkFlowRequest(),
                TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode())),
            Arguments.of(
                TestModelDataBuilder.buildWorkFlowRequest(),
                TestModelDataBuilder.buildRepOrderDTO(null)),
            Arguments.of(
                TestModelDataBuilder.buildWorkFlowRequest(),
                TestModelDataBuilder.buildRepOrderDTO("RepStatus")),
            Arguments.of(
                WorkflowRequest.builder()
                    .applicationDTO(ApplicationDTO.builder()
                        .statusDTO(null)
                        .caseDetailsDTO(null)
                        .build())
                    .build(),
                TestModelDataBuilder.buildRepOrderDTO("RepStatus")),
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

    @Test
    void givenRequestValidationFails_whenPreProcessEvidenceRequestIsInvoked_thenExceptionIsThrown() {
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        when(maatCourtDataService.getUserSummary(userActionDTO.getUsername())).thenReturn(userSummaryDTO);
        doThrow(new CrimeValidationException(List.of(ValidationService.USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION)))
                .when(validationService).isUserActionValid(userActionDTO, userSummaryDTO);

        assertThatThrownBy(() -> workflowPreProcessorService.preProcessEvidenceRequest(userActionDTO, false))
                .isInstanceOf(CrimeValidationException.class);
    }

    @Test
    void givenUserNotAllowedToApplyUplift_whenPreProcessEvidenceRequestIsInvoked_thenExceptionIsThrown() {
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        when(maatCourtDataService.getUserSummary(userActionDTO.getUsername())).thenReturn(userSummaryDTO);
        when(validationService.isUserActionValid(userActionDTO, userSummaryDTO)).thenReturn(true);
        doThrow(new ValidationException(ValidationService.USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION))
                .when(validationService).checkUpliftFieldPermissions(userSummaryDTO);

        assertThatThrownBy(() -> workflowPreProcessorService.preProcessEvidenceRequest(userActionDTO, true))
                .isInstanceOf(ValidationException.class);
    }
}
