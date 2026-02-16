package uk.gov.justice.laa.crime.orchestration.utils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.OBJECT_MAPPER;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.TEST_TRACE_ID;

import uk.gov.justice.laa.crime.dto.ErrorDTO;
import uk.gov.justice.laa.crime.error.ErrorExtension;
import uk.gov.justice.laa.crime.error.ErrorMessage;
import uk.gov.justice.laa.crime.util.ProblemDetailUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class WebClientTestUtils {

    /** Reference to be used in the MethodSource annotations to use the messageListData content. */
    public static final String MESSAGE_LIST_DATA =
            "uk.gov.justice.laa.crime.orchestration.utils.WebClientTestUtils#messageListData";
    /**
     * Method used in tests to provide basic test data. Is called via the MESSAGE_LIST_DATA static String
     * @return Stream of Arguments which are {@literal List<String>s}.
     */
    public static Stream<Arguments> messageListData() {
        return Stream.of(
                Arguments.of(Arrays.asList("test", "data")), Arguments.of(List.of()), Arguments.of((Object) null));
    }

    /** Reference to be used in the MethodSource annotations to use the errorListData content. */
    public static final String ERROR_LIST_DATA =
            "uk.gov.justice.laa.crime.orchestration.utils.WebClientTestUtils#errorListData";
    /**
     * Method used in tests to provide basic test data. Is called via the ERROR_LIST_DATA static String
     * @return Stream of Arguments which are {@literal List<ErrorMessage>s}.
     */
    static Stream<Arguments> errorListData() {
        return Stream.of(
                Arguments.of(Arrays.asList(new ErrorMessage("fieldA", "Test"), new ErrorMessage("fieldB", "Error"))),
                Arguments.of(List.of()),
                Arguments.of((Object) null));
    }

    public static WebClientResponseException getWebClientResponseException(HttpStatus status) {
        return WebClientResponseException.create(
                status.value(), status.getReasonPhrase(), new HttpHeaders(), new byte[0], null);
    }

    public static WebClientResponseException getErrorDTOWebClientResponseException(
            HttpStatus status, List<String> messageList) throws JsonProcessingException {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .code(status.toString())
                .message(status.getReasonPhrase())
                .traceId(TEST_TRACE_ID)
                .messageList(messageList)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return WebClientResponseException.create(
                status.value(),
                status.getReasonPhrase(),
                new HttpHeaders(),
                OBJECT_MAPPER.writeValueAsBytes(errorDTO),
                null);
    }

    public static WebClientResponseException getProblemDetailWebClientResponseException(
            HttpStatus status, List<ErrorMessage> errorMessages) throws JsonProcessingException {
        ErrorExtension extension =
                ProblemDetailUtil.buildErrorExtension(status.toString(), TEST_TRACE_ID, errorMessages);
        ProblemDetail pd = ProblemDetailUtil.buildProblemDetail(status, status.getReasonPhrase(), extension);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return WebClientResponseException.create(
                status.value(), status.getReasonPhrase(), headers, OBJECT_MAPPER.writeValueAsBytes(pd), null);
    }

    /**
     * Checks the provided resultAction's response has populated the ErrorDTO's messageList as expected using the
     * parameterised messageList. Used to validate mapping and behaviour.
     * @param messageList {@literal List<String>} of all the messages that would have been present in the error.
     * @param resultActions ResultAction a mvc rest call which is to be inspected.
     */
    public static void checkErrorMessageFromErrorDTO(List<String> messageList, ResultActions resultActions)
            throws Exception {
        if (Objects.isNull(messageList)) {
            resultActions.andExpect(jsonPath("$.messageList").isEmpty());
        } else {
            resultActions
                    .andExpect(jsonPath("$.messageList.length()").value(messageList.size()))
                    .andExpect(jsonPath("$.messageList", Matchers.containsInAnyOrder(messageList.toArray())));
        }
    }

    /**
     * Checks the provided resultAction's response has populated the ErrorDTO's messageList as expected using the
     * parameterised errorMessages. Used to validate mapping and behaviour.
     * @param errorMessages {@literal List<ErrorMessage>} of all the messages that would have been present in the error.
     * @param resultActions ResultAction a mvc rest call which is to be inspected.
     */
    public static void checkMessageListFromProblemDetail(List<ErrorMessage> errorMessages, ResultActions resultActions)
            throws Exception {
        if (Objects.isNull(errorMessages) || errorMessages.isEmpty()) {
            // if empty, should have had problemDetails.detail put in place. Check for it.
            resultActions.andExpect(jsonPath("$.messageList").isEmpty());
        } else {
            List<String> messageList =
                    errorMessages.stream().map(ErrorMessage::message).toList();
            resultActions
                    .andExpect(jsonPath("$.messageList.length()").value(errorMessages.size()))
                    .andExpect(jsonPath("$.messageList", Matchers.containsInAnyOrder(messageList.toArray())));
        }
    }
}
