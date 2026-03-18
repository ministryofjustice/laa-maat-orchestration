package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void givenValidIdWithPartner_whenFindIsInvoked_thenPassportedDTOIsReturned() {
        ApiGetPassportedAssessmentResponse response =
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITH_PARTNER);
        ApplicantDTO applicantDTO = PassportAssessmentDataBuilder.getApplicantDTO();
        PassportedDTO passportedDTO = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITH_PARTNER);

        when(assessmentApiService.findPassportAssessment(Constants.PASSPORT_ASSESSMENT_ID))
                .thenReturn(response);
        when(maatCourtDataApiService.getApplicant(Constants.PARTNER_ID)).thenReturn(applicantDTO);
        when(passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response, applicantDTO))
                .thenReturn(passportedDTO);

        assertThat(passportAssessmentService.find(Constants.PASSPORT_ASSESSMENT_ID))
                .isEqualTo(passportedDTO);
    }

    @Test
    void givenValidId_whenFindIsInvoked_thenPassportDTOIsReturned() {
        ApiGetPassportedAssessmentResponse response =
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITHOUT_PARTNER);
        PassportedDTO passportedDTO = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);

        when(assessmentApiService.findPassportAssessment(Constants.PASSPORT_ASSESSMENT_ID))
                .thenReturn(response);
        when(passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response, null))
                .thenReturn(passportedDTO);

        verifyNoInteractions(maatCourtDataApiService);
        assertThat(passportAssessmentService.find(Constants.PASSPORT_ASSESSMENT_ID))
                .isEqualTo(passportedDTO);
    }
}
