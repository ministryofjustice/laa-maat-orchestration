package uk.gov.justice.laa.crime.orchestration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestConfiguration
public class WiremockStubs {

    private static final String CCP_URL = "/api/internal/v1/proceedings";
    private static final String CCC_URL = "/api/internal/v1/contribution";
    private static final String MAAT_API_APPLICATION_URL = "/api/internal/v1/application";
    private static final String MAAT_API_ASSESSMENT_URL = "/api/internal/v1/assessment";
    private static final String CMA_ROLLBACK_URL = "/api/internal/v1/assessment/means/rollback/";
    private static final String HARDSHIP_ROLLBACK_URL = "/api/internal/v1/hardship/";
    private static final String MAAT_API_USER_URL = "/api/internal/v1/users/summary/";

    private static final String CAT_URL = "/api/internal/v1/application-tracking-output-result";

    public static void stubForOAuth() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );

        stubFor(post("/oauth2/token")
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(mapper.writeValueAsString(token))));
    }

    public static void stubForUpdateCrownCourtApplication(String response) {
        stubFor((put(urlMatching(CCP_URL))
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response))));
    }

    public static void stubForUpdateCrownCourtOutcome(String response) {
        stubFor((put(urlMatching(CCP_URL + "/update-crown-court"))
            .willReturn(WireMock.ok()
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(response))));
    }

    public static void stubForGetAssessment(int financialAssessmentId, String response) {
        stubFor((get(urlMatching(MAAT_API_ASSESSMENT_URL + "/financial-assessments/" + financialAssessmentId))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(response))));
    }

    public static void assertStubForUpdateCrownCourtApplication(int times) {
        verify(exactly(times), putRequestedFor(urlPathMatching(CCP_URL)));
    }

    public static void assertStubForUpdateCrownCourtOutcome(int times) {
        verify(exactly(times), putRequestedFor(urlPathMatching(CCP_URL + "/update-crown-court")));
    }

    public static void stubForCalculateContributions(String response) {
        stubFor(post(urlMatching(CCC_URL + "/calculate-contribution"))
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response)));
    }

    public static void assertStubForCalculateContributions(int times) {
        verify(exactly(times), postRequestedFor(urlPathMatching(CCC_URL + "/calculate-contribution")));
    }

    public static void stubForGetContributionsSummary(String response) {
        stubFor(get(urlMatching(CCC_URL + "/summaries"))
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response)));
    }

    public static void assertStubForGetContributionsSummary(int times) {
        verify(exactly(times), getRequestedFor(urlPathMatching(
            CCC_URL + "/summaries/" + TestModelDataBuilder.REP_ID)));
    }

    public static void assertStubForHandleCrimeApplyService(int times) {
        verify(exactly(times), postRequestedFor(urlPathMatching(CAT_URL)));
    }

    public static void stubForCheckContributionsRule() {
        stubFor(post(urlMatching(CCC_URL + "/check-contribution-rule"))
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(Boolean.TRUE.toString())));
    }

    public static void assertStubForCheckContributionsRule(int times) {
        verify(exactly(times), postRequestedFor(urlPathMatching(CCC_URL + "/check-contribution-rule")));
    }

    public static void stubForInvokeStoredProcedure(String response) {
        stubFor(post(urlMatching(MAAT_API_ASSESSMENT_URL + "/execute-stored-procedure"))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(response)));
    }

    public static void stubForInvokeStoredProcedure(String currentState, String response) {
        stubFor(post(urlMatching(MAAT_API_ASSESSMENT_URL + "/execute-stored-procedure"))
            .inScenario("invokeStoredProcedure")
            .whenScenarioStateIs("DB_ASSESSMENT_POST_PROCESSING_PART_2")
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response)));
    }

    public static void stubForInvokeStoredProcedure(String currentState, String nextState, String response) {
        stubFor(post(urlMatching(MAAT_API_ASSESSMENT_URL + "/execute-stored-procedure"))
            .inScenario("invokeStoredProcedure")
            .whenScenarioStateIs(currentState)
            .willSetStateTo(nextState)
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response)));
    }

    public static void assertStubForInvokeStoredProcedure(int times) {
        verify(exactly(times), postRequestedFor(urlPathMatching(MAAT_API_ASSESSMENT_URL + "/execute-stored-procedure")));
    }

    public static void stubForRollbackMeansAssessment(String response) {
        stubFor((patch(urlMatching(CMA_ROLLBACK_URL + Constants.FINANCIAL_ASSESSMENT_ID))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(response))));
    }

    public static void stubForGetUserSummary(String response) {
        stubFor(get(urlMatching(MAAT_API_USER_URL + Constants.USERNAME))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(response)));
    }

    public static void stubForGetRepOrders(String response) {
        stubFor(get(urlMatching(MAAT_API_ASSESSMENT_URL + "/rep-orders/" + TestModelDataBuilder.REP_ID))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(response)));
    }

    public static void stubForUpdateSendToCCLF() {
        stubFor(put(urlMatching(MAAT_API_APPLICATION_URL + "/applicant/update-cclf"))
                .willReturn(WireMock.ok()));
    }

    public static void assertStubForUpdateSendToCCLF(int times) {
        verify(exactly(times), putRequestedFor(urlPathMatching(MAAT_API_APPLICATION_URL + "/applicant/update-cclf")));
    }
}
