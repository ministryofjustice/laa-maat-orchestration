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
import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.service.MeansAssessmentOrchestrationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.APPLICANT_ID;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.FINANCIAL_ASSESSMENT_ID;
import static uk.gov.justice.laa.crime.orchestration.util.RequestBuilderUtils.buildRequest;
import static uk.gov.justice.laa.crime.orchestration.util.RequestBuilderUtils.buildRequestGivenContent;

@WebMvcTest(MeansAssessmentController.class)
@Import(OrchestrationTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class MeansAssessmentControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/cma";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeansAssessmentOrchestrationService orchestrationService;

    @Test
    void givenValidRequest_whenFindIsInvoked_thenOkResponseIsReturned() throws Exception {

        when(orchestrationService.find(anyInt(), anyInt()))
                .thenReturn(new FinancialAssessmentDTO());

        String endpoint = ENDPOINT_URL + "/" + FINANCIAL_ASSESSMENT_ID + "/applicantId/" + APPLICANT_ID;
        mvc.perform(buildRequest(HttpMethod.GET, endpoint))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenFindIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/invalidId" + "/applicantId/" + APPLICANT_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWebClientFailure_whenFindIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {
        when(orchestrationService.find(anyInt(), anyInt()))
                .thenThrow(new APIClientException());

        String endpoint = ENDPOINT_URL + "/" + FINANCIAL_ASSESSMENT_ID + "/applicantId/" + APPLICANT_ID;
        mvc.perform(buildRequest(HttpMethod.GET, endpoint))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequest_whenCreateIsInvoked_thenOkResponseIsReturned() throws Exception {

        when(orchestrationService.create(any(WorkflowRequest.class)))
                .thenReturn(new ApplicationDTO());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, objectMapper.writeValueAsString(
                        TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE)), ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWebClientFailure_whenCreateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {

        when(orchestrationService.create(any(WorkflowRequest.class)))
                .thenThrow(new APIClientException());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, objectMapper.writeValueAsString(
                        TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE)), ENDPOINT_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenOkResponseIsReturned() throws Exception {

        when(orchestrationService.update(any(WorkflowRequest.class)))
                .thenReturn(new ApplicationDTO());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, objectMapper.writeValueAsString(
                        TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE)), ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenUpdateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "requestBody", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWebClientFailure_whenUpdateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {

        when(orchestrationService.update(any(WorkflowRequest.class)))
                .thenThrow(new APIClientException());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, objectMapper.writeValueAsString(
                        TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE)), ENDPOINT_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
