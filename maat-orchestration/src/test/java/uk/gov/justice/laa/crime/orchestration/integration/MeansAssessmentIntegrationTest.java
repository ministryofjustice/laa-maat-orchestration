package uk.gov.justice.laa.crime.orchestration.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.assertStubForCalculateContributions;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.assertStubForGetContributionsSummary;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.assertStubForInvokeStoredProcedure;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.assertStubForUpdateCrownCourtApplication;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForCalculateContributions;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForFindRepOrder;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForGetContributionsSummaries;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForGetUserSummary;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForInvokeStoredProcedure;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForRollbackMeansAssessment;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForUpdateCrownCourtApplication;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForUpdateSendToCCLF;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequest;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

import uk.gov.justice.laa.crime.common.model.meansassessment.ApiRollbackMeansAssessmentResponse;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

class MeansAssessmentIntegrationTest extends WiremockIntegrationTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/cma";
    private static final String CMA_URL = "/api/internal/v1/assessment/means";

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @AfterEach
    void cleanUp() {
        wiremock.resetAll();
    }

    @Test
    void givenValidIds_whenFindIsInvoked_thenAssessmentIsReturned() throws Exception {
        String response =
                objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiGetMeansAssessmentResponse());

        stubForOAuth();
        stubFor(get(urlMatching(CMA_URL + "/" + Constants.FINANCIAL_ASSESSMENT_ID))
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
        mvc.perform(buildRequest(HttpMethod.GET, findAssessmentUrl, false)).andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidIds_whenFindIsInvoked_thenBadRequestIsReturned() throws Exception {
        stubForOAuth();

        String findAssessmentUrl =
                String.format("%s/%d/applicantId/%s", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, "invalid-id");
        mvc.perform(buildRequest(HttpMethod.GET, findAssessmentUrl)).andExpect(status().isBadRequest());
    }

    @Test
    void givenApiClientException_whenFindIsInvoked_thenInternalServerErrorIsReturned() throws Exception {
        stubForOAuth();
        stubFor(get(urlMatching(CMA_URL + "/" + Constants.FINANCIAL_ASSESSMENT_ID))
                .willReturn(WireMock.serverError()));

        String findAssessmentUrl = String.format(
                "%s/%d/applicantId/%d", ENDPOINT_URL, Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        mvc.perform(buildRequest(HttpMethod.GET, findAssessmentUrl)).andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequestData_whenCreateIsInvoked_thenAssessmentIsCreated() throws Exception {
        String cmaResponse =
                objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiMeansAssessmentResponse());
        String ccpResponse =
                objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiUpdateApplicationResponse());
        String cccCalculateResponse =
                objectMapper.writeValueAsString(TestModelDataBuilder.getApiMaatCalculateContributionResponse());
        String cccSummariesResponse =
                objectMapper.writeValueAsString(List.of(TestModelDataBuilder.getApiContributionSummary()));
        String maatApiResponse =
                objectMapper.writeValueAsString(TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT));
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());
        String userSummaryResponse = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getUserSummaryDTO());
        String repOrderDTO = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getRepOrderDTO());

        stubForOAuth();
        wiremock.stubFor(post(urlMatching(CMA_URL))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(cmaResponse)));
        stubForUpdateCrownCourtApplication(ccpResponse);
        stubForCalculateContributions(cccCalculateResponse);
        stubForGetContributionsSummaries(cccSummariesResponse);
        stubForGetUserSummary(userSummaryResponse);
        stubForFindRepOrder(repOrderDTO);
        stubForUpdateSendToCCLF();

        stubForInvokeStoredProcedure(Scenario.STARTED, "DB_GET_APPLICATION_CORRESPONDENCE", maatApiResponse);
        stubForInvokeStoredProcedure(
                "DB_GET_APPLICATION_CORRESPONDENCE", "DB_ASSESSMENT_POST_PROCESSING_PART_1_C3", maatApiResponse);
        stubForInvokeStoredProcedure(
                "DB_ASSESSMENT_POST_PROCESSING_PART_1_C3", "DB_PRE_UPDATE_CC_APPLICATION", maatApiResponse);
        stubForInvokeStoredProcedure(
                "DB_PRE_UPDATE_CC_APPLICATION", "DB_ASSESSMENT_POST_PROCESSING_PART_2", maatApiResponse);
        stubForInvokeStoredProcedure("DB_ASSESSMENT_POST_PROCESSING_PART_2", maatApiResponse);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk());
        assertStubForUpdateCrownCourtApplication(1);
        assertStubForCalculateContributions(1);
        assertStubForGetContributionsSummary(1, TestModelDataBuilder.REP_ID);
        assertStubForInvokeStoredProcedure(4);
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
    void givenErrorCallingMaatApi_whenCreateIsInvoked_thenServerErrorIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());
        String cmaRollbackResponse = objectMapper.writeValueAsString(new ApiRollbackMeansAssessmentResponse());

        stubForOAuth();
        wiremock.stubFor(post(urlMatching(CMA_URL)).willReturn(WireMock.serverError()));
        stubForRollbackMeansAssessment(cmaRollbackResponse);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void givenValidRequestData_whenUpdateIsInvoked_thenAssessmentIsUpdated() throws Exception {
        String cmaResponse =
                objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiMeansAssessmentResponse());
        String ccpResponse =
                objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiUpdateApplicationResponse());
        String cccCalculateResponse =
                objectMapper.writeValueAsString(TestModelDataBuilder.getApiMaatCalculateContributionResponse());
        String cccSummariesResponse =
                objectMapper.writeValueAsString(List.of(TestModelDataBuilder.getApiContributionSummary()));
        String maatApiResponse =
                objectMapper.writeValueAsString(TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT));
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());
        String userSummaryResponse = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getUserSummaryDTO());
        String repOrderDTO = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getRepOrderDTO());

        stubForOAuth();
        wiremock.stubFor(put(urlMatching(CMA_URL))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(cmaResponse)));
        stubForUpdateCrownCourtApplication(ccpResponse);
        stubForCalculateContributions(cccCalculateResponse);
        stubForGetContributionsSummaries(cccSummariesResponse);
        stubForGetUserSummary(userSummaryResponse);
        stubForFindRepOrder(repOrderDTO);
        stubForUpdateSendToCCLF();

        stubForInvokeStoredProcedure(Scenario.STARTED, "DB_GET_APPLICATION_CORRESPONDENCE", maatApiResponse);
        stubForInvokeStoredProcedure(
                "DB_GET_APPLICATION_CORRESPONDENCE", "DB_ASSESSMENT_POST_PROCESSING_PART_1_C3", maatApiResponse);
        stubForInvokeStoredProcedure(
                "DB_ASSESSMENT_POST_PROCESSING_PART_1_C3", "DB_PRE_UPDATE_CC_APPLICATION", maatApiResponse);
        stubForInvokeStoredProcedure(
                "DB_PRE_UPDATE_CC_APPLICATION", "DB_ASSESSMENT_POST_PROCESSING_PART_2", maatApiResponse);
        stubForInvokeStoredProcedure("DB_ASSESSMENT_POST_PROCESSING_PART_2", maatApiResponse);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk());
        assertStubForUpdateCrownCourtApplication(1);
        assertStubForCalculateContributions(1);
        assertStubForGetContributionsSummary(1, TestModelDataBuilder.REP_ID);
        assertStubForInvokeStoredProcedure(4);
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
    void givenErrorCallingMaatApi_whenUpdateIsInvoked_thenServerErrorIsReturned() throws Exception {
        String requestBody = objectMapper.writeValueAsString(MeansAssessmentDataBuilder.buildWorkFlowRequest());
        String cmaRollbackResponse = objectMapper.writeValueAsString(new ApiRollbackMeansAssessmentResponse());

        stubForOAuth();
        wiremock.stubFor(put(urlMatching(CMA_URL)).willReturn(WireMock.serverError()));
        stubForRollbackMeansAssessment(cmaRollbackResponse);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().is5xxServerError());
    }
}
