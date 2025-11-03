package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FeatureToggleDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggle;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggleAction;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class FeatureDecisionServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    private static final String IS_C3_ENABLED_METHOD_NAME = "isC3Enabled";
    private static final String IS_MAAT_POST_ASSESSMENT_PROCESSING_ENABLED_METHOD_NAME =
            "isMaatPostAssessmentProcessingEnabled";

    @ParameterizedTest
    @ValueSource(strings = {IS_C3_ENABLED_METHOD_NAME, IS_MAAT_POST_ASSESSMENT_PROCESSING_ENABLED_METHOD_NAME})
    void givenUserDoesNotHaveFeatureToggle_whenFeatureToggleMethodIsInvoked_thenReturnFalse(String methodName)
            throws Exception {
        when(maatCourtDataService.getUserSummary(Constants.USERNAME)).thenReturn(new UserSummaryDTO());

        UserDTO userDTO = UserDTO.builder().userName(Constants.USERNAME).build();

        WorkflowRequest request = WorkflowRequest.builder().userDTO(userDTO).build();

        FeatureDecisionService featureDecisionService = new FeatureDecisionService(maatCourtDataService);
        Method method = FeatureDecisionService.class.getMethod(methodName, WorkflowRequest.class);
        boolean result = (boolean) method.invoke(featureDecisionService, request);

        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @MethodSource("userHasFeatureToggleButWithoutRequiredAction")
    void givenUserHasFeatureToggleButWithoutTheCorrectAction_whenFeatureToggleMethodIsInvoked_thenReturnFalse(
            String methodName, FeatureToggle featureToggle, FeatureToggleAction featureToggleAction) throws Exception {
        UserSummaryDTO userSummaryDTO = UserSummaryDTO.builder()
                .featureToggle(List.of(FeatureToggleDTO.builder()
                        .featureName(featureToggle.getName())
                        .action(featureToggleAction.getName())
                        .build()))
                .build();

        when(maatCourtDataService.getUserSummary(Constants.USERNAME)).thenReturn(userSummaryDTO);

        UserDTO userDTO = UserDTO.builder().userName(Constants.USERNAME).build();

        WorkflowRequest request = WorkflowRequest.builder().userDTO(userDTO).build();

        FeatureDecisionService featureDecisionService = new FeatureDecisionService(maatCourtDataService);
        Method method = FeatureDecisionService.class.getMethod(methodName, WorkflowRequest.class);
        boolean result = (boolean) method.invoke(featureDecisionService, request);

        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @MethodSource("userHasFeatureToggleWithRequiredAction")
    void givenUserHasFeatureToggleWithTheCorrectAction_whenFeatureToggleMethodIsInvoked_thenReturnTrue(
            String methodName,
            FeatureToggle featureToggle,
            FeatureToggleAction featureToggleAction,
            String isEnabled,
            boolean expectedResult)
            throws Exception {
        UserSummaryDTO userSummaryDTO = UserSummaryDTO.builder()
                .featureToggle(List.of(FeatureToggleDTO.builder()
                        .featureName(featureToggle.getName())
                        .action(featureToggleAction.getName())
                        .isEnabled(isEnabled)
                        .build()))
                .build();

        when(maatCourtDataService.getUserSummary(Constants.USERNAME)).thenReturn(userSummaryDTO);

        UserDTO userDTO = UserDTO.builder().userName(Constants.USERNAME).build();

        WorkflowRequest request = WorkflowRequest.builder().userDTO(userDTO).build();

        FeatureDecisionService featureDecisionService = new FeatureDecisionService(maatCourtDataService);
        Method method = FeatureDecisionService.class.getMethod(methodName, WorkflowRequest.class);
        boolean actual = (boolean) method.invoke(featureDecisionService, request);

        assertThat(actual).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> userHasFeatureToggleButWithoutRequiredAction() {
        return Stream.of(
                Arguments.of(IS_C3_ENABLED_METHOD_NAME, FeatureToggle.CALCULATE_CONTRIBUTION, FeatureToggleAction.READ),
                Arguments.of(
                        IS_MAAT_POST_ASSESSMENT_PROCESSING_ENABLED_METHOD_NAME,
                        FeatureToggle.MAAT_POST_ASSESSMENT_PROCESSING,
                        FeatureToggleAction.CREATE));
    }

    private static Stream<Arguments> userHasFeatureToggleWithRequiredAction() {
        return Stream.of(
                Arguments.of(
                        IS_C3_ENABLED_METHOD_NAME,
                        FeatureToggle.CALCULATE_CONTRIBUTION,
                        FeatureToggleAction.CREATE,
                        "Y",
                        true),
                Arguments.of(
                        IS_C3_ENABLED_METHOD_NAME,
                        FeatureToggle.CALCULATE_CONTRIBUTION,
                        FeatureToggleAction.CREATE,
                        "N",
                        false),
                Arguments.of(
                        IS_MAAT_POST_ASSESSMENT_PROCESSING_ENABLED_METHOD_NAME,
                        FeatureToggle.MAAT_POST_ASSESSMENT_PROCESSING,
                        FeatureToggleAction.READ,
                        "Y",
                        true),
                Arguments.of(
                        IS_MAAT_POST_ASSESSMENT_PROCESSING_ENABLED_METHOD_NAME,
                        FeatureToggle.MAAT_POST_ASSESSMENT_PROCESSING,
                        FeatureToggleAction.READ,
                        "N",
                        false));
    }
}
