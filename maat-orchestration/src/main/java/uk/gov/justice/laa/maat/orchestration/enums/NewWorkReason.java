package uk.gov.justice.laa.maat.orchestration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum NewWorkReason {

    NEW("NEW", NewWorkReasonType.HARDIOJ, "New"),
    PRI("PRI", NewWorkReasonType.HARDIOJ, "Previous Record Incorrect"),
    JR("JR", NewWorkReasonType.HARDIOJ, "Judicial Review");

    private final String code;
    private final String type;
    private final String description;

    public static NewWorkReason getFrom(String code) {
        if (StringUtils.isBlank(code)) return null;

        return Stream.of(NewWorkReason.values())
                .filter(newWorkReason -> newWorkReason.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("New Work Reason with value: %s does not exist.", code)));
    }

    private static class NewWorkReasonType {
        private static final String HARDIOJ = "HARDIOJ";
    }
}
