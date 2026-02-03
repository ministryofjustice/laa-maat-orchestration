package uk.gov.justice.laa.crime.orchestration.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.enums.IojAppealAssessor;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.enums.IojAppealDecisionResult;

import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

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

    @ParameterizedTest
    @CsvSource({"JR,JUDGE", "NEW,CASEWORKER"})
    void mapsAppealAssessorBasedOnNewWorkReason(String reason, IojAppealAssessor expectedAssessor) {
        WorkflowRequest req = TestModelDataBuilder.buildWorkFlowRequest();
        req.getApplicationDTO()
                .getAssessmentDTO()
                .getIojAppeal()
                .getNewWorkReasonDTO()
                .setCode(NewWorkReason.valueOf(reason).getCode());

        ApiCreateIojAppealRequest actual = iojAppealMapper.mapIojAppealDtoToApiCreateIojAppealRequest(req);

        assertThat(actual.getIojAppeal().getAppealAssessor()).isEqualTo(expectedAssessor);
    }

    @ParameterizedTest
    @MethodSource("appealDecisionResults")
    void givenAppealDecisionResult_whenMapping_thenAppealSuccessfulIsExpected(String decisionResult, boolean expected) {
        WorkflowRequest req = TestModelDataBuilder.buildWorkFlowRequest();

        IOJAppealDTO dto = req.getApplicationDTO().getAssessmentDTO().getIojAppeal();

        dto.setAppealDecisionResult(decisionResult);

        ApiCreateIojAppealRequest actual = iojAppealMapper.mapIojAppealDtoToApiCreateIojAppealRequest(req);

        assertThat(actual.getIojAppeal().getAppealSuccessful()).isEqualTo(expected);
    }

    private static Stream<Arguments> appealDecisionResults() {
        return Stream.of(
                Arguments.of(IojAppealDecisionResult.PASS.toString(), true),
                Arguments.of(IojAppealDecisionResult.FAIL.toString(), false),
                Arguments.of("", false),
                Arguments.of(null, false));
    }
}
