package uk.gov.justice.laa.crime.orchestration.service;

import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;

import java.util.Collection;

public interface AssessmentOrchestrator<T> {

    T find(int assessmentId);

    ApplicationDTO create(ApplicationDTO application);

    ApplicationDTO update(ApplicationDTO application);

    default void updateAssessmentSummary(ApplicationDTO application, AssessmentSummaryDTO summaryDTO) {
        Collection<AssessmentSummaryDTO> assessmentSummary = application.getAssessmentSummary();
        assessmentSummary.removeIf(s -> s.getId().equals(summaryDTO.getId()));
        assessmentSummary.add(summaryDTO);
    }
}
