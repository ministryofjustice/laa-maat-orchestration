package uk.gov.justice.laa.crime.orchestration.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.LEGACY_APPEAL_ID;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.assertStubForSendApplicationTrackingResult;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForCalculateContributions;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForCreateIojAppeal;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForDetermineMagsRepDecision;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForFindIojAppeal;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForFindRepOrder;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForGetContributionsSummaries;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForGetUserSummary;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForInvokeStoredProcedure;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForSendApplicationTrackingResult;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForUpdateCrownCourtApplication;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequest;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
import com.github.tomakehurst.wiremock.stubbing.Scenario;

class IojAppealIntegrationTest extends WiremockIntegrationTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/ioj-appeals";

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @AfterEach
    void clean() {
        wiremock.resetAll();
    }

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    void givenAEmptyOAuthToken_whenFindIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL, false)).andExpect(status().isUnauthorized());
    }

    @Test
    void givenAValidAppealId_whenFindIsInvoked_thenShouldReturnIojAppeal() throws Exception {
        stubForOAuth();
        stubForFindIojAppeal(objectMapper.writeValueAsString(TestModelDataBuilder.getIojAppealResponse()));

        IOJAppealDTO expected = TestModelDataBuilder.getIOJAppealDTO();

        // Jackson returns the date with a timestamp
        DateTimeFormatter expectedDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");

        String expectedReceivedDate =
                expected.getReceivedDate().toInstant().atOffset(ZoneOffset.UTC).format(expectedDateFormat);

        String expectedDecisionDate =
                expected.getDecisionDate().toInstant().atOffset(ZoneOffset.UTC).format(expectedDateFormat);

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + LEGACY_APPEAL_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.iojId").value(expected.getIojId()))
                .andExpect(jsonPath("$.cmuId").value(expected.getCmuId()))
                .andExpect(jsonPath("$.receivedDate").value(expectedReceivedDate))
                .andExpect(jsonPath("$.decisionDate").value(expectedDecisionDate))
                .andExpect(jsonPath("$.appealSetUpResult").value(expected.getAppealSetUpResult()))
                .andExpect(jsonPath("$.appealDecisionResult").value(expected.getAppealDecisionResult()))
                .andExpect(jsonPath("$.notes").value(expected.getNotes()))
                .andExpect(jsonPath("$.appealReason").value(expected.getAppealReason()))
                .andExpect(jsonPath("$.assessmentStatusDTO").value(expected.getAssessmentStatusDTO()))
                .andExpect(jsonPath("$.newWorkReasonDTO").value(expected.getNewWorkReasonDTO()));

        verify(
                exactly(1),
                getRequestedFor(
                        urlPathMatching("/api/internal/v1/ioj-appeals/lookup-by-legacy-id/" + LEGACY_APPEAL_ID)));
    }

    @Test
    void givenValidContent_whenCreateIsInvoked_thenShouldReturnSuccessResponse() throws Exception {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();

        stubForOAuth();
        stubForCreateIojAppeal(objectMapper.writeValueAsString(TestModelDataBuilder.getApiCreateIojAppealResponse()));
        stubForFindRepOrder(objectMapper.writeValueAsString(repOrderDTO));
        stubForDetermineMagsRepDecision(
                objectMapper.writeValueAsString(TestModelDataBuilder.getDetermineMagsRepDecisionResponse()));
        stubForCalculateContributions(
                objectMapper.writeValueAsString(TestModelDataBuilder.getApiMaatCalculateContributionResponse()));
        stubForGetContributionsSummaries(
                objectMapper.writeValueAsString(List.of(TestModelDataBuilder.getApiContributionSummary())));
        stubForInvokeStoredProcedure(
                Scenario.STARTED,
                "PRE_UPDATE_CC_APPLICATION",
                objectMapper.writeValueAsString(TestModelDataBuilder.getApplicationDTO()));
        stubForInvokeStoredProcedure(
                "PRE_UPDATE_CC_APPLICATION",
                "PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE",
                objectMapper.writeValueAsString(TestModelDataBuilder.getApplicationDTO()));
        stubForInvokeStoredProcedure(objectMapper.writeValueAsString(TestModelDataBuilder.getApplicationDTO()));
        stubForUpdateCrownCourtApplication(
                objectMapper.writeValueAsString(MeansAssessmentDataBuilder.getApiUpdateApplicationResponse()));
        stubForGetUserSummary(objectMapper.writeValueAsString(
                TestModelDataBuilder.getUserSummaryDTO(List.of(Action.CREATE_IOJ.getCode()), NewWorkReason.NEW)));
        stubForSendApplicationTrackingResult();

        IOJAppealDTO expected = TestModelDataBuilder.getIOJAppealDTO();
        String requestBody = objectMapper.writeValueAsString(workflowRequest);

        DateTimeFormatter expectedDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");

        String expectedReceivedDate =
                expected.getReceivedDate().toInstant().atOffset(ZoneOffset.UTC).format(expectedDateFormat);

        String expectedDecisionDate =
                expected.getDecisionDate().toInstant().atOffset(ZoneOffset.UTC).format(expectedDateFormat);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentDTO.iojAppeal.iojId").value(expected.getIojId()))
                .andExpect(jsonPath("$.assessmentDTO.iojAppeal.cmuId").value(expected.getCmuId()))
                .andExpect(jsonPath("$.assessmentDTO.iojAppeal.receivedDate").value(expectedReceivedDate))
                .andExpect(jsonPath("$.assessmentDTO.iojAppeal.decisionDate").value(expectedDecisionDate))
                .andExpect(
                        jsonPath("$.assessmentDTO.iojAppeal.appealSetUpResult").value(expected.getAppealSetUpResult()))
                .andExpect(jsonPath("$.assessmentDTO.iojAppeal.appealDecisionResult")
                        .value(expected.getAppealDecisionResult()))
                .andExpect(jsonPath("$.assessmentDTO.iojAppeal.notes").value(expected.getNotes()))
                .andExpect(jsonPath("$.assessmentDTO.iojAppeal.appealReason").value(expected.getAppealReason()))
                .andExpect(jsonPath("$.assessmentDTO.iojAppeal.assessmentStatusDTO")
                        .value(expected.getAssessmentStatusDTO()))
                .andExpect(
                        jsonPath("$.assessmentDTO.iojAppeal.newWorkReasonDTO").value(expected.getNewWorkReasonDTO()));

        verify(exactly(1), postRequestedFor(urlPathMatching("/api/internal/v1/ioj-appeals")));
        assertStubForSendApplicationTrackingResult(1);
    }
}
