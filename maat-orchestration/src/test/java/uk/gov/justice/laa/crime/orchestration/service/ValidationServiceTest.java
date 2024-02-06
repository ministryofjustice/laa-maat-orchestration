package uk.gov.justice.laa.crime.orchestration.service;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.enums.Action;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.model.maat_api.ApiIsRoleActionValidRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.service.ValidationService.CANNOT_MODIFY_APPLICATION_ERROR;
@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {
    public static final String EXISTING_RESERVATION_SO_RESERVATION_NOT_ALLOWED = "User have an existing reservation, so reservation not allowed";
    public static final String NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION = "User does not have a role capable of performing this action";
    public static final String DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE = "User does not have a valid New Work Reason Code";
    public static final String ACTION_NEW_WORK_REASON_AND_SESSION_DOES_NOT_EXIST = "Action, New work reason and Session does not exist";
    @InjectMocks
    private ValidationService validationService;
    @Mock
    private MaatCourtDataService maatCourtDataService;

    @ParameterizedTest
    @MethodSource("validateApplicationTimestamp")
    void validateApplicationTimestamp(final WorkflowRequest workflowRequest, final RepOrderDTO repOrderDTO) {
        when(maatCourtDataService.findRepOrder(any())).thenReturn(repOrderDTO);
        ValidationException validationException = assertThrows(ValidationException.class, () -> validationService.
                validate(workflowRequest));
        assertThat(validationException.getMessage()).isEqualTo(CANNOT_MODIFY_APPLICATION_ERROR);
    }

    @ParameterizedTest
    @MethodSource("validateApplicationStatus")
    void validateApplicationStatus(final WorkflowRequest workflowRequest, final RepOrderDTO repOrderDTO) {
        when(maatCourtDataService.findRepOrder(any())).thenReturn(repOrderDTO);
        ValidationException validationException = assertThrows(ValidationException.class, () -> validationService.
                validate(workflowRequest));
        assertThat(validationException.getMessage()).contains("Cannot update case in status of");
    }

    @ParameterizedTest
    @MethodSource("validateApplicationStatusNoException")
    void validateApplicationStatus_noException(final WorkflowRequest workflowRequest, final RepOrderDTO repOrderDTO) {
        when(maatCourtDataService.findRepOrder(any())).thenReturn(repOrderDTO);
        assertDoesNotThrow(() -> validationService.validate(workflowRequest));
    }

    private static Stream<Arguments> validateApplicationTimestamp() {
        return Stream.of(
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequestForApplicationTimestampValidation(),
                        TestModelDataBuilder
                                .buildRepOrderDTOWithModifiedDate()),
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequestForApplicationTimestampValidation(),
                        TestModelDataBuilder
                                .buildRepOrderDTOWithCreatedDateAndNoModifiedDate())
        );
    }

    private static Stream<Arguments> validateApplicationStatus() {
        return Stream.of(
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequest(false),
                        TestModelDataBuilder
                                .buildRepOrderDTO(RepOrderStatus.ERR.getCode()))
        );
    }

    private static Stream<Arguments> validateApplicationStatusNoException() {
        return Stream.of(
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequest(true),
                        TestModelDataBuilder
                                .buildRepOrderDTO(RepOrderStatus.ERR.getCode())),
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequest(false),
                        TestModelDataBuilder
                                .buildRepOrderDTO(null)),
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkFlowRequest(true),
                        TestModelDataBuilder
                                .buildRepOrderDTO(RepOrderStatus.CURR.getCode()))
        );
    }

    @Test
    void givenValidParameters_whenIsUserActionValidIsInvoked_thenValidationStatusIsReturned() {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        Boolean isUserActionValid =
                validationService.isUserActionValid(TestModelDataBuilder.getApiIsRoleActionValidRequest());
        assertTrue(isUserActionValid);
    }


    @Test
    void givenInputWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getApiIsRoleActionValidRequestWithReservation()))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(EXISTING_RESERVATION_SO_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithRoleActionNotAssignedToUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequest();
        apiIsRoleActionValidRequest.setAction(Action.CREATE_APPLICATION);
        assertThatThrownBy(() -> validationService.isUserActionValid(apiIsRoleActionValidRequest))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);

    }

    @Test
    void givenInputWithNewWorkReasonNotAssignedToUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequest();
        apiIsRoleActionValidRequest.setNewWorkReason(NewWorkReason.NEW);
        assertThatThrownBy(() -> validationService.isUserActionValid(apiIsRoleActionValidRequest))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE);
    }

    @Test
    void givenParametersWithNoExistingReservation_whenIsUserActionValidIsInvoked_thenValidationStatusIsReturned() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequest();
        apiIsRoleActionValidRequest.setSessionId("");
        Boolean isUserActionValid =
                validationService.isUserActionValid(TestModelDataBuilder.getApiIsRoleActionValidRequest());
        assertTrue(isUserActionValid);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndNewWorkReasonNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);

        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequestWithReservation();
        apiIsRoleActionValidRequest.setAction(Action.CREATE_APPLICATION);
        apiIsRoleActionValidRequest.setNewWorkReason(NewWorkReason.NEW);

        assertThatThrownBy(() -> validationService.isUserActionValid(apiIsRoleActionValidRequest))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION,
                        EXISTING_RESERVATION_SO_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndNewWorkReasonNotAssigned_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);

        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequestWithReservation();
        apiIsRoleActionValidRequest.setAction(Action.CREATE_APPLICATION);
        apiIsRoleActionValidRequest.setNewWorkReason(NewWorkReason.NEW);
        apiIsRoleActionValidRequest.setSessionId("");

        assertThatThrownBy(() -> validationService.isUserActionValid(apiIsRoleActionValidRequest))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);

        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequestWithReservation();
        apiIsRoleActionValidRequest.setAction(Action.CREATE_APPLICATION);

        assertThatThrownBy(() -> validationService.isUserActionValid(apiIsRoleActionValidRequest))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION,
                        EXISTING_RESERVATION_SO_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithNewWorkReasonNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);

        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequestWithReservation();
        apiIsRoleActionValidRequest.setNewWorkReason(NewWorkReason.NEW);

        assertThatThrownBy(() -> validationService.isUserActionValid(apiIsRoleActionValidRequest))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        EXISTING_RESERVATION_SO_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInvalidInput_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionInvalidValidRequest();

        assertThatThrownBy(() -> validationService.isUserActionValid(apiIsRoleActionValidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ACTION_NEW_WORK_REASON_AND_SESSION_DOES_NOT_EXIST);
    }

    @Test
    void givenValidInputAndWithNoRoleActionsForUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setRoleActions(null);
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);

        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getApiIsRoleActionValidRequest()))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);
    }

    @Test
    void givenValidInputAndWithNewWorkReasonForUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setNewWorkReasons(null);
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);

        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getApiIsRoleActionValidRequest()))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE);
    }

    @Test
    void givenValidInputAndWithDifferentSessionIdForUser_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.getReservationsEntity().setUserSession("sessionId_1234");
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);
        Boolean isUserActionValid =
                validationService.isUserActionValid(TestModelDataBuilder.getApiIsRoleActionValidRequest());
        assertTrue(isUserActionValid);
    }

    @Test
    void givenValidInputWithOnlyRoleAction_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequest();
        apiIsRoleActionValidRequest.setNewWorkReason(null);
        apiIsRoleActionValidRequest.setSessionId(null);
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);
        Boolean isUserActionValid =
                validationService.isUserActionValid(apiIsRoleActionValidRequest);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenValidInputWithOnlyNewWorkReason_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequest();
        apiIsRoleActionValidRequest.setAction(null);
        apiIsRoleActionValidRequest.setSessionId(null);
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);
        Boolean isUserActionValid =
                validationService.isUserActionValid(apiIsRoleActionValidRequest);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenValidInputWithOnlySessionId_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequestWithReservation();
        apiIsRoleActionValidRequest.setAction(null);
        apiIsRoleActionValidRequest.setNewWorkReason(null);

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.getReservationsEntity().setUserSession("sessionId_1234");
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);

        Boolean isUserActionValid =
                validationService.isUserActionValid(apiIsRoleActionValidRequest);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenValidInputWithNoReservation_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        ApiIsRoleActionValidRequest apiIsRoleActionValidRequest = TestModelDataBuilder.getApiIsRoleActionValidRequestWithReservation();
        apiIsRoleActionValidRequest.setAction(null);
        apiIsRoleActionValidRequest.setNewWorkReason(null);

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setReservationsEntity(null);
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);

        Boolean isUserActionValid =
                validationService.isUserActionValid(apiIsRoleActionValidRequest);
        assertTrue(isUserActionValid);
    }
}
