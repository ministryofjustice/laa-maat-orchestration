package uk.gov.justice.laa.crime.orchestration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.commons.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.EvidenceOrchestrationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestWithTransactionIdGivenContent;

@WebMvcTest(controllers = EvidenceController.class)
@Import(OrchestrationTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class EvidenceControllerTest {
    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/evidence/income";

    @MockBean
    private EvidenceOrchestrationService evidenceOrchestrationService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private TraceIdHandler traceIdHandler;

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenOkResponseIsReturned() throws Exception {
        when(evidenceOrchestrationService.updateIncomeEvidence(any(WorkflowRequest.class)))
                .thenReturn(new ApplicationDTO());
        String requestBody = objectMapper
                .writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mockMvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenUpdateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mockMvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.PUT, "", ENDPOINT_URL, true))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWebClientFailure_whenUpdateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {

        when(evidenceOrchestrationService.updateIncomeEvidence(any(WorkflowRequest.class)))
                .thenThrow(new APIClientException());

        String requestBody = objectMapper
                .writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mockMvc.perform(buildRequestWithTransactionIdGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL, true))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
