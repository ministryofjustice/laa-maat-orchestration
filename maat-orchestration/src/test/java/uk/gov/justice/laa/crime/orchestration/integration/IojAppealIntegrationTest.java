package uk.gov.justice.laa.crime.orchestration.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.LEGACY_APPEAL_ID;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForFindIojAppeal;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForOAuth;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequest;

import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(OrchestrationTestConfiguration.class)
@SpringBootTest(classes = OrchestrationTestConfiguration.class, webEnvironment = DEFINED_PORT)
@AutoConfigureWireMock(port = 9999)
@AutoConfigureObservability
class IojAppealIntegrationTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/appeals";

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
                getRequestedFor(urlPathMatching("/api/internal/v1/ioj-appeals/lookup-by-legacy-id/" + LEGACY_APPEAL_ID)));
    }
}
