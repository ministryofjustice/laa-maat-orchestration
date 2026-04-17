package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.EvidenceDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.EvidenceApiService;
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
    private EvidenceApiService evidenceApiService;

    @Mock
    private PassportAssessmentMapper passportAssessmentMapper;

    @Mock
    private MaatCourtDataApiService maatCourtDataApiService;

    @InjectMocks
    private PassportAssessmentService passportAssessmentService;

    @Test
    void givenValidIdWithPartner_whenFindIsInvoked_thenPassportedDTOIsReturned() {
        ApiGetPassportedAssessmentResponse assessment =
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITH_PARTNER);
        ApiGetPassportEvidenceResponse evidence = EvidenceDataBuilder.getApiGetPassportEvidenceResponse();
        ApplicantDTO applicant = PassportAssessmentDataBuilder.getApplicantDTO();
        PassportedDTO passportedDTO = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITH_PARTNER);

        when(assessmentApiService.findPassportAssessment(Constants.PASSPORT_ASSESSMENT_ID))
                .thenReturn(assessment);
        when(evidenceApiService.getPassportEvidence(Constants.PASSPORT_ASSESSMENT_ID))
                .thenReturn(evidence);
        when(maatCourtDataApiService.getApplicant(Constants.PARTNER_ID)).thenReturn(applicant);
        when(passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                        assessment, evidence, applicant))
                .thenReturn(passportedDTO);

        assertThat(passportAssessmentService.find(Constants.PASSPORT_ASSESSMENT_ID))
                .isEqualTo(passportedDTO);
    }

    @Test
    void givenValidId_whenFindIsInvoked_thenPassportDTOIsReturned() {
        ApiGetPassportedAssessmentResponse assessment =
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITHOUT_PARTNER);
        ApiGetPassportEvidenceResponse evidence = EvidenceDataBuilder.getApiGetPassportEvidenceResponse();
        PassportedDTO passportedDTO = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);

        when(assessmentApiService.findPassportAssessment(Constants.PASSPORT_ASSESSMENT_ID))
                .thenReturn(assessment);
        when(evidenceApiService.getPassportEvidence(Constants.PASSPORT_ASSESSMENT_ID))
                .thenReturn(evidence);
        when(passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(assessment, evidence, null))
                .thenReturn(passportedDTO);

        verifyNoInteractions(maatCourtDataApiService);
        assertThat(passportAssessmentService.find(Constants.PASSPORT_ASSESSMENT_ID))
                .isEqualTo(passportedDTO);
    }
}
