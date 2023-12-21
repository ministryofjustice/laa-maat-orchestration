package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;

@ExtendWith(SoftAssertionsExtension.class)
public class MeansAssessmentMapperTest {
    UserMapper userMapper = new UserMapper();
    MeansAssessmentMapper meansAssessmentMapper = new MeansAssessmentMapper(userMapper);
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiFindHardshipResponseWithSection_whenFindHardshipResponseToHardshipDTOIsInvoked_thenMappingIsCorrect() {
        FinancialAssessmentDTO actual =
                meansAssessmentMapper.getMeansAssessmentResponseToFinancialAssessmentDto(
                        TestModelDataBuilder.getApiGetMeansAssessmentResponse(), Constants.APPLICANT_ID);
        FinancialAssessmentDTO expected = TestModelDataBuilder.getFinancialAssessmentDto();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}
