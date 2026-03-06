package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

@ExtendWith(MockitoExtension.class)
class PassportAssessmentServiceTest {

    @Mock
    private AssessmentApiService assessmentApiService;
    @Mock
    private PassportAssessmentMapper passportAssessmentMapper;
    @Mock
    private MaatCourtDataApiService maatCourtDataApiService;
    @InjectMocks
    private PassportAssessmentService passportAssessmentService;

    @Test
    void givenValidLegacyIdWithPartner_whenFindIsInvoked_thenPassportedDTOIsReturned() {
        ApiGetPassportedAssessmentResponse response =
            PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(true);
        ApplicantDTO applicantDTO = PassportAssessmentDataBuilder.getApplicantDTO();
        PassportedDTO passportedDTO = PassportAssessmentDataBuilder.getPassportedDTO();

        when(assessmentApiService.findPassportAssessment(PASSPORT_ASSESSMENT_ID))
            .thenReturn(response);
        when(maatCourtDataApiService.getApplicant(PARTNER_ID)).thenReturn(applicantDTO);
        when(passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response,
            applicantDTO)).thenReturn(passportedDTO);

        assertThat(passportAssessmentService.find(PASSPORT_ASSESSMENT_ID)).isEqualTo(passportedDTO);
    }

    @Test
    void givenValidLegacyId_whenFindIsInvoked_thenPassportDTOIsReturned() {
        ApiGetPassportedAssessmentResponse response =
            PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(false);
        PassportedDTO passportedDTO = PassportAssessmentDataBuilder.getPassportedDTO();

        when(assessmentApiService.findPassportAssessment(PASSPORT_ASSESSMENT_ID))
            .thenReturn(response);
        when(passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response,
            null)).thenReturn(passportedDTO);

        verifyNoInteractions(maatCourtDataApiService);
        assertThat(passportAssessmentService.find(PASSPORT_ASSESSMENT_ID)).isEqualTo(passportedDTO);
    }

}
