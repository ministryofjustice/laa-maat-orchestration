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
import uk.gov.justice.laa.crime.orchestration.utils.TestUtils;

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
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);

        stubForOAuth();
        stubForFindPassportAssessment(objectMapper.writeValueAsString(
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITHOUT_PARTNER)));
        stubForFindPassportEvidence(objectMapper.writeValueAsString(
                EvidenceDataBuilder.getApiGetPassportEvidenceResponse(Constants.WITHOUT_PARTNER)));
        stubForGetApplicant(objectMapper.writeValueAsString(PassportAssessmentDataBuilder.getApplicantDTO()));

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + PASSPORT_ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.passportedId").value(expected.getPassportedId()))
                .andExpect(jsonPath("$.cmuId").value(expected.getCmuId()))
                .andExpect(jsonPath("$.usn").value(expected.getUsn()))
                .andExpect(jsonPath("$.date").value(TestUtils.formatDate(expected.getDate())))
                .andExpect(jsonPath("$.assessementStatusDTO.status")
                        .value(expected.getAssessementStatusDTO().getStatus()))
                .andExpect(jsonPath("$.passportConfirmationDTO.confirmation")
                        .value(expected.getPassportConfirmationDTO().getConfirmation()))
                .andExpect(jsonPath("$.newWorkReason.code")
                        .value(expected.getNewWorkReason().getCode()))
                .andExpect(jsonPath("$.reviewType.code")
                        .value(expected.getReviewType().getCode()))
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
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.evidenceDueDate")
                        .value(TestUtils.formatDate(
                                expected.getPassportSummaryEvidenceDTO().getEvidenceDueDate())))
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.evidenceReceivedDate")
                        .value(TestUtils.formatDate(
                                expected.getPassportSummaryEvidenceDTO().getEvidenceReceivedDate())))
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.upliftAppliedDate")
                        .value(TestUtils.formatDate(
                                expected.getPassportSummaryEvidenceDTO().getUpliftAppliedDate())))
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.upliftRemovedDate")
                        .value(TestUtils.formatDate(
                                expected.getPassportSummaryEvidenceDTO().getUpliftRemovedDate())))
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.incomeEvidenceNotes")
                        .value(expected.getPassportSummaryEvidenceDTO().getIncomeEvidenceNotes()))
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.firstReminderDate")
                        .value(TestUtils.formatDate(
                                expected.getPassportSummaryEvidenceDTO().getFirstReminderDate())))
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.secondReminderDate")
                        .value(TestUtils.formatDate(
                                expected.getPassportSummaryEvidenceDTO().getSecondReminderDate())))
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.applicantIncomeEvidenceList[0].id")
                        .value(Constants.APPLICANT_EVIDENCE_ID))
                .andExpect(jsonPath("$.passportSummaryEvidenceDTO.extraEvidenceList[0].otherText")
                        .value(Constants.OTHER_DESCRIPTION))
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
