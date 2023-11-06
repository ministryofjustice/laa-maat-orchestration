package uk.gov.justice.laa.maat.orchestration.controller;

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
import uk.gov.justice.laa.maat.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.maat.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.maat.orchestration.dto.ApplicationDTO;
import uk.gov.justice.laa.maat.orchestration.dto.AssessmentDTO;
import uk.gov.justice.laa.maat.orchestration.mapper.HardshipReviewMapper;
import uk.gov.justice.laa.maat.orchestration.service.HardshipService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.maat.orchestration.util.RequestBuilderUtils.buildRequestGivenContent;

@WebMvcTest(HardshipOrchestrationController.class)
@Import(OrchestrationTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class HardshipOrchestrationControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/hardship";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HardshipService hardshipService;

    @MockBean
    private HardshipReviewMapper hardshipReviewMapper;

    @Test
    void givenValidRequest_whenFindIsInvoked_thenOkResponseIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.getHardshipDTO());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL + "/get-summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenFindIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL + "/get-summary"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidRequest_whenCreateIsInvoked_thenOkResponseIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(ApplicationDTO.builder()
                .assessmentDTO(AssessmentDTO.builder().build())

                .build());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenOkResponseIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(ApplicationDTO.builder().build());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenUpdateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "requestBody", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

}
