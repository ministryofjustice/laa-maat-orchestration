package uk.gov.justice.laa.crime.orchestration.mapper;

import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class IojAppealMapperTest {

    UserMapper userMapper = new UserMapper();
    IojAppealMapper iojAppealMapper = new IojAppealMapper(userMapper);

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

    @Test
    void givenIoJAppealDTO_whenMapIojAppealDtoToApiCreateIojAppealRequestIsInvoked_thenMappingIsCorrect() {
        ApiCreateIojAppealRequest expected = TestModelDataBuilder.getApiCreateIojAppealRequest();
        ApiCreateIojAppealRequest actual =
                iojAppealMapper.mapIojAppealDtoToApiCreateIojAppealRequest(TestModelDataBuilder.buildWorkFlowRequest());

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}
