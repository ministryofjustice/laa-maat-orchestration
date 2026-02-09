package uk.gov.justice.laa.crime.orchestration.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.dto.ErrorDTO;
import uk.gov.justice.laa.crime.orchestration.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.util.ProblemDetailUtil;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {

    private final TraceIdHandler traceIdHandler;
    private final ObjectMapper mapper;

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorDTO> onRuntimeException(WebClientResponseException exception) {
        String errorMessage;
        List<String> messageList = null;
        try {
            // check if ProblemDetail.
            if (MediaType.APPLICATION_PROBLEM_JSON.equals(exception.getHeaders().getContentType())) {
                ProblemDetail problemDetail =
                        ProblemDetailUtil.parseProblemDetailJson(exception.getResponseBodyAsString());
                messageList = ProblemDetailUtil.getErrorDetails(problemDetail);
                errorMessage = problemDetail.getDetail();
            } else {
                // attempt to read as ErrorDTO
                ErrorDTO errorDTO = mapper.readValue(exception.getResponseBodyAsString(), ErrorDTO.class);
                errorMessage = errorDTO.getMessage();
                messageList = errorDTO.getMessageList();
            }
        } catch (Exception ex) {
            log.warn("Unable to read the ErrorDTO from WebClientResponseException", ex);
            errorMessage = exception.getMessage();
        }
        return buildErrorResponse(exception.getStatusCode(), errorMessage, traceIdHandler.getTraceId(), messageList);
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ErrorDTO> onRuntimeException(WebClientRequestException exception) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                traceIdHandler.getTraceId(),
                Collections.emptyList());
    }

    private static ResponseEntity<ErrorDTO> buildErrorResponse(
            HttpStatusCode status, String message, String traceId, List<String> errorMessages) {
        log.error("Exception Occurred. Status - {}, Detail - {}, TraceId - {}", status, message, traceId);
        return new ResponseEntity<>(
                ErrorDTO.builder()
                        .traceId(traceId)
                        .code(status.toString())
                        .message(message)
                        .messageList(errorMessages)
                        .build(),
                status);
    }
}
