package uk.gov.justice.laa.crime.orchestration.service;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserValidationDTO;
import uk.gov.justice.laa.crime.orchestration.enums.Action;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.*;
import static uk.gov.justice.laa.crime.orchestration.service.ValidationService.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {
    private static final LocalDateTime CC_WITHDRAWAL_DATETIME = LocalDateTime.of(2022, 10, 14, 0, 0, 0);
    private static final LocalDateTime CC_REP_ORDER_DATETIME = LocalDateTime.of(2022, 10, 13, 0, 0, 0);

    @InjectMocks
    private ValidationService validationService;
    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private MaatCourtDataApiService maatCourtDataApiService;

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

    @ParameterizedTest
    @MethodSource("validateApplicationTimestamp")
    void validateApplicationTimestamp(final WorkflowRequest workflowRequest, final RepOrderDTO repOrderDTO) {
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
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        Boolean isUserActionValid =
                validationService.isUserActionValid(TestModelDataBuilder.getUserValidationDTO());
        assertTrue(isUserActionValid);
    }


    @Test
    void givenInputWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getUserValidationDTOWithReservation()))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(ValidationService.USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithRoleActionNotAssignedToUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTOWithReservation();
        userValidationDTO.setAction(Action.CREATE_APPLICATION);
        assertThatThrownBy(() -> validationService.isUserActionValid(userValidationDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);

    }

    @Test
    void givenInputWithNewWorkReasonNotAssignedToUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTOWithReservation();
        userValidationDTO.setNewWorkReason(NewWorkReason.CFC);
        assertThatThrownBy(() -> validationService.isUserActionValid(userValidationDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE);
    }

    @Test
    void givenParametersWithNoExistingReservation_whenIsUserActionValidIsInvoked_thenValidationStatusIsReturned() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);
        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTO();
        userValidationDTO.setSessionId("");

        assertThatThrownBy(() -> validationService.isUserActionValid(userValidationDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndNewWorkReasonNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);

        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTOWithReservation();
        userValidationDTO.setAction(Action.CREATE_APPLICATION);
        userValidationDTO.setNewWorkReason(NewWorkReason.CFC);

        assertThatThrownBy(() -> validationService.isUserActionValid(userValidationDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION,
                        USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndNewWorkReasonNotAssigned_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);

        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTOWithReservation();
        userValidationDTO.setAction(Action.CREATE_APPLICATION);
        userValidationDTO.setNewWorkReason(NewWorkReason.CFC);
        userValidationDTO.setSessionId("");

        assertThatThrownBy(() -> validationService.isUserActionValid(userValidationDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);
    }

    @Test
    void givenInputWithRoleActionNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);

        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTOWithReservation();
        userValidationDTO.setAction(Action.CREATE_APPLICATION);

        assertThatThrownBy(() -> validationService.isUserActionValid(userValidationDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION,
                        USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInputWithNewWorkReasonNotAssignedAndWithExistingReservation_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO expectedUserSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(expectedUserSummaryDTO);

        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTOWithReservation();
        userValidationDTO.setNewWorkReason(NewWorkReason.CFC);

        assertThatThrownBy(() -> validationService.isUserActionValid(userValidationDTO))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE,
                        USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenInvalidInput_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTOInvalidValidRequest();

        assertThatThrownBy(() -> validationService.isUserActionValid(userValidationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ACTION_NEW_WORK_REASON_AND_SESSION_DOES_NOT_EXIST);
    }

    @Test
    void givenValidInputAndWithNoRoleActionsForUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setRoleActions(null);
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);

        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getUserValidationDTOWithReservation()))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);
    }

    @Test
    void givenValidInputAndWithNewWorkReasonForUser_whenIsUserActionValidIsInvoked_thenExceptionIsThrown() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setNewWorkReasons(null);
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);

        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getUserValidationDTOWithReservation()))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE);
    }

    @Test
    void givenValidInputAndWithDifferentSessionIdForUser_whenIsUserActionValidIsInvoked_thenFalseIsReturned() throws CrimeValidationException {
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.getReservationsEntity().setUserSession("sessionId_1234");
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);
        assertThatThrownBy(() -> validationService.isUserActionValid(TestModelDataBuilder.getUserValidationDTOWithReservation()))
                .isInstanceOf(CrimeValidationException.class)
                .extracting("exceptionMessage", InstanceOfAssertFactories.ITERABLE)
                .contains(USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
    }

    @Test
    void givenValidInputWithOnlyRoleAction_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTO();
        userValidationDTO.setNewWorkReason(null);
        userValidationDTO.setSessionId(null);
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);
        Boolean isUserActionValid =
                validationService.isUserActionValid(userValidationDTO);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenValidInputWithOnlyNewWorkReason_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTO();
        userValidationDTO.setAction(null);
        userValidationDTO.setSessionId(null);
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);
        Boolean isUserActionValid =
                validationService.isUserActionValid(userValidationDTO);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenValidInputWithOnlySessionId_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTOWithReservation();
        userValidationDTO.setAction(null);
        userValidationDTO.setNewWorkReason(null);

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.getReservationsEntity().setUserSession(TEST_USER_SESSION);
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);

        assertThat(validationService.isUserActionValid(userValidationDTO)).isTrue();
    }

    @Test
    void givenValidInputWithNoReservation_whenIsUserActionValidIsInvoked_thenOKResponseIsReturned() throws CrimeValidationException {
        UserValidationDTO userValidationDTO = TestModelDataBuilder.getUserValidationDTO();
        userValidationDTO.setAction(null);
        userValidationDTO.setNewWorkReason(null);

        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setReservationsEntity(null);
        when(maatCourtDataService.getUserSummary(any())).thenReturn(userSummaryDTO);

        Boolean isUserActionValid =
                validationService.isUserActionValid(userValidationDTO);
        assertTrue(isUserActionValid);
    }

    @Test
    void givenInValidInputWithoutApplicationDTO_whenUpdateSendToCCLFIsInvoked_thenValidationExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.setApplicationDTO(null);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        String action = "action";
        assertThatThrownBy(() -> validationService.updateSendToCCLF(workflowRequest, repOrderDTO, action))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Valid ApplicationDTO and RepOrderDTO is required");
    }

    @Test
    void givenInValidInputWithoutApplicantDTO_whenUpdateSendToCCLFIsInvoked_thenValidationExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().setApplicantDTO(null);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        String action = "action";
        assertThatThrownBy(() -> validationService.updateSendToCCLF(workflowRequest, repOrderDTO, action))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Valid ApplicationDTO and RepOrderDTO is required");
    }

    @Test
    void givenInValidInputWithoutRepId_whenUpdateSendToCCLFIsInvoked_thenValidationExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        repOrderDTO.setId(null);
        String action = "action";
        assertThatThrownBy(() -> validationService.updateSendToCCLF(workflowRequest, repOrderDTO, action))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Valid ApplicationDTO and RepOrderDTO is required");
    }

    @Test
    void givenInValidInputWithoutRepOrderDTO_whenUpdateSendToCCLFIsInvoked_thenValidationExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        String action = "action";
        assertThatThrownBy(() -> validationService.updateSendToCCLF(workflowRequest, null, action))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Valid ApplicationDTO and RepOrderDTO is required");
    }


    @Test
    void givenValidInputWithUpdateAction_whenUpdateSendToCCLFIsInvoked_thenNoExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        String action = "UPDATE";
        assertDoesNotThrow(() -> validationService.updateSendToCCLF(workflowRequest, repOrderDTO, action));
    }

    @Test
    void givenValidInputWithNullAction_whenUpdateSendToCCLFIsInvoked_thenNoExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        assertDoesNotThrow(() -> validationService.updateSendToCCLF(workflowRequest, repOrderDTO, null));
    }

    @Test
    void givenValidInputWithBeforeDecisionDate_whenUpdateSendToCCLFIsInvoked_thenNoExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        LocalDateTime DECISION_DATETIME = LocalDateTime.of(2000, 10, 13, 0, 0, 0);
        workflowRequest.getApplicationDTO().setDecisionDate(Date.from(DECISION_DATETIME.atZone(ZoneId.systemDefault()).toInstant()));
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        assertDoesNotThrow(() -> validationService.updateSendToCCLF(workflowRequest, repOrderDTO, null));
    }

    @Test
    void givenValidInputWithDifferentObject_whenCompareRepOrderAndApplicationDTOIsInvoked_thenOKResponseIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        assertThat(validationService.compareRepOrderAndApplicationDTO(repOrderDTO, workflowRequest.getApplicationDTO())).isFalse();
    }

    @Test
    void givenValidInputWithSameAttributes_whenCompareRepOrderAndApplicationDTOIsInvoked_thenOKResponseIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = getTestApplicationDTO(workflowRequest);
        RepOrderDTO repOrderDTO = getTestRepOrderDTO(applicationDTO);
        assertThat(validationService.compareRepOrderAndApplicationDTO(repOrderDTO, workflowRequest.getApplicationDTO())).isTrue();
    }


    @Test
    void givenValidInput_whenUpdateSendToCCLFIsInvoked_thenOKResponseIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        String action = "CREATE";
        when(maatCourtDataApiService.updateSendToCCLF(any())).thenReturn(repOrderDTO);
        assertDoesNotThrow(() -> validationService.updateSendToCCLF(workflowRequest, repOrderDTO, action));
        verify(maatCourtDataApiService).updateSendToCCLF(any());
    }

    @Test
    void givenValidInput_whenGetWithdrawalDateIsInvoked_thenOKResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcWithDrawalDate(Date.from(CC_WITHDRAWAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()));
        assertThat(validationService.getWithDrawalDate(applicationDTO)).isEqualTo(LocalDate.from(CC_WITHDRAWAL_DATETIME));
    }

    @Test
    void givenInputWithOutCCO_whenGetWithdrawalDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.setCrownCourtOverviewDTO(null);
        assertNull(validationService.getWithDrawalDate(applicationDTO));
    }

    @Test
    void givenInputWithOutCCSummary_whenGetWithdrawalDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setCrownCourtSummaryDTO(null);
        assertNull(validationService.getWithDrawalDate(applicationDTO));
    }

    @Test
    void givenInputWithOutCCWithdrawal_whenGetWithdrawalDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcWithDrawalDate(null);
        assertNull(validationService.getWithDrawalDate(applicationDTO));
    }

    @Test
    void givenValidInput_whenGetRepOrderDateIsInvoked_thenOKResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcRepOrderDate(Date.from(CC_REP_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()));
        assertThat(validationService.getRepOrderDate(applicationDTO)).isEqualTo(LocalDate.from(CC_REP_ORDER_DATETIME));
    }

    @Test
    void givenInputWithOutCCO_whenGetRepOrderDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.setCrownCourtOverviewDTO(null);
        assertNull(validationService.getRepOrderDate(applicationDTO));
    }

    @Test
    void givenInputWithOutCCSummary_whenGetRepOrderDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setCrownCourtSummaryDTO(null);
        assertNull(validationService.getRepOrderDate(applicationDTO));
    }

    @Test
    void givenInputWithOutCCRepOrder_whenGetRepOrderDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcRepOrderDate(null);
        assertNull(validationService.getRepOrderDate(applicationDTO));
    }

    @Test
    void parseValidDate() throws ParseException {
        String date = "2023-01-01";
        Date result = validationService.parseDate(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date expected = format.parse(date);
        assertEquals(expected, result);
    }

    @Test()
    void parseInvalidFormat() {
        assertThatThrownBy(() -> validationService.parseDate("invalid")).isInstanceOf(APIClientException.class);
    }

    @Test()
    void parseNullDate() {
        assertNull(validationService.parseDate(null));
    }

    @Test
    void givenValidInput_whenGetRepOrderCcOutcomeIsInvoked_thenValidResponseIsReturned() {
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        repOrderDTO.getRepOrderCCOutcome().add(TestModelDataBuilder.getRepOrderCCOutcomeDTO());
        assertEquals("CONVICTED", ValidationService.getRepOrderCcOutcome(repOrderDTO));
    }

    @Test
    void givenValidInput_whenGetAppealTypeIsInvoked_thenValidResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getAppealDTO().getAppealTypeDTO().setCode(AppealType.ACS.getCode());
        assertEquals(AppealType.ACS.getCode(), ValidationService.getAppealType(applicationDTO));
    }

    @Test
    void givenValidInput_whenGetOutcomeIsInvoked_thenValidResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcOutcome(TestModelDataBuilder.getOutcomeDTO());
        assertEquals(CrownCourtOutcome.SUCCESSFUL.toString(), ValidationService.getOutcome(applicationDTO));
    }

    @Test
    void givenValidInput_whenGetFeeLevelIsInvoked_thenValidResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getEvidenceProvisionFee().setFeeLevel("TEST");
        assertEquals("TEST", ValidationService.getFeeLevel(applicationDTO));
    }
}
