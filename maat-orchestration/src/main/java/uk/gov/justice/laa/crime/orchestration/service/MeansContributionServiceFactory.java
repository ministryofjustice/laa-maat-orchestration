package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansContributionServiceFactory {

    private final FeatureDecisionService featureDecisionService;
    private final LegacyMeansContributionService legacyMeansContributionService;
    private final ModernMeansContributionService modernMeansContributionService;

    public MeansContributionService getService(WorkflowRequest request) {
        if (featureDecisionService.isC3Enabled(request)) {
            return modernMeansContributionService;
        } else {
            return legacyMeansContributionService;
        }
    }
}
