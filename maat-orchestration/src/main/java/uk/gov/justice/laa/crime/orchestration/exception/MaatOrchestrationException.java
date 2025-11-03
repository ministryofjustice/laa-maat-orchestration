package uk.gov.justice.laa.crime.orchestration.exception;

import lombok.Getter;
import lombok.Setter;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

@Getter
@Setter
public class MaatOrchestrationException extends RuntimeException {

    private final ApplicationDTO applicationDTO;

    public MaatOrchestrationException(ApplicationDTO applicationDTO) {
        this.applicationDTO = applicationDTO;
    }
}
