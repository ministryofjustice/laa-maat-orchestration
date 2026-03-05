package uk.gov.justice.laa.crime.orchestration.exception;

import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

public class RollbackException extends MaatOrchestrationException {
    public RollbackException(ApplicationDTO applicationDTO) {
        super(applicationDTO);
    }
}
