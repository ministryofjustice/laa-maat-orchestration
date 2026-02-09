package uk.gov.justice.laa.crime.orchestration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.APPLICANT_ID;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.FINANCIAL_ASSESSMENT_ID;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.TEST_TRACE_ID;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestWithTransactionId;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestWithTransactionIdGivenContent;

import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.error.ErrorMessage;
import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.MeansAssessmentOrchestrationService;
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

@WebMvcTest(MeansAssessmentController.class)
@Import(OrchestrationTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class MeansAssessmentControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/cma";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MeansAssessmentOrchestrationService orchestrationService;

    @MockitoBean
    private TraceIdHandler traceIdHandler;

    @Test
    void givenValidRequest_whenFindIsInvoked_thenOkResponseIsReturned() throws Exception {

        when(orchestrationService.find(anyInt(), anyInt())).thenReturn(new FinancialAssessmentDTO());

        String endpoint = ENDPOINT_URL + "/" + FINANCIAL_ASSESSMENT_ID + "/applicantId/" + APPLICANT_ID;
        mvc.perform(buildRequestWithTransactionId(HttpMethod.GET, endpoint, true))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenFindIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestWithTransactionId(
                        HttpMethod.GET, ENDPOINT_URL + "/invalidId" + "/applicantId/" + APPLICANT_ID, true))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWebClientFailure_whenFindIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {

        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(orchestrationService.find(anyInt(), anyInt())).thenThrow(webClientResponseException);

        String endpoint = ENDPOINT_URL + "/" + FINANCIAL_ASSESSMENT_ID + "/applicantId/" + APPLICANT_ID;
        mvc.perform(buildRequestWithTransactionId(HttpMethod.GET, endpoint, true))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequest_whenCreateIsInvoked_thenOkResponseIsReturned() throws Exception {

        when(orchestrationService.create(any(WorkflowRequest.class))).thenReturn(new ApplicationDTO());

        String requestBody = objectMapper.writeValueAsString(
                TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenEmptyRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.POST, "", ENDPOINT_URL, true))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource(WebClientTestUtils.MESSAGE_LIST_DATA)
    void givenInvalidRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturned(List<String> messageList)
            throws Exception {
        when(traceIdHandler.getTraceId()).thenReturn(TEST_TRACE_ID);
        String requestBody = objectMapper.writeValueAsString(
                TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));

        when(orchestrationService.create(any(WorkflowRequest.class)))
                .thenThrow(
                        WebClientTestUtils.getErrorDTOWebClientResponseException(HttpStatus.BAD_REQUEST, messageList));

        ResultActions result = mvc.perform(
                        buildRequestWithTransactionIdGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.traceId").value(TEST_TRACE_ID))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()));
        WebClientTestUtils.checkErrorMessageFromErrorDTO(messageList, result);
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

    @Test
    void givenWebClientFailure_whenCreateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {

        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(orchestrationService.create(any(WorkflowRequest.class))).thenThrow(webClientResponseException);

        String requestBody = objectMapper.writeValueAsString(
                TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenOkResponseIsReturned() throws Exception {

        when(orchestrationService.update(any(WorkflowRequest.class))).thenReturn(new ApplicationDTO());

        String requestBody = objectMapper.writeValueAsString(
                TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenUpdateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.PUT, "", ENDPOINT_URL, true))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWebClientFailure_whenUpdateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {

        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(orchestrationService.update(any(WorkflowRequest.class))).thenThrow(webClientResponseException);

        String requestBody = objectMapper.writeValueAsString(
                TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
