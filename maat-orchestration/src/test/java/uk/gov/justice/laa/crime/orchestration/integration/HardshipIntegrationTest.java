package uk.gov.justice.laa.crime.orchestration.integration;

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
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.*;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequest;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(OrchestrationTestConfiguration.class)
@SpringBootTest(classes = OrchestrationTestConfiguration.class, webEnvironment = DEFINED_PORT)
@AutoConfigureWireMock(port = 9999)
@AutoConfigureObservability
class HardshipIntegrationTest {
    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/hardship";
    private static final List<String> UPDATE_ROLE_ACTIONS = List.of(Action.UPDATE_MAGS_HARDSHIP.getCode(), Action.UPDATE_CROWN_HARDSHIP.getCode());
    private static final List<String> CREATE_ROLE_ACTIONS = List.of(Action.CREATE_CROWN_HARDSHIP.getCode(), Action.CREATE_MAGS_HARDSHIP.getCode());

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
    void givenAValidContentAndIfAnyException_whenCreateIsInvoked_thenShouldRollback() throws Exception {
        stubForOAuth();
        stubForGetUserSummary(objectMapper.writeValueAsString(TestModelDataBuilder.getUserSummaryDTO(CREATE_ROLE_ACTIONS, NewWorkReason.NEW)));
        stubForGetRepOrders(objectMapper.writeValueAsString(TestModelDataBuilder.buildRepOrderDTO(null)));
        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().is5xxServerError());
        verify(exactly(1), postRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
        verify(exactly(1), patchRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
    }

    @Test
    void givenMissingRoleAction_whenCreateIsInvoked_thenRollbackIsNotInvoked() throws Exception {
        stubForOAuth();
        stubForGetUserSummary(objectMapper.writeValueAsString(TestModelDataBuilder.getUserSummaryDTO(null, NewWorkReason.NEW)));
        stubForGetRepOrders(objectMapper.writeValueAsString(TestModelDataBuilder.buildRepOrderDTO(null)));
        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
        verify(exactly(1), getRequestedFor(urlPathMatching("/api/internal/v1/users/summary/.*")));
        verify(exactly(0), patchRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
    }

    @Test
    void givenMismatchingApplicationAndRepoOrderDates_whenCreateIsInvoked_thenRollbackIsNotInvoked() throws Exception {
        stubForOAuth();
        stubForGetUserSummary(objectMapper.writeValueAsString(TestModelDataBuilder.getUserSummaryDTO(UPDATE_ROLE_ACTIONS, NewWorkReason.NEW)));
        stubForGetRepOrders(objectMapper.writeValueAsString(TestModelDataBuilder.buildRepOrderDTOWithModifiedDateOf("2023-06-27T10:15:30")));
        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
            .andExpect(status().is4xxClientError());
        verify(exactly(0), getRequestedFor(urlPathMatching("/api/internal/v1/users/summary/.*")));
        verify(exactly(0), patchRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
    }

    @Test
    void givenAValidContent_whenCreateIsInvoked_thenShouldCreateSuccess() throws Exception {

        stubForCreateHardship(CourtType.CROWN_COURT);

        String requestBody = objectMapper
                .writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crownCourtOverviewDTO.contribution.monthlyContribs").value(150.0));

        verifyStubForCreateHardship(CourtType.MAGISTRATE);

    }

    @Test
    void givenAValidCrownCourtContent_whenCreateIsInvoked_thenShouldCreateSuccess() throws Exception {

        stubForCreateHardship(CourtType.CROWN_COURT);

        String requestBody = objectMapper
                .writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT));
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crownCourtOverviewDTO.contribution.monthlyContribs").value(150.0));

        verifyStubForCreateHardship(CourtType.CROWN_COURT);

    }

    @Test
    void givenAEmptyOAuthToken_whenUpdateIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAValidContentAndIfAnyException_whenUpdateIsInvoked_thenShouldRollback() throws Exception {
        stubForOAuth();
        stubForUpdateHardship();
        String requestBody = objectMapper
                .writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.CROWN_COURT));
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().is5xxServerError());
        verify(exactly(1), putRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
        verify(exactly(1), patchRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
    }

    @Test
    void givenAValidContent_whenUpdateIsInvoked_thenShouldSuccess() throws Exception {
        stubForUpdateHardship();
        String requestBody = objectMapper
            .writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.crownCourtOverviewDTO.contribution.monthlyContribs").value(150.0));

        verifyStubForUpdateHardship(CourtType.MAGISTRATE);
    }

    @Test
    void givenMissingRoleAction_whenUpdateIsInvoked_thenRollbackIsNotInvoked() throws Exception {
        stubForOAuth();
        stubForGetUserSummary(objectMapper.writeValueAsString(TestModelDataBuilder.getUserSummaryDTO(null, NewWorkReason.NEW)));
        stubForGetRepOrders(objectMapper.writeValueAsString(TestModelDataBuilder.buildRepOrderDTO(null)));
        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
        verify(exactly(1), getRequestedFor(urlPathMatching("/api/internal/v1/users/summary/.*")));
        verify(exactly(0), patchRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
    }

    @Test
    void givenMismatchingApplicationAndRepoOrderDates_whenUpdateIsInvoked_thenRollbackIsNotInvoked() throws Exception {
        stubForOAuth();
        stubForGetUserSummary(objectMapper.writeValueAsString(TestModelDataBuilder.getUserSummaryDTO(UPDATE_ROLE_ACTIONS, NewWorkReason.NEW)));
        stubForGetRepOrders(objectMapper.writeValueAsString(TestModelDataBuilder.buildRepOrderDTOWithModifiedDateOf("2023-06-27T10:15:30")));
        String requestBody = objectMapper.writeValueAsString(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
            .andExpect(status().is4xxClientError());
        verify(exactly(0), getRequestedFor(urlPathMatching("/api/internal/v1/users/summary/.*")));
        verify(exactly(0), patchRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
    }



    private void stubForUpdateHardship() throws JsonProcessingException {
        wiremock.stubFor(put(urlMatching("/api/internal/v1/hardship/.*"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiPerformHardshipResponse()))
                )
        );

        stubForInvokeStoredProcedure(objectMapper.writeValueAsString(TestModelDataBuilder.getApplicationDTO()));
        stubForCheckContributionsRule();
        stubForCalculateContributions(objectMapper.writeValueAsString(TestModelDataBuilder.getApiMaatCalculateContributionResponse()));
        stubForGetContributionsSummary(objectMapper.writeValueAsString(List.of(TestModelDataBuilder.getApiContributionSummary())));
        stubForGetUserSummary(objectMapper.writeValueAsString(TestModelDataBuilder.getUserSummaryDTO(UPDATE_ROLE_ACTIONS, NewWorkReason.NEW)));
        stubForGetRepOrders(objectMapper.writeValueAsString(TestModelDataBuilder.buildRepOrderDTO(null)));
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

    private void stubForCreateHardship(CourtType courtType) throws JsonProcessingException {
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
        stubForInvokeStoredProcedure(objectMapper.writeValueAsString(TestModelDataBuilder.getApplicationDTO()));
        stubForCheckContributionsRule();
        stubForCalculateContributions(objectMapper.writeValueAsString(TestModelDataBuilder.getApiMaatCalculateContributionResponse()));
        stubForGetContributionsSummary(objectMapper.writeValueAsString(List.of(TestModelDataBuilder.getApiContributionSummary())));
        stubForGetUserSummary(objectMapper.writeValueAsString(TestModelDataBuilder.getUserSummaryDTO(CREATE_ROLE_ACTIONS, NewWorkReason.NEW)));
        stubForGetRepOrders(objectMapper.writeValueAsString(TestModelDataBuilder.buildRepOrderDTOWithAssessorName()));
        stubForOAuth();
        if (CourtType.CROWN_COURT.equals(courtType)) {
            wiremock.stubFor(put(urlMatching("/api/internal/v1/proceedings"))
                    .willReturn(
                            WireMock.ok()
                                    .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                    .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getApiUpdateApplicationResponse()))
                    )
            );
        }
    }

    private static void verifyStubForCreateHardship(CourtType courtType) {
        verify(exactly(1), postRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
        verify(exactly(1), getRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
        assertStubForCalculateContributions(1);
        assertStubForGetContributionsSummary(1);
        if (CourtType.CROWN_COURT.equals(courtType)) {
            // TODO: Uncomment this assertion for application tracking before activating hardship orchestration
            // assertStubForHandleEformSerivce(1);
            assertStubForInvokeStoredProcedure(4);
        } else {
            assertStubForCheckContributionsRule(1);
            assertStubForInvokeStoredProcedure(2);
        }
    }


    private static void verifyStubForUpdateHardship(CourtType courtType) {
        verify(exactly(1), putRequestedFor(urlPathMatching("/api/internal/v1/hardship/.*")));
        assertStubForInvokeStoredProcedure(2);
        assertStubForCheckContributionsRule(1);
        assertStubForCalculateContributions(1);
        assertStubForGetContributionsSummary(1);
        if (CourtType.CROWN_COURT.equals(courtType)) {
            // TODO: Uncomment this assertion for application tracking before activating hardship orchestration
            // assertStubForHandleEformSerivce(1);
        }
    }

}