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
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.CrownCourtOrchestrationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.util.RequestBuilderUtils.buildRequestGivenContent;

@WebMvcTest(CrownCourtController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(OrchestrationTestConfiguration.class)
class CrownCourtControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrownCourtOrchestrationService orchestrationService;

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/crown-court";

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenOkResponseIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                WorkflowRequest.builder()
                        .applicationDTO(
                                ApplicationDTO.builder()
                                        .build()
                        ).build()
        );

        when(orchestrationService.update(any(WorkflowRequest.class)))
                .thenReturn(new ApplicationDTO());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenUpdateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWebClientFailure_whenUpdateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                WorkflowRequest.builder()
                        .applicationDTO(
                                ApplicationDTO.builder()
                                        .build()
                        ).build()
        );

        when(orchestrationService.update(any(WorkflowRequest.class)))
                .thenThrow(new APIClientException());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isInternalServerError());
    }
}
