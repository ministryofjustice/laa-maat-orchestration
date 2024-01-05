package uk.gov.justice.laa.crime.orchestration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CurrentStatus {
    IN_PROGRESS("IN PROGRESS", "Incomplete"),
    COMPLETE("COMPLETE", "Complete");

    private static final String EXCEPTION_MESSAGE = "Assessment status with value: %s does not exist";

    private final String value;
    private String description;

    public static CurrentStatus getFrom(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Stream.of(CurrentStatus.values())
                .filter(currentStatus -> currentStatus.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(EXCEPTION_MESSAGE, value)));
    }
}
