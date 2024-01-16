package uk.gov.justice.laa.crime.orchestration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestConfiguration
public class WiremockStubs {

    private static final String CCP_URL = "/api/internal/v1/proceedings";
    private static final String CCC_URL = "/api/internal/v1/contribution";
    private static final String MAAT_API_URL = "/api/internal/v1/assessment/execute-stored-procedure";

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

    public static void stubForUpdateCrownCourtProceedings(String response) {
        stubFor((put(urlMatching(CCP_URL))
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response))));
    }

    public static void stubForCalculateContributions(String response) {
        stubFor(post(urlMatching(CCC_URL + "/calculate-contribution"))
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response)));
    }

    public static void stubForGetContributionsSummary(String response) {
        stubFor(get(urlMatching(CCC_URL + "/summaries"))
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response)));
    }

    public static void stubForCheckContributionsRule() {
        stubFor(post(urlMatching(CCC_URL + "/check-contribution-rule"))
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(Boolean.TRUE.toString())));
    }

    public static void stubForInvokeStoredProcedure(String response) {
        stubFor(post(urlMatching(MAAT_API_URL))
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(response)));
    }

    public static void stubForInvokeStoredProcedure(String currentState, String response) {
        stubFor(post(urlMatching(MAAT_API_URL))
            .inScenario("invokeStoredProcedure")
            .whenScenarioStateIs("DB_ASSESSMENT_POST_PROCESSING_PART_2")
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response)));
    }

    public static void stubForInvokeStoredProcedure(String currentState, String nextState, String response) {
        stubFor(post(urlMatching(MAAT_API_URL))
            .inScenario("invokeStoredProcedure")
            .whenScenarioStateIs(currentState)
            .willSetStateTo(nextState)
            .willReturn(WireMock.ok()
                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                    .withBody(response)));
    }
}
