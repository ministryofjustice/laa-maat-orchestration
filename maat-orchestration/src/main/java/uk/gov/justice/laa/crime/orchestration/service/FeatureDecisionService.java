package uk.gov.justice.laa.crime.orchestration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CurrentFeatureToggles;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggleAction;

@Slf4j
@Service
public class FeatureDecisionService {

    private final MaatCourtDataService maatCourtDataService;

    public FeatureDecisionService(final MaatCourtDataService maatCourtDataService) {
        this.maatCourtDataService = maatCourtDataService;
    }

    /**
     * Returns whether the CCC Service is enabled based on whether the user has the requisite
     * feature flag (CurrentFeatureToggles.CALCULATE_CONTRIBUTION) and action (Create) in their
     * user summary information.
     * @param workflowRequest The workflow request to check against.
     * @return A boolean describing whether C3 should be invoked for the user's request.
     */
    public boolean isC3Enabled(WorkflowRequest workflowRequest) {
        UserSummaryDTO userSummaryDTO = maatCourtDataService.getUserSummary(
            workflowRequest.getUserDTO().getUserName());

        return userSummaryDTO.getFeatureToggle() != null && userSummaryDTO.getFeatureToggle().stream().anyMatch(
            t -> CurrentFeatureToggles.CALCULATE_CONTRIBUTION.getName().equals(t.getFeatureName())
                && FeatureToggleAction.CREATE.getName().equals(t.getAction()));
    }
}