package uk.gov.justice.laa.crime.orchestration.service;

import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggle;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggleAction;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeatureDecisionService {

    private final MaatCourtDataService maatCourtDataService;

    public FeatureDecisionService(final MaatCourtDataService maatCourtDataService) {
        this.maatCourtDataService = maatCourtDataService;
    }

    /**
     * Returns whether the MAAT post-assessment processing flow is enabled based on whether the user
     * has the requisite feature flag (CurrentFeatureToggles.MAAT_POST_ASSESSMENT_PROCESSING) and action
     * (Read) in their user summary information.
     *
     * @param workflowRequest The workflow request to check against.
     * @return A boolean describing whether the MAAT post-assessment processing flow should be
     * invoked for the user's request.
     */
    public boolean isMaatPostAssessmentProcessingEnabled(WorkflowRequest workflowRequest) {
        return isFeatureEnabled(
                workflowRequest, FeatureToggle.MAAT_POST_ASSESSMENT_PROCESSING, FeatureToggleAction.READ);
    }

    private boolean isFeatureEnabled(
            WorkflowRequest workflowRequest, FeatureToggle featureToggle, FeatureToggleAction featureToggleAction) {
        UserSummaryDTO userSummaryDTO =
                maatCourtDataService.getUserSummary(workflowRequest.getUserDTO().getUserName());

        return userSummaryDTO.getFeatureToggle() != null
                && userSummaryDTO.getFeatureToggle().stream()
                        .anyMatch(featureToggleDTO -> featureToggle.getName().equals(featureToggleDTO.getFeatureName())
                                && featureToggleAction.getName().equals(featureToggleDTO.getAction())
                                && "Y".equals(featureToggleDTO.getIsEnabled()));
    }
}
