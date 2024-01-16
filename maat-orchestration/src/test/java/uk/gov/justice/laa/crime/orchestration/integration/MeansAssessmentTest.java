package uk.gov.justice.laa.crime.orchestration.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
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
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequest;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
// @TestInstance(TestInstance.Lifecycle.PER_CLASS) TODO: Is this annotation needed?
@Import(OrchestrationTestConfiguration.class)
@SpringBootTest(classes = OrchestrationTestConfiguration.class, webEnvironment = DEFINED_PORT)
@AutoConfigureWireMock(port = 9999)
// @AutoConfigureObservability TODO: Is this annotation needed? Not sure what it does
public class MeansAssessmentTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/cma";
    private static final String CMA_URL = "/api/internal/v1/assessment/means";
    private static final String CCP_URL = "/api/internal/v1/proceedings";
    private static final String CCC_URL = "/api/internal/v1/contribution";
    private static final String MAAT_API_URL = "/api/internal/v1/assessment/execute-stored-procedure";
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

        stubForOAuth();
        wiremock.stubFor(get(urlMatching(CMA_URL + "/" + Constants.FINANCIAL_ASSESSMENT_ID))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(response)));

        String findAssessmentUrl = String.format(
                "%s/%d/applicantId/%d", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        mvc.perform(buildRequest(HttpMethod.GET, findAssessmentUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Constants.FINANCIAL_ASSESSMENT_ID))
                .andExpect(jsonPath("$.initial.id").value(MeansAssessmentDataBuilder.INIT_MEANS_ID));
    }

    @Test
    void givenEmptyOAuthToken_whenFindIsInvoked_thenUnauthorizedAccessIsReturned() throws Exception {
        String findAssessmentUrl = String.format(
                "%s/%d/applicantId/%d", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        mvc.perform(buildRequest(HttpMethod.GET, findAssessmentUrl, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidIds_whenFindIsInvoked_thenBadRequestIsReturned() throws Exception {
        stubForOAuth();

        String findAssessmentUrl = String.format(
                "%s/%d/applicantId/%s", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, "invalid-id");
        mvc.perform(buildRequest(HttpMethod.GET, findAssessmentUrl))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenApiClientException_whenFindIsInvoked_thenInternalServerErrorIsReturned() throws Exception {
        stubForOAuth();
        wiremock.stubFor(get(urlMatching(CMA_URL + "/" + Constants.FINANCIAL_ASSESSMENT_ID))
                .willReturn(WireMock.serverError()));

        String findAssessmentUrl = String.format(
                "%s/%d/applicantId/%d", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        mvc.perform(buildRequest(HttpMethod.GET, findAssessmentUrl))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequestData_whenCreateIsInvoked_thenAssessmentIsCreated () throws Exception {
        String cmaResponse = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiMeansAssessmentResponse());
        String ccpResponse = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiUpdateApplicationResponse());
        String cccCalculateResponse = objectMapper.writeValueAsString(TestModelDataBuilder.getApiMaatCalculateContributionResponse());
        String cccSummariesResponse = objectMapper.writeValueAsString(List.of(TestModelDataBuilder.getApiContributionSummary()));
        // applicationDTO is overwritten by the applicationDTO below following first call to invokeSP
        // when this then gets mapped as part of calculate contribution it look through the list of outcomeDTOs and gets last one
        // mapper assumes last should be an appealOutcome however in test data we are only setting one outcome
        // this has outcome of CONVICTED which is not a valid appealOutcome (poss as we are only setting one outcome in test data)
        // Is only one outcome in list valid? If so then mapper should be updated as it assumes there should always be 2 outcomes
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT);
//        applicationDTO.setMagsOutcomeDTO(TestModelDataBuilder.getOutcomeDTO());
        String maatApiResponse = objectMapper.writeValueAsString(applicationDTO);
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());

        stubForOAuth();
        wiremock.stubFor(post(urlMatching(CMA_URL))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(cmaResponse)));
        wiremock.stubFor((put(urlMatching(CCP_URL))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(ccpResponse))));
        wiremock.stubFor(post(urlMatching(CCC_URL + "/calculate-contribution"))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(cccCalculateResponse)));
        wiremock.stubFor(get(urlMatching(CCC_URL + "/summaries"))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(cccSummariesResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo("DB_GET_APPLICATION_CORRESPONDENCE")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs("DB_GET_APPLICATION_CORRESPONDENCE")
                .willSetStateTo("DB_ASSESSMENT_POST_PROCESSING_PART_1_C3")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs("DB_ASSESSMENT_POST_PROCESSING_PART_1_C3")
                .willSetStateTo("DB_PRE_UPDATE_CC_APPLICATION")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs("DB_PRE_UPDATE_CC_APPLICATION")
                .willSetStateTo("DB_ASSESSMENT_POST_PROCESSING_PART_2")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs("DB_ASSESSMENT_POST_PROCESSING_PART_2")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk());
        // TODO: Verify the various stubs were called x amount of times
    }

    @Test
    void givenEmptyOAuthToken_whenCreateIsInvoked_thenUnauthorizedAccessIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidRequestData_whenCreateIsInvoked_thenBadRequestIsReturned() throws Exception {
        WorkflowRequest request = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        request.setApplicationDTO(null);
        String requestBody = objectMapper.writeValueAsString(request);

        stubForOAuth();

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenErrorCallingMaatApi_whenCreateIsInvoked_thenInternalServerErrorIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());

        stubForOAuth();
        wiremock.stubFor(post(urlMatching(CMA_URL))
                .willReturn(WireMock.serverError()));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequestData_whenUpdateIsInvoked_thenAssessmentIsUpdated () throws Exception {
        String cmaResponse = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiMeansAssessmentResponse());
        String ccpResponse = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiUpdateApplicationResponse());
        String cccCalculateResponse = objectMapper.writeValueAsString(TestModelDataBuilder.getApiMaatCalculateContributionResponse());
        String cccSummariesResponse = objectMapper.writeValueAsString(List.of(TestModelDataBuilder.getApiContributionSummary()));
        // applicationDTO is overwritten by the applicationDTO below following first call to invokeSP
        // when this then gets mapped as part of calculate contribution it look through the list of outcomeDTOs and gets last one
        // mapper assumes last should be an appealOutcome however in test data we are only setting one outcome
        // this has outcome of CONVICTED which is not a valid appealOutcome (poss as we are only setting one outcome in test data)
        // Is only one outcome in list valid? If so then mapper should be updated as it assumes there should always be 2 outcomes
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT);
//        applicationDTO.setMagsOutcomeDTO(TestModelDataBuilder.getOutcomeDTO());
        String maatApiResponse = objectMapper.writeValueAsString(applicationDTO);

        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());

        stubForOAuth();
        wiremock.stubFor(put(urlMatching(CMA_URL))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(cmaResponse)));
        wiremock.stubFor((put(urlMatching(CCP_URL))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(ccpResponse))));
        wiremock.stubFor(post(urlMatching(CCC_URL + "/calculate-contribution"))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(cccCalculateResponse)));
        wiremock.stubFor(get(urlMatching(CCC_URL + "/summaries"))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(cccSummariesResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo("DB_GET_APPLICATION_CORRESPONDENCE")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs("DB_GET_APPLICATION_CORRESPONDENCE")
                .willSetStateTo("DB_ASSESSMENT_POST_PROCESSING_PART_1_C3")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs("DB_ASSESSMENT_POST_PROCESSING_PART_1_C3")
                .willSetStateTo("DB_PRE_UPDATE_CC_APPLICATION")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs("DB_PRE_UPDATE_CC_APPLICATION")
                .willSetStateTo("DB_ASSESSMENT_POST_PROCESSING_PART_2")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));
        wiremock.stubFor(post(urlMatching(MAAT_API_URL))
                .inScenario("invokeStoredProcedure")
                .whenScenarioStateIs("DB_ASSESSMENT_POST_PROCESSING_PART_2")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(maatApiResponse)));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk());
        // TODO: Verify the various stubs were called x amount of times
    }

    @Test
    void givenEmptyOAuthToken_whenUpdateIsInvoked_thenUnauthorizedAccessIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidRequestData_whenUpdateIsInvoked_thenBadRequestIsReturned() throws Exception {
        WorkflowRequest request = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        request.setApplicationDTO(null);
        String requestBody = objectMapper.writeValueAsString(request);

        stubForOAuth();

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenErrorCallingMaatApi_whenUpdateIsInvoked_thenInternalServerErrorIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());

        stubForOAuth();
        wiremock.stubFor(put(urlMatching(CMA_URL))
                .willReturn(WireMock.serverError()));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isInternalServerError());
    }

    // TODO: Can this be located somewhere common for any integration test to import?
    // Need to pass in wiremock object if located in common
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
