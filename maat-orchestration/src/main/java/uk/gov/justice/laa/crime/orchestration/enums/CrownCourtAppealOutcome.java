package uk.gov.justice.laa.crime.orchestration.enums;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CrownCourtAppealOutcome {

    SUCCESSFUL("SUCCESSFUL"),
    UNSUCCESSFUL("UNSUCCESSFUL"),
    PART_SUCCESS("PART SUCCESS");

    @NotNull
    private final String value;

    public static CrownCourtAppealOutcome getFrom(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Stream.of(CrownCourtAppealOutcome.values())
                .filter(appealOutcome -> appealOutcome.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Appeal Outcome with value: %s does not exist.", value)));
    }
}
