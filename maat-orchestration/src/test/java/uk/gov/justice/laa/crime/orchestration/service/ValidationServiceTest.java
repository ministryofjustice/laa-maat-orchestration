package uk.gov.justice.laa.crime.orchestration.service;

import java.util.Optional;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RoleDataItemDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.enums.Action;
import uk.gov.justice.laa.crime.orchestration.enums.RestrictedField;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.TEST_USER_SESSION;
import static uk.gov.justice.laa.crime.orchestration.service.ValidationService.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {
    @InjectMocks
    private ValidationService validationService;

    private static Stream<Arguments> restrictedFieldsNotEditableByUser() {
        return Stream.of(
            Arguments.of(RestrictedField.APPEAL_CC_OUTCOME),
            Arguments.of(RestrictedField.APPEAL_RECEIVED_DATE)
        );
    }

    private static Stream<Arguments> restrictedFieldsEditableByUser() {
        return Stream.of(
            Arguments.of(RestrictedField.APPEAL_RECEIVED_DATE),
            Arguments.of(RestrictedField.APPEAL_SENTENCE_ORDER_DATE),
            Arguments.of(RestrictedField.APPEAL_CC_WITHDRAWAL_DATE)
        );
    }

    private static Stream<Arguments> validateApplicationTimestamp() {
        return Stream.of(
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkflowRequestForApplicationTimestampValidation(Optional.empty()),
                        TestModelDataBuilder
                                .buildRepOrderDTOWithModifiedDate()),
                Arguments.of(
                        TestModelDataBuilder
                                .buildWorkflowRequestForApplicationTimestampValidation(Optional.empty()),
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
    void validateApplicationTimestamp_whenApplicationTimestampIsNull_thenNoExceptionIsThrow() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithModifiedDate();

        assertDoesNotThrow(() -> validationService.validate(workflowRequest, repOrderDTO));
    }

    @Test
    void validateApplicationTimestamp_whenApplicationAndRepOrderTimestampsAreEqual_thenNoExceptionIsThrown() {
        String timestamp = "2024-07-26T10:41:02.515";

        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkflowRequestForApplicationTimestampValidation(Optional.of(timestamp));
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithModifiedDateOf(timestamp);

        assertDoesNotThrow(() -> validationService.validate(workflowRequest, repOrderDTO));
    }

    @Test
    void validateApplicationTimestamp_whenApplicationAndRepOrderTimestampsAreEqualExceptMilliseconds_thenNoExceptionIsThrown() {
        String applicationTimestamp = "2024-07-26T10:41:02.515";
        String repOrderTimestamp = "2024-07-26T10:41:02.314";

        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkflowRequestForApplicationTimestampValidation(Optional.of(applicationTimestamp));
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithModifiedDateOf(repOrderTimestamp);

        assertDoesNotThrow(() -> validationService.validate(workflowRequest, repOrderDTO));
    }

    @ParameterizedTest
    @MethodSource("validateApplicationTimestamp")
    void validateApplicationTimestamp_whenApplicationHasBeenModifiedByAnotherUser_thenExceptionIsThrown(final WorkflowRequest workflowRequest, final RepOrderDTO repOrderDTO) {
        ValidationException validationException = assertThrows(ValidationException.class, () -> validationService.
                validate(workflowRequest, repOrderDTO));
        assertThat(validationException.getMessage()).isEqualTo(CANNOT_MODIFY_APPLICATION_ERROR);
    }

    @ParameterizedTest
    @MethodSource("validateApplicationStatus")
    void validateApplicationStatus(final WorkflowRequest workflowRequest, final RepOrderDTO repOrderDTO) {
        ValidationException validationException = assertThrows(ValidationException.class, () -> validationService.
                validate(workflowRequest, repOrderDTO));
        assertThat(validationException.getMessage()).contains("Cannot update case in status of");
    }

    @ParameterizedTest
    @MethodSource("validateApplicationStatusNoException")
    void validateApplicationStatus_noException(final WorkflowRequest workflowRequest, final RepOrderDTO repOrderDTO) {
        assertDoesNotThrow(() -> validationService.validate(workflowRequest, repOrderDTO));
    }

    @Test
    void givenValidParameters_whenIsUserActionValidIsInvoked_thenValidationStatusIsReturned() {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        Boolean isUserActionValid =
                validationService.isUserActionValid(TestModelDataBuilder.getUserActionDTO(), userSummaryDTO);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenInputWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getUserActionDTOWithReservation(), userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(ValidationService.USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithRoleActionNotAssignedToUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTOWithReservation();
        userActionDTO.setAction(Action.CREATE_APPLICATION);

        assertThatThrownBy(() -> validationService.isUserActionValid(userActionDTO, userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);

    }

    @Test
    void givenInputWithNewWorkReasonNotAssignedToUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTOWithReservation();
        userActionDTO.setNewWorkReason(NewWorkReason.CFC);

        assertThatThrownBy(() -> validationService.isUserActionValid(userActionDTO, userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE);
    }

    @Test
    void givenParametersWithNoExistingReservation_whenIsUserActionValidIsInvoked_thenValidationStatusIsReturned() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        userActionDTO.setSessionId("");

        assertThatThrownBy(() -> validationService.isUserActionValid(userActionDTO, userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndNewWorkReasonNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTOWithReservation();
        userActionDTO.setAction(Action.CREATE_APPLICATION);
        userActionDTO.setNewWorkReason(NewWorkReason.CFC);

        assertThatThrownBy(() -> validationService.isUserActionValid(userActionDTO, userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION,
                        USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndNewWorkReasonNotAssigned_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTOWithReservation();
        userActionDTO.setAction(Action.CREATE_APPLICATION);
        userActionDTO.setNewWorkReason(NewWorkReason.CFC);
        userActionDTO.setSessionId("");

        assertThatThrownBy(() -> validationService.isUserActionValid(userActionDTO, userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTOWithReservation();
        userActionDTO.setAction(Action.CREATE_APPLICATION);

        assertThatThrownBy(() -> validationService.isUserActionValid(userActionDTO, userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION,
                        USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithNewWorkReasonNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTOWithReservation();
        userActionDTO.setNewWorkReason(NewWorkReason.CFC);

        assertThatThrownBy(() -> validationService.isUserActionValid(userActionDTO, userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInvalidInput_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTOInvalidValidRequest();

        assertThatThrownBy(() -> validationService.isUserActionValid(userActionDTO, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ACTION_NEW_WORK_REASON_AND_SESSION_DOES_NOT_EXIST);
    }

    @Test
    void givenValidInputAndWithNoRoleActionsForUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setRoleActions(null);

        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getUserActionDTOWithReservation(), userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);
    }

    @Test
    void givenValidInputAndWithNewWorkReasonForUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setNewWorkReasons(null);

        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getUserActionDTOWithReservation(), userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE);
    }

    @Test
    void givenValidInputAndWithDifferentSessionIdForUser_whenIsUserActionValidIsInvoked_thenFalseIsReturned() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.getReservationsDTO().setUserSession("sessionId_1234");

        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getUserActionDTOWithReservation(), userSummaryDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenValidInputWithOnlyRoleAction_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        userActionDTO.setNewWorkReason(null);
        userActionDTO.setSessionId(null);
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        boolean isUserActionValid = validationService.isUserActionValid(userActionDTO, userSummaryDTO);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenValidInputWithOnlyNewWorkReason_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        userActionDTO.setAction(null);
        userActionDTO.setSessionId(null);
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        boolean isUserActionValid = validationService.isUserActionValid(userActionDTO, userSummaryDTO);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenValidInputWithOnlySessionId_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTOWithReservation();
        userActionDTO.setAction(null);
        userActionDTO.setNewWorkReason(null);

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.getReservationsDTO().setUserSession(TEST_USER_SESSION);

        assertThat(validationService.isUserActionValid(userActionDTO, userSummaryDTO)).isTrue();
    }

    @Test
    void givenValidInputWithNoReservation_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        userActionDTO.setAction(null);
        userActionDTO.setNewWorkReason(null);

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setReservationsDTO(null);

        boolean isUserActionValid = validationService.isUserActionValid(userActionDTO, userSummaryDTO);

        assertTrue(isUserActionValid);
    }

    @Test
    void givenUserHasNoPermissions_whenIsUserAuthorisedToEditFieldIsInvoked_thenReturnsFalse() {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();

        boolean result = validationService.isUserAuthorisedToEditField(userSummaryDTO, RestrictedField.APPEAL_CC_OUTCOME);

        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("restrictedFieldsNotEditableByUser")
    void givenUserHasNoRoleWhichHasPermissionToEdit_whenIsUserAuthorisedToEditFieldIsInvoked_thenReturnsFalse(final RestrictedField restrictedField) {
        List<RoleDataItemDTO> roleDataItems = List.of(
            new RoleDataItemDTO("CCMT CASEWORKER", RestrictedField.APPEAL_CC_OUTCOME.getField(), "N", null, null)
        );

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO(roleDataItems);

        boolean result = validationService.isUserAuthorisedToEditField(userSummaryDTO, restrictedField);

        assertFalse(result);
    }

    @Test
    void givenUserHasPermissionButPermissionIsDisabled_whenIsUserAuthorisedToEditFieldIsInvoked_thenReturnsFalse() {
        List<RoleDataItemDTO> roleDataItems = List.of(
            new RoleDataItemDTO("CCMT CASEWORKER", RestrictedField.APPEAL_CC_OUTCOME.getField(), "N", null, null),
            new RoleDataItemDTO("CCMT CASEWORKER", RestrictedField.APPEAL_RECEIVED_DATE.getField(), "N", "Y", "N"),
            new RoleDataItemDTO("CCMT CASEWORKER", RestrictedField.APPEAL_RECEIVED_DATE.getField(), "N", "N", "Y")
        );

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO(roleDataItems);
        RestrictedField restrictedField = RestrictedField.APPEAL_RECEIVED_DATE;

        boolean result = validationService.isUserAuthorisedToEditField(userSummaryDTO, restrictedField);

        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("restrictedFieldsEditableByUser")
    void givenUserHasPermission_whenIsUserAuthorisedToEditField_thenReturnsTrue(final RestrictedField restrictedField) {
        List<RoleDataItemDTO> roleDataItems = List.of(
            new RoleDataItemDTO("CCMT CASEWORKER", RestrictedField.APPEAL_CC_OUTCOME.getField(), "N", null, null),
            new RoleDataItemDTO("CCMT CASEWORKER", RestrictedField.APPEAL_RECEIVED_DATE.getField(), "Y", "Y", "N"),
            new RoleDataItemDTO("CCMT CASEWORKER", RestrictedField.APPEAL_SENTENCE_ORDER_DATE.getField(), "Y", "N", "Y"),
            new RoleDataItemDTO("CCMT CASEWORKER", RestrictedField.APPEAL_CC_WITHDRAWAL_DATE.getField(), "Y", "Y", "Y")
        );

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO(roleDataItems);

        boolean result = validationService.isUserAuthorisedToEditField(userSummaryDTO, restrictedField);

        assertTrue(result);
    }
}
