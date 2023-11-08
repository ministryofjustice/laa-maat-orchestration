package uk.gov.justice.laa.maat.orchestration.service;

import uk.gov.justice.laa.maat.orchestration.dto.ApplicationDTO;

public interface AssessmentOrchestrator<T> {

    T find (int assessmentId);

    ApplicationDTO create(ApplicationDTO application);

    ApplicationDTO update(ApplicationDTO application);
}
