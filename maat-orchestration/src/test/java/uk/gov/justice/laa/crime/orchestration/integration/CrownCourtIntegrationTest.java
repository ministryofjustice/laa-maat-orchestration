package uk.gov.justice.laa.crime.orchestration.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getApplicationDTO;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.assertStubForInvokeStoredProcedure;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.assertStubForUpdateCrownCourtOutcome;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.assertStubForUpdateSendToCCLF;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForGetRepOrders;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForGetUserSummary;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForInvokeStoredProcedure;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForOAuth;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForUpdateCrownCourtOutcome;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForUpdateSendToCCLF;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FeatureToggleDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggle;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggleAction;

import java.util.List;

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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(OrchestrationTestConfiguration.class)
@SpringBootTest(classes = OrchestrationTestConfiguration.class, webEnvironment = DEFINED_PORT)
@AutoConfigureWireMock(port = 9999)
@AutoConfigureObservability
class CrownCourtIntegrationTest {
    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/crown-court";
    private static final String MAAT_API_ASSESSMENT_URL = "/api/internal/v1/assessment";

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
    void givenNoOAuthToken_whenUpdateIsInvoked_thenUnauthorisedResponseIsReturned() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenBadRequest_whenUpdateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        request.setApplicationDTO(null);
        String requestBody = objectMapper.writeValueAsString(request);

        stubForOAuth();

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenErrorCallingMaatApi_whenUpdateIsInvoked_thenServerErrorResponseIsReturned() throws Exception {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        request.setApplicationDTO(getApplicationDTO());

        String requestBody = objectMapper.writeValueAsString(request);

        stubForOAuth();
        stubFor(post(urlMatching(MAAT_API_ASSESSMENT_URL + "/execute-stored-procedure"))
                .willReturn(WireMock.serverError()));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenCrownCourtIsUpdated() throws Exception {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        ApiUpdateCrownCourtOutcomeResponse updateCrownCourtOutcomeResponse =
                TestModelDataBuilder.getApiUpdateCrownCourtResponse();
        ApplicationDTO applicationDTO = getApplicationDTO();
        UserSummaryDTO userSummaryDTO = TestModelDataBuilder.getUserSummaryDTO();
        userSummaryDTO.setFeatureToggle(List.of(FeatureToggleDTO.builder()
                .featureName(FeatureToggle.MAAT_POST_ASSESSMENT_PROCESSING.getName())
                .action(FeatureToggleAction.READ.getName())
                .isEnabled("Y")
                .build()));

        request.setApplicationDTO(applicationDTO);

        String requestBody = objectMapper.writeValueAsString(request);

        stubForOAuth();
        stubForInvokeStoredProcedure(objectMapper.writeValueAsString(applicationDTO));
        stubForUpdateCrownCourtOutcome(objectMapper.writeValueAsString(updateCrownCourtOutcomeResponse));
        stubForGetUserSummary(objectMapper.writeValueAsString(userSummaryDTO));
        stubForGetRepOrders(objectMapper.writeValueAsString(TestModelDataBuilder.buildRepOrderDTO(null)));
        stubForUpdateSendToCCLF();

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk());

        assertStubForUpdateCrownCourtOutcome(1);
        assertStubForUpdateSendToCCLF(1);
        assertStubForInvokeStoredProcedure(3);
    }
}
