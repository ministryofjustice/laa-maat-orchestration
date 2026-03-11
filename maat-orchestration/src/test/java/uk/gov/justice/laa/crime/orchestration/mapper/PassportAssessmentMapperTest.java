package uk.gov.justice.laa.crime.orchestration.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class PassportAssessmentMapperTest {

    PassportAssessmentMapper passportAssessmentMapper = new PassportAssessmentMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenValidApiResponse_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenPassportDTOIsReturned() {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(false);
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
            PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(false), null);

        softly.assertThat(actual)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expected);
    }

    @Test
    void givenValidApiResponseWithPartner_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenPassportDTOIsReturned() {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(true);
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
            PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(true),
            PassportAssessmentDataBuilder.getApplicantDTO());

        softly.assertThat(actual)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(BenefitType.class)
    void givenDifferentDeclaredBenefitsInApiResponse_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenAppropriateBenefitIsReturned(
        BenefitType benefit) {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(false);
        expected.setBenefitIncomeSupport(false);
        switch (benefit) {
            case BenefitType.INCOME_SUPPORT -> expected.setBenefitIncomeSupport(true);
            case BenefitType.JSA ->
                expected.setBenefitJobSeeker(PassportAssessmentDataBuilder.getJobSeekerDTO());
            case BenefitType.GSPC -> expected.setBenefitGaurenteedStatePension(true);
            case BenefitType.ESA -> expected.setBenefitEmploymentSupport(true);
            case BenefitType.UC -> expected.setBenefitUniversalCredit(true);
        }

        ApiGetPassportedAssessmentResponse response = PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(
            false);
        response.getDeclaredBenefit().setBenefitType(benefit);
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
            response, null);

        softly.assertThat(actual)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expected);
    }
}
