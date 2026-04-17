package uk.gov.justice.laa.crime.orchestration.mapper;

import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.EvidenceDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class PassportAssessmentMapperTest {

    @Mock
    private PassportEvidenceMapper passportEvidenceMapper;

    @InjectMocks
    private PassportAssessmentMapper passportAssessmentMapper;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenValidApiResponse_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenPassportDTOIsReturned(
            boolean withPartner) {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(withPartner);
        ApiGetPassportEvidenceResponse evidence = EvidenceDataBuilder.getApiGetPassportEvidenceResponse(withPartner);
        when(passportEvidenceMapper.apiGetPassportEvidenceResponseToIncomeEvidenceSummaryDTO(evidence))
                .thenReturn(EvidenceDataBuilder.getIncomeEvidenceSummaryDTO(withPartner));
        ApplicantDTO applicant = withPartner ? PassportAssessmentDataBuilder.getApplicantDTO() : null;
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(withPartner), evidence, applicant);

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
        softly.assertAll();
    }

    @ParameterizedTest
    @EnumSource(BenefitType.class)
    void
            givenDifferentDeclaredBenefitsInApiResponse_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenAppropriateBenefitIsReturned(
                    BenefitType benefit) {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);
        expected.setBenefitIncomeSupport(false);
        switch (benefit) {
            case BenefitType.INCOME_SUPPORT -> expected.setBenefitIncomeSupport(true);
            case BenefitType.JSA -> expected.setBenefitJobSeeker(PassportAssessmentDataBuilder.getJobSeekerDTO());
            case BenefitType.GSPC -> expected.setBenefitGaurenteedStatePension(true);
            case BenefitType.ESA -> expected.setBenefitEmploymentSupport(true);
            case BenefitType.UC -> expected.setBenefitUniversalCredit(true);
        }
        ApiGetPassportedAssessmentResponse response =
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITHOUT_PARTNER);
        response.getDeclaredBenefit().setBenefitType(benefit);
        ApiGetPassportEvidenceResponse evidence =
                EvidenceDataBuilder.getApiGetPassportEvidenceResponse(Constants.WITHOUT_PARTNER);
        when(passportEvidenceMapper.apiGetPassportEvidenceResponseToIncomeEvidenceSummaryDTO(evidence))
                .thenReturn(EvidenceDataBuilder.getIncomeEvidenceSummaryDTO(Constants.WITHOUT_PARTNER));
        PassportedDTO actual =
                passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response, evidence, null);

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
        softly.assertAll();
    }
}
