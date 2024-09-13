package uk.gov.justice.laa.crime.orchestration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeatureToggleAction {
    CREATE("Create"),
    READ("Read"),
    UPDATE("Update"),
    DELETE("Delete");

    private final String name;
}
