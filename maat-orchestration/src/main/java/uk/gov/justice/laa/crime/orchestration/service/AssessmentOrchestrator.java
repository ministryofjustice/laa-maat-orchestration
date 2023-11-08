package uk.gov.justice.laa.crime.orchestration.service;

import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

public interface AssessmentOrchestrator<T> {

    T find (int assessmentId);

    ApplicationDTO create(ApplicationDTO application);

    ApplicationDTO update(ApplicationDTO application);
}
