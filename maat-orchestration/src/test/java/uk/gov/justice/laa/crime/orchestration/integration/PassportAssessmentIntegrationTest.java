package uk.gov.justice.laa.crime.orchestration.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.PASSPORT_ASSESSMENT_ID;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForFindPassportAssessment;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForFindPassportEvidence;
import static uk.gov.justice.laa.crime.orchestration.utils.WiremockStubs.stubForGetApplicant;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequest;

import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.EvidenceDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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

class PassportAssessmentIntegrationTest extends WiremockIntegrationTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/passport";

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
    void givenValidId_whenFindIsInvoked_thenPassportAssessmentIsReturned() throws Exception {
        stubForOAuth();
        stubForFindPassportAssessment(objectMapper.writeValueAsString(
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITHOUT_PARTNER)));
        stubForFindPassportEvidence(objectMapper.writeValueAsString(
                EvidenceDataBuilder.getApiGetPassportEvidenceResponse(Constants.WITHOUT_PARTNER)));
        stubForGetApplicant(objectMapper.writeValueAsString(PassportAssessmentDataBuilder.getApplicantDTO()));

        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);

        DateTimeFormatter expectedDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");

        String expectedDate =
                expected.getDate().toInstant().atOffset(ZoneOffset.UTC).format(expectedDateFormat);

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + PASSPORT_ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.passportedId").value(expected.getPassportedId()))
                .andExpect(jsonPath("$.cmuId").value(expected.getCmuId()))
                .andExpect(jsonPath("$.usn").value(expected.getUsn()))
                .andExpect(jsonPath("$.date").value(expectedDate))
                .andExpect(jsonPath("$.assessementStatusDTO").value(expected.getAssessementStatusDTO()))
                .andExpect(jsonPath("$.passportConfirmationDTO").value(expected.getPassportConfirmationDTO()))
                .andExpect(jsonPath("$.newWorkReason").value(expected.getNewWorkReason()))
                .andExpect(jsonPath("$.reviewType").value(expected.getReviewType()))
                .andExpect(jsonPath("$.dwpResult").value(expected.getDwpResult()))
                .andExpect(jsonPath("$.benefitIncomeSupport").value(expected.getBenefitIncomeSupport()))
                .andExpect(jsonPath("$.benefitJobSeeker").value(expected.getBenefitJobSeeker()))
                .andExpect(
                        jsonPath("$.benefitGaurenteedStatePension").value(expected.getBenefitGaurenteedStatePension()))
                .andExpect(jsonPath("$.benefitClaimedByPartner").value(expected.getBenefitClaimedByPartner()))
                .andExpect(jsonPath("$.benefitEmploymentSupport").value(expected.getBenefitEmploymentSupport()))
                .andExpect(jsonPath("$.benefitUniversalCredit").value(expected.getBenefitUniversalCredit()))
                .andExpect(jsonPath("$.partnerDetails").value(expected.getPartnerDetails()))
                .andExpect(jsonPath("$.notes").value(expected.getNotes()))
                .andExpect(jsonPath("$.result").value(expected.getResult()))
                .andExpect(jsonPath("$.under18HeardYouthCourt").value(expected.getUnder18HeardYouthCourt()))
                .andExpect(jsonPath("$.under18HeardMagsCourt").value(expected.getUnder18HeardMagsCourt()))
                .andExpect(jsonPath("$.under18FullEducation").value(expected.getUnder18FullEducation()))
                .andExpect(jsonPath("$.under16").value(expected.getUnder16()))
                .andExpect(jsonPath("$.between1617").value(expected.getBetween1617()))
                .andExpect(jsonPath("$.whoDwpChecked").value(expected.getWhoDwpChecked()));
    }

    @Test
    void givenEmptyOAuthToken_whenFindIsInvoked_thenFailsUnauthorisedAccess() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + PASSPORT_ASSESSMENT_ID, Constants.WITHOUT_AUTH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenEmptyAssessmentResponse_whenFindIsInvoked_thenFailsNotFound() throws Exception {
        stubForOAuth();
        wiremock.stubFor(
                get(urlMatching("/api/internal/v1/passport/lookup-by-legacy-id/" + Constants.PASSPORT_ASSESSMENT_ID))
                        .willReturn(WireMock.notFound()));

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + PASSPORT_ASSESSMENT_ID))
                .andExpect(status().isNotFound());
    }
}
