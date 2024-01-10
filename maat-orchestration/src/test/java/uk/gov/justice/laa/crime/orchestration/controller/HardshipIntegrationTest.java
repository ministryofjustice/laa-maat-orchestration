package uk.gov.justice.laa.crime.orchestration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
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
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.orchestration.util.RequestBuilderUtils.buildRequest;
import static uk.gov.justice.laa.crime.orchestration.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(OrchestrationTestConfiguration.class)
@SpringBootTest(classes = OrchestrationTestConfiguration.class, webEnvironment = DEFINED_PORT)
@AutoConfigureWireMock(port = 9999)
@AutoConfigureObservability
class HardshipIntegrationTest {
    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/hardship";

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private WireMockServer wiremock;

    @AfterEach
    void clean() {
        wiremock.resetAll();
    }

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    void givenAEmptyOAuthToken_whenFindIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAValidContent_whenFindIsInvoked_thenShouldReturnHardshipReview() throws Exception {

        stubForFindHardship();

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + Constants.HARDSHIP_REVIEW_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Constants.HARDSHIP_REVIEW_ID));

        verify(exactly(1), getRequestedFor(urlPathMatching("/api/internal/v1/hardship/" + Constants.HARDSHIP_REVIEW_ID)));
    }

    @Test
    void givenAEmptyOAuthToken_whenCreateIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAEmptyContent_whenCreateIsInvoked_thenFailsBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL, true))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAValidContentAndIfAnyException_whenCreateIsInvoked_thenShouldRollback() throws Exception {
        stubForOAuth();
        wiremock.stubFor(post(urlMatching("/api/internal/v1/hardship/.*"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiPerformHardshipResponse()))
                )
        );
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE)), ENDPOINT_URL))
                .andExpect(status().is5xxServerError());
        verify(exactly(1), putRequestedFor(urlPathMatching("/api/internal/v1/hardship/rollback")));

    }

    @Test
    void givenAValidContent_whenCreateIsInvoked_thenShouldCreateSuccess() throws Exception {

        stubForCreateHardship();

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE)), ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crownCourtOverviewDTO.contribution.monthlyContribs").value(150.0));

        verifyStubForCreateHardship();

    }

    @Test
    void givenAEmptyOAuthToken_whenUpdateIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAEmptyContent_whenUpdateIsInvoked_thenFailsBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, true))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAValidContentAndIfAnyException_whenUpdateIsInvoked_thenShouldRollback() throws Exception {
        stubForOAuth();
        wiremock.stubFor(put(urlMatching("/api/internal/v1/hardship/.*"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiPerformHardshipResponse()))
                )
        );
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE)), ENDPOINT_URL))
                .andExpect(status().is5xxServerError());
        verify(exactly(1), putRequestedFor(urlPathMatching("/api/internal/v1/hardship/rollback")));

    }

    @Test
    void givenAValidContent_whenUpdateIsInvoked_thenShouldSuccess() throws Exception {
        stubForUpdateHardship();
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT,
                        objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE)),
                        ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.crownCourtOverviewDTO.contribution.monthlyContribs").value(150.0));

        verifyStubForUpdateHardship();
    }

    private void stubForOAuth() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );

        wiremock.stubFor(
                post("/oauth2/token").willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(mapper.writeValueAsString(token))
                )
        );
    }

    private void stubForUpdateHardship() throws JsonProcessingException {
        wiremock.stubFor(put(urlMatching("/api/internal/v1/hardship/.*"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiPerformHardshipResponse()))
                )
        );
        stubForCalculateContribution();
        stubForOAuth();
    }

    private void stubForFindHardship() throws JsonProcessingException {

        wiremock.stubFor(get(urlMatching("/api/internal/v1/hardship/" + Constants.HARDSHIP_REVIEW_ID))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiFindHardshipResponse()))
                )
        );
        stubForOAuth();
    }

    private void stubForCreateHardship() throws JsonProcessingException {
        wiremock.stubFor(post(urlMatching("/api/internal/v1/hardship/.*"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiPerformHardshipResponse()))
                )
        );
        wiremock.stubFor(get(urlMatching("/api/internal/v1/hardship/" + Constants.HARDSHIP_REVIEW_ID))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiFindHardshipResponse()))
                )
        );
        stubForCalculateContribution();
        stubForOAuth();
    }

    private void stubForCalculateContribution() throws JsonProcessingException {
        wiremock.stubFor(post(urlMatching("/api/internal/v1/assessment/execute-stored-procedure"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApplicationDTO()))
                )
        );

        wiremock.stubFor(post(urlMatching("/api/internal/v1/contribution/check-contribution-rule"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(Boolean.TRUE.toString())
                )
        );

        wiremock.stubFor(post(urlMatching("/api/internal/v1/contribution/calculate-contribution"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiMaatCalculateContributionResponse()))
                )
        );

        wiremock.stubFor(get(urlMatching("/api/internal/v1/contribution/summaries"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(
                                        List.of(TestModelDataBuilder.getApiContributionSummary())
                                ))
                )
        );
    }

    private static void verifyStubForCreateHardship() {
        verify(exactly(1), postRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
        verify(exactly(1), getRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
        verify(exactly(3), postRequestedFor(urlPathMatching("/api/internal/v1/assessment/execute-stored-procedure")));
        verify(exactly(1), postRequestedFor(urlPathMatching("/api/internal/v1/contribution/check-contribution-rule")));
        verify(exactly(1), postRequestedFor(urlPathMatching("/api/internal/v1/contribution/calculate-contribution")));
        verify(exactly(1), getRequestedFor(urlPathMatching("/api/internal/v1/contribution/summaries")));
    }

    private static void verifyStubForUpdateHardship() {
        verify(exactly(1), putRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
        verify(exactly(3), postRequestedFor(urlPathMatching("/api/internal/v1/assessment/execute-stored-procedure")));
        verify(exactly(1), postRequestedFor(urlPathMatching("/api/internal/v1/contribution/check-contribution-rule")));
        verify(exactly(1), postRequestedFor(urlPathMatching("/api/internal/v1/contribution/calculate-contribution")));
        verify(exactly(1), getRequestedFor(urlPathMatching("/api/internal/v1/contribution/summaries")));
    }


}