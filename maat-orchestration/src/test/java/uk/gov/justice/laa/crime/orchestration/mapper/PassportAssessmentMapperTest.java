package uk.gov.justice.laa.crime.orchestration.mapper;

import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.EvidenceDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ExtraEvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;

import java.util.Comparator;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(SoftAssertionsExtension.class)
class PassportAssessmentMapperTest {

    PassportEvidenceMapper passportEvidenceMapper = new PassportEvidenceMapper();
    PassportAssessmentMapper passportAssessmentMapper = new PassportAssessmentMapper(passportEvidenceMapper);

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void
            givenValidApiResponse_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenPassportDTOIsReturned() {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITHOUT_PARTNER),
                EvidenceDataBuilder.getApiGetPassportEvidenceResponse(),
                null);

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .withComparatorForType(Comparator.comparing(EvidenceDTO::getId), EvidenceDTO.class)
                .withComparatorForType(Comparator.comparing(ExtraEvidenceDTO::getId), ExtraEvidenceDTO.class)
                .isEqualTo(expected);
    }

    @Test
    void
            givenValidApiResponseWithPartner_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenPassportDTOIsReturned() {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITH_PARTNER);
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITH_PARTNER),
                EvidenceDataBuilder.getApiGetPassportEvidenceResponse(),
                PassportAssessmentDataBuilder.getApplicantDTO());

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .withComparatorForType(Comparator.comparing(EvidenceDTO::getId), EvidenceDTO.class)
                .withComparatorForType(Comparator.comparing(ExtraEvidenceDTO::getId), ExtraEvidenceDTO.class)
                .isEqualTo(expected);
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
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                response, EvidenceDataBuilder.getApiGetPassportEvidenceResponse(), null);

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .withComparatorForType(Comparator.comparing(EvidenceDTO::getId), EvidenceDTO.class)
                .withComparatorForType(Comparator.comparing(ExtraEvidenceDTO::getId), ExtraEvidenceDTO.class)
                .isEqualTo(expected);
    }
}
