package uk.gov.justice.laa.crime.orchestration.mapper;

import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
public class IojAppealMapperTest {

    IojAppealMapper iojAppealMapper = new IojAppealMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiFindIojAppealResponse_whenApiGetIojAppealResponseToIojAppealDTOIsInvoked_thenMappingIsCorrect() {
        IOJAppealDTO actual =
                iojAppealMapper.apiGetIojAppealResponseToIojAppealDTO(TestModelDataBuilder.getIojAppealResponse());
        IOJAppealDTO expected = TestModelDataBuilder.getIOJAppealDTO();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}
