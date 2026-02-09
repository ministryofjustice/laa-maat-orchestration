package uk.gov.justice.laa.crime.orchestration.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrimeValidationException extends RuntimeException {

    private final List<String> exceptionMessages;

    public CrimeValidationException(List<String> exceptionMessages) {
        this.exceptionMessages = exceptionMessages;
    }
}
