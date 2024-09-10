package uk.gov.justice.laa.crime.orchestration.service;

import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;

public interface MeansContributionService {
    void processContributions(WorkflowRequest request);
}
