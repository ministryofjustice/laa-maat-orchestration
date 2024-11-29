package uk.gov.justice.laa.crime.orchestration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeatureToggle {
    CALCULATE_CONTRIBUTION("CalculateContribution"),
    MAAT_POST_ASSESSMENT_PROCESSING("MaatPostAssessmentProcessing");

    private final String name;
}
