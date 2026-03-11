package uk.gov.justice.laa.crime.orchestration.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestWithTransactionId;

import uk.gov.justice.laa.crime.orchestration.config.OrchestrationTestConfiguration;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.service.orchestration.PassportAssessmentOrchestrationService;
import uk.gov.justice.laa.crime.orchestration.tracing.TraceIdHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PassportAssessmentController.class)
@Import(OrchestrationTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PassportAssessmentControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/orchestration/passport";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PassportAssessmentOrchestrationService passportAssessmentOrchestrationService;

    @MockitoBean
    private TraceIdHandler traceIdHandler;

    @Test
    void givenValidLegacyId_whenFindIsInvoked_thenOkResponseIsReturned() throws Exception {
        PassportedDTO dto = PassportAssessmentDataBuilder.getPassportedDTO(false);

        when(passportAssessmentOrchestrationService.find(Constants.PASSPORT_ASSESSMENT_ID))
                .thenReturn(dto);

        mvc.perform(buildRequestWithTransactionId(
                        HttpMethod.GET, ENDPOINT_URL + "/" + Constants.PASSPORT_ASSESSMENT_ID, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidLegacyId_whenFindIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(buildRequestWithTransactionId(HttpMethod.GET, ENDPOINT_URL + "/inv4l1d1d", false))
                .andExpect(status().isBadRequest());
    }
}
