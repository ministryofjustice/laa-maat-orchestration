package uk.gov.justice.laa.crime.orchestration.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiGetMeansAssessmentResponse;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequest;

@DirtiesContext
// @TestInstance(TestInstance.Lifecycle.PER_CLASS) TODO: Is this annotation needed?
@Import(OrchestrationTestConfiguration.class)
@SpringBootTest(classes = OrchestrationTestConfiguration.class, webEnvironment = DEFINED_PORT)
@AutoConfigureWireMock(port = 9999)
// @AutoConfigureObservability TODO: Is this annotation needed? Not sure what it does
public class MeansAssessmentTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/cma";
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private WireMockServer wiremock;

    @BeforeEach
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @AfterEach
    void cleanUp() {
        wiremock.resetAll();
    }

    @Test
    void givenValidIds_whenFindIsInvoked_thenAssessmentIsReturned () throws Exception {
        String response = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiGetMeansAssessmentResponse());
        String cmaFindAssessmentUrl = String.format(
                "/api/internal/v1/assessment/means/%s", Constants.FINANCIAL_ASSESSMENT_ID);

        stubForOAuth();
        wiremock.stubFor(get(urlMatching(cmaFindAssessmentUrl))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(response)));

        String orchFindAssessmentUrl = String.format(
                "%s/%d/applicantId/%d", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        mvc.perform(buildRequest(HttpMethod.GET, orchFindAssessmentUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Constants.FINANCIAL_ASSESSMENT_ID))
                .andExpect(jsonPath("$.initial.id").value(MeansAssessmentDataBuilder.INIT_MEANS_ID));
    }

    @Test
    void givenEmptyOAuthToken_whenFindIsInvoked_thenUnauthorizedAccessIsReturned() throws Exception {
        String orchFindAssessmentUrl = String.format(
                "%s/%d/applicantId/%d", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        mvc.perform(buildRequest(HttpMethod.GET, orchFindAssessmentUrl, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidIds_whenFindIsInvoked_thenBadRequestIsReturned() throws Exception {
        stubForOAuth();

        String orchFindAssessmentUrl = String.format(
                "%s/%d/applicantId/%s", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, "invalid-id");
        mvc.perform(buildRequest(HttpMethod.GET, orchFindAssessmentUrl))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenApiClientException_whenFindIsInvoked_thenInternalServerErrorIsReturned() throws Exception {
        String cmaFindAssessmentUrl = String.format(
                "/api/internal/v1/assessment/means/%s", Constants.FINANCIAL_ASSESSMENT_ID);

        stubForOAuth();
        wiremock.stubFor(get(urlMatching(cmaFindAssessmentUrl))
                .willReturn(WireMock.serverError()));

        String orchFindAssessmentUrl = String.format(
                "%s/%d/applicantId/%d", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        mvc.perform(buildRequest(HttpMethod.GET, orchFindAssessmentUrl))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequestData_whenCreateIsInvoked_thenAssessmentIsCreated () throws Exception {

    }

    @Test
    void givenEmptyOAuthToken_whenCreateIsInvoked_thenUnauthorizedAccessIsReturned() throws Exception {

    }

    @Test
    void givenInvalidRequestData_whenCreateIsInvoked_thenBadRequestIsReturned() throws Exception {

    }

    // TODO: Check that rollback is called?
    @Test
    void givenErrorCallingMaatApi_whenCreateIsInvoked_thenInternalServerErrorIsReturned() throws Exception {

    }

    @Test
    void givenValidRequestData_whenUpdateIsInvoked_thenAssessmentIsUpdated () throws Exception {

    }

    @Test
    void givenEmptyOAuthToken_whenUpdateIsInvoked_thenUnauthorizedAccessIsReturned() throws Exception {

    }

    @Test
    void givenInvalidRequestData_whenUpdateIsInvoked_thenBadRequestIsReturned() throws Exception {

    }

    // TODO: Check that rollback is called?
    @Test
    void givenErrorCallingMaatApi_whenUpdateIsInvoked_thenInternalServerErrorIsReturned() throws Exception {

    }

    // TODO: Can this be located somewhere common for any integration to test to import?
    private void stubForOAuth() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );

        wiremock.stubFor(post("/oauth2/token")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(mapper.writeValueAsString(token))));
    }
}
