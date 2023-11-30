package uk.gov.justice.laa.crime.orchestration.enums;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

/**
 * static data migrated from TOGDATA.CASE_TYPES table
 */
@Getter
@AllArgsConstructor
public enum CaseType {

    INDICTABLE("INDICTABLE", "Indictable"),
    SUMMARY_ONLY("SUMMARY ONLY", "Summary-only"),
    CC_ALREADY("CC ALREADY", "Trial already in Crown Court"),
    APPEAL_CC("APPEAL CC", "Appeal to Crown Court"),
    COMMITAL("COMMITAL", "Committal for Sentence"),
    EITHER_WAY("EITHER WAY", "Either-Way");

    @NotNull
    private final String caseType;
    private final String description;

    public String getCaseType() {
        return caseType;
    }

    public static CaseType getFrom(String caseType) {
        if (StringUtils.isBlank(caseType)) {
            return null;
        }

        return Stream.of(CaseType.values())
                .filter(f -> f.caseType.equals(caseType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("CaseType with value: %s does not exist.", caseType)));
    }
}