package uk.gov.justice.laa.crime.orchestration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurrentFeatureToggles {
    CALCULATE_CONTRIBUTION("CalculateContribution");

    private final String name;
}
