package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.RepStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.laa.crime.orchestration.service.ValidationService.CANNOT_MODIFY_APPLICATION_ERROR;

class ValidationServiceTest {

    private static final LocalDateTime APPLICATION_TIMESTAMP = LocalDateTime.parse("2024-01-27T10:15:30");
    private static final LocalDateTime REP_ORDER_MODIFIED_TIMESTAMP = LocalDateTime.parse("2023-06-27T10:15:30");
    private static final LocalDate REP_ORDER_CREATED_TIMESTAMP = LocalDate.of(2024, Month.JANUARY, 8);
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
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

    private static Stream<Arguments> validateApplicationTimestamp() {
        return Stream.of(
                Arguments.of(WorkflowRequest
                                .builder()
                                .applicationDTO(
                                        ApplicationDTO.
                                                builder().
                                                timestamp(DateUtil.toTimeStamp(APPLICATION_TIMESTAMP))
                                                .build()).build(),
                        RepOrderDTO
                                .builder()
                                .dateModified(REP_ORDER_MODIFIED_TIMESTAMP).build()),
                Arguments.of(WorkflowRequest
                                .builder()
                                .applicationDTO(
                                        ApplicationDTO.
                                                builder().
                                                timestamp(DateUtil.toTimeStamp(APPLICATION_TIMESTAMP))
                                                .build()).build(),
                        RepOrderDTO
                                .builder()
                                .dateCreated(REP_ORDER_CREATED_TIMESTAMP)
                                .dateModified(null).build())
        );
    }

    private static Stream<Arguments> validateApplicationStatus() {
        return Stream.of(
                Arguments.of(WorkflowRequest
                                .builder()
                                .applicationDTO(
                                        ApplicationDTO.
                                                builder().
                                                timestamp(DateUtil.toTimeStamp(APPLICATION_TIMESTAMP))
                                                .statusDTO(
                                                        RepStatusDTO
                                                                .builder()
                                                                .updateAllowed(false)
                                                                .build())
                                                .build())
                                .build(),
                        RepOrderDTO
                                .builder()
                                .dateModified(APPLICATION_TIMESTAMP)
                                .rorsStatus(RepOrderStatus.ERR.getCode())
                                .build())
        );
    }

    private static Stream<Arguments> validateApplicationStatusNoException() {
        return Stream.of(
                Arguments.of(WorkflowRequest
                                .builder()
                                .applicationDTO(
                                        ApplicationDTO.
                                                builder().
                                                timestamp(DateUtil.toTimeStamp(APPLICATION_TIMESTAMP))
                                                .statusDTO(
                                                        RepStatusDTO
                                                                .builder()
                                                                .updateAllowed(true)
                                                                .build())
                                                .build())
                                .build(),
                        RepOrderDTO
                                .builder()
                                .dateModified(APPLICATION_TIMESTAMP)
                                .rorsStatus(RepOrderStatus.ERR.getCode())
                                .build()),
                Arguments.of(WorkflowRequest
                                .builder()
                                .applicationDTO(
                                        ApplicationDTO.
                                                builder().
                                                timestamp(DateUtil.toTimeStamp(APPLICATION_TIMESTAMP))
                                                .statusDTO(
                                                        RepStatusDTO
                                                                .builder()
                                                                .updateAllowed(false)
                                                                .build())
                                                .build())
                                .build(),
                        RepOrderDTO
                                .builder()
                                .dateModified(APPLICATION_TIMESTAMP)
                                .rorsStatus(null)
                                .build()),
                Arguments.of(WorkflowRequest
                                .builder()
                                .applicationDTO(
                                        ApplicationDTO.
                                                builder().
                                                timestamp(DateUtil.toTimeStamp(APPLICATION_TIMESTAMP))
                                                .statusDTO(
                                                        RepStatusDTO
                                                                .builder()
                                                                .updateAllowed(true)
                                                                .build())
                                                .build())
                                .build(),
                        RepOrderDTO
                                .builder()
                                .dateModified(APPLICATION_TIMESTAMP)
                                .rorsStatus(RepOrderStatus.CURR.getCode())
                                .build())

        );
    }
}
