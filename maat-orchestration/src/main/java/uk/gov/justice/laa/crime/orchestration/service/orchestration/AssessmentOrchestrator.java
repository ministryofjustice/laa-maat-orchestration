package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

public interface AssessmentOrchestrator<T> {

    T find(int assessmentId);

    ApplicationDTO create(WorkflowRequest workflowRequest);

    ApplicationDTO update(WorkflowRequest workflowRequest);
}
