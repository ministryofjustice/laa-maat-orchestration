package uk.gov.justice.laa.crime.orchestration.mapper;

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
    void
            givenValidApiGetPassportedAssessmentResponse_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenPassportDTOIsReturned() {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(false);
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(false), null);

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    void
            givenValidApiGetPassportedAssessmentResponseWithPartner_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenPassportDTOIsReturned() {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(true);
        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(true),
                PassportAssessmentDataBuilder.getApplicantDTO());

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}
