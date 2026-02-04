package uk.gov.justice.laa.crime.orchestration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.TEST_TRACE_ID;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.LEGACY_APPEAL_ID;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestWithTransactionId;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestWithTransactionIdGivenContent;

import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.error.ErrorMessage;
import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.IojAppealsOrchestrationService;
import uk.gov.justice.laa.crime.orchestration.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.orchestration.utils.WebClientTestUtils;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(IojAppealController.class)
@Import(OrchestrationTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class IojAppealControllerTest {
    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/ioj-appeals";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IojAppealsOrchestrationService orchestrationService;

    @MockitoBean
    private TraceIdHandler traceIdHandler;

    @Test
    void givenValidRequest_whenFindIsInvoked_thenOkResponseIsReturned() throws Exception {
        when(orchestrationService.find(anyInt())).thenReturn(new IOJAppealDTO());

        String endpoint = ENDPOINT_URL + "/" + LEGACY_APPEAL_ID;
        mvc.perform(buildRequestWithTransactionId(HttpMethod.GET, endpoint, true))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenFindIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestWithTransactionId(HttpMethod.GET, ENDPOINT_URL + "/invalidId", true))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWebClientNotFoundStatus_whenFindIsInvoked_thenNotFoundResponseIsReturned() throws Exception {
        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.NOT_FOUND);

        when(orchestrationService.find(anyInt())).thenThrow(webClientResponseException);

        String endpoint = ENDPOINT_URL + "/" + LEGACY_APPEAL_ID;
        mvc.perform(buildRequestWithTransactionId(HttpMethod.GET, endpoint, true))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenWebClientInternalServerErrorStatus_whenFindIsInvoked_thenInternalServerErrorResponseIsReturned()
            throws Exception {
        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(orchestrationService.find(anyInt())).thenThrow(webClientResponseException);

        String endpoint = ENDPOINT_URL + "/" + LEGACY_APPEAL_ID;
        mvc.perform(buildRequestWithTransactionId(HttpMethod.GET, endpoint, true))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequest_whenCreateIsInvoked_thenOkResponseIsReturned() throws Exception {
        when(orchestrationService.create(any(WorkflowRequest.class))).thenReturn(new ApplicationDTO());

        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkFlowRequest());
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.POST, "", ENDPOINT_URL, true))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource(WebClientTestUtils.ERROR_LIST_DATA)
    void givenBadRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturned(List<ErrorMessage> errorMessages)
            throws Exception {
        when(traceIdHandler.getTraceId()).thenReturn(TEST_TRACE_ID);
        when(orchestrationService.create(any(WorkflowRequest.class)))
                .thenThrow(WebClientTestUtils.getProblemDetailWebClientResponseException(
                        HttpStatus.BAD_REQUEST, errorMessages));

        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkFlowRequest());
        // ProblemDetail should be converted to ErrorDTO, and returned as "application/json"
        ResultActions resultActions = mvc.perform(
                        buildRequestWithTransactionIdGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.traceId").value(TEST_TRACE_ID))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()));
        WebClientTestUtils.checkMessageListFromProblemDetail(errorMessages, resultActions);
    }

    @ParameterizedTest
    @MethodSource(WebClientTestUtils.MESSAGE_LIST_DATA)
    void givenInvalidRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturnedPopulated(List<String> messageList)
            throws Exception {
        when(traceIdHandler.getTraceId()).thenReturn(TEST_TRACE_ID);
        when(orchestrationService.create(any(WorkflowRequest.class)))
                .thenThrow(
                        WebClientTestUtils.getErrorDTOWebClientResponseException(HttpStatus.BAD_REQUEST, messageList));

        String requestBody = objectMapper.writeValueAsString(
                TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));

        ResultActions result = mvc.perform(
                        buildRequestWithTransactionIdGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.traceId").value(TEST_TRACE_ID))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()));
        WebClientTestUtils.checkErrorMessageFromErrorDTO(messageList, result);
    }

    @Test
    void givenWebClientInternalServerErrorStatus_whenCreateIsInvoked_thenInternalServerErrorResponseIsReturned()
            throws Exception {
        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        when(orchestrationService.create(any(WorkflowRequest.class))).thenThrow(webClientResponseException);

        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkFlowRequest());
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isInternalServerError());
    }
}
