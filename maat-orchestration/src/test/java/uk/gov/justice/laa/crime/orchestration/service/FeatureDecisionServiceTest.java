package uk.gov.justice.laa.crime.orchestration.service;

import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FeatureToggleDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CurrentFeatureToggles;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggleAction;

@ExtendWith({MockitoExtension.class})
class FeatureDecisionServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenUserDoesNotHaveFeatureToggle_whenIsC3EnabledIsInvoked_thenReturnFalse() {
        when(maatCourtDataService.getUserSummary(Constants.USERNAME))
            .thenReturn(new UserSummaryDTO());

        UserDTO userDTO = UserDTO.builder()
            .userName(Constants.USERNAME)
            .build();

        WorkflowRequest request = WorkflowRequest.builder()
            .userDTO(userDTO)
            .build();

        FeatureDecisionService featureDecisionService = new FeatureDecisionService(maatCourtDataService);
        boolean result = featureDecisionService.isC3Enabled(request);

        Assertions.assertFalse(result);
    }

    @Test
    void givenUserHasFeatureToggleButWithoutTheCorrectAction_whenIsC3EnabledIsInvoked_thenReturnFalse() {
        UserSummaryDTO userSummaryDTO = UserSummaryDTO.builder()
            .featureToggle(List.of(
                FeatureToggleDTO.builder()
                    .featureName(CurrentFeatureToggles.CALCULATE_CONTRIBUTION.getName())
                    .action(FeatureToggleAction.READ.getName())
                    .build()))
            .build();

        when(maatCourtDataService.getUserSummary(Constants.USERNAME))
            .thenReturn(userSummaryDTO);

        UserDTO userDTO = UserDTO.builder()
            .userName(Constants.USERNAME)
            .build();

        WorkflowRequest request = WorkflowRequest.builder()
            .userDTO(userDTO)
            .build();

        FeatureDecisionService featureDecisionService = new FeatureDecisionService(maatCourtDataService);
        boolean result = featureDecisionService.isC3Enabled(request);

        Assertions.assertFalse(result);
    }

    @Test
    void givenUserHasFeatureToggleWithTheCorrectAction_whenIsC3EnabledIsInvoked_thenReturnTrue() {
        UserSummaryDTO userSummaryDTO = UserSummaryDTO.builder()
            .featureToggle(List.of(
                FeatureToggleDTO.builder()
                    .featureName(CurrentFeatureToggles.CALCULATE_CONTRIBUTION.getName())
                    .action(FeatureToggleAction.CREATE.getName())
                    .build()))
            .build();

        when(maatCourtDataService.getUserSummary(Constants.USERNAME))
            .thenReturn(userSummaryDTO);

        UserDTO userDTO = UserDTO.builder()
            .userName(Constants.USERNAME)
            .build();

        WorkflowRequest request = WorkflowRequest.builder()
            .userDTO(userDTO)
            .build();

        FeatureDecisionService featureDecisionService = new FeatureDecisionService(maatCourtDataService);
        boolean result = featureDecisionService.isC3Enabled(request);

        Assertions.assertTrue(result);
    }

    @Test
    void givenUserDoesNotHaveFeatureToggle_whenIsFeatureEnabledIsInvoked_thenReturnFalse() {
        when(maatCourtDataService.getUserSummary(Constants.USERNAME))
                .thenReturn(new UserSummaryDTO());

        UserDTO userDTO = UserDTO.builder()
                .userName(Constants.USERNAME)
                .build();

        WorkflowRequest request = WorkflowRequest.builder()
                .userDTO(userDTO)
                .build();

        FeatureDecisionService featureDecisionService = new FeatureDecisionService(maatCourtDataService);
        boolean result = featureDecisionService.isFeatureEnabled(request, CurrentFeatureToggles.MAAT_POST_ASSESSMENT_PROCESSING, FeatureToggleAction.UPDATE);

        Assertions.assertFalse(result);
    }

    @Test
    void givenUserHasFeatureToggle_whenIsFeatureEnabledIsInvoked_thenReturnTrue() {
        UserSummaryDTO userSummaryDTO = UserSummaryDTO.builder()
                .featureToggle(List.of(
                        FeatureToggleDTO.builder()
                                .featureName(CurrentFeatureToggles.MAAT_POST_ASSESSMENT_PROCESSING.getName())
                                .action(FeatureToggleAction.UPDATE.getName())
                                .build()))
                .build();

        when(maatCourtDataService.getUserSummary(Constants.USERNAME))
                .thenReturn(userSummaryDTO);

        UserDTO userDTO = UserDTO.builder()
                .userName(Constants.USERNAME)
                .build();

        WorkflowRequest request = WorkflowRequest.builder()
                .userDTO(userDTO)
                .build();

        FeatureDecisionService featureDecisionService = new FeatureDecisionService(maatCourtDataService);
        boolean result = featureDecisionService.isFeatureEnabled(request, CurrentFeatureToggles.MAAT_POST_ASSESSMENT_PROCESSING, FeatureToggleAction.UPDATE);

        Assertions.assertTrue(result);
    }
}
