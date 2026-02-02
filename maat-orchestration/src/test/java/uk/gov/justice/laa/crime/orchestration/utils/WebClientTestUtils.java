package uk.gov.justice.laa.crime.orchestration.utils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.OBJECT_MAPPER;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.TEST_TRACE_ID;

import uk.gov.justice.laa.crime.dto.ErrorDTO;
import uk.gov.justice.laa.crime.error.ErrorExtension;
import uk.gov.justice.laa.crime.error.ErrorMessage;
import uk.gov.justice.laa.crime.util.ProblemDetailUtil;

import java.util.List;
import java.util.Objects;

import org.hamcrest.Matchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class WebClientTestUtils {

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
            resultActions
                    .andExpect(jsonPath("$.messageList.length()").value(1))
                    .andExpect(jsonPath("$.messageList[0]").value(HttpStatus.BAD_REQUEST.getReasonPhrase()));
        } else {
            List<String> messageList =
                    errorMessages.stream().map(ErrorMessage::message).toList();
            resultActions
                    .andExpect(jsonPath("$.messageList.length()").value(errorMessages.size()))
                    .andExpect(jsonPath("$.messageList", Matchers.containsInAnyOrder(messageList.toArray())));
        }
    }
}
