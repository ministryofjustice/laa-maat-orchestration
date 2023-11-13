package uk.gov.justice.laa.crime.orchestration.service;

import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.WorkflowRequestDTO;

import java.util.Collection;

public interface AssessmentOrchestrator<T> {

    T find(int assessmentId);

    ApplicationDTO create(WorkflowRequestDTO workflowRequest);

    ApplicationDTO update(WorkflowRequestDTO workflowRequest);

    default void updateAssessmentSummary(ApplicationDTO application, AssessmentSummaryDTO summaryDTO) {
        Collection<AssessmentSummaryDTO> assessmentSummary = application.getAssessmentSummary();
        assessmentSummary.removeIf(s -> s.getId().equals(summaryDTO.getId()));
        assessmentSummary.add(summaryDTO);
    }
}
