package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;

@ExtendWith(SoftAssertionsExtension.class)
class MeansAssessmentMapperTest {
    UserMapper userMapper = new UserMapper();
    MeansAssessmentMapper meansAssessmentMapper = new MeansAssessmentMapper(userMapper);
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiFindHardshipResponseWithSection_whenFindHardshipResponseToHardshipDTOIsInvoked_thenMappingIsCorrect() {
        FinancialAssessmentDTO actual =
                meansAssessmentMapper.getMeansAssessmentResponseToFinancialAssessmentDto(
                        MeansAssessmentDataBuilder.getApiGetMeansAssessmentResponse(), Constants.APPLICANT_ID);
        FinancialAssessmentDTO expected = MeansAssessmentDataBuilder.getFinancialAssessmentDto();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}
