package uk.gov.justice.laa.crime.orchestration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssessmentSummaryType {
    INITIAL_MEANS_ASSESSMENT("Initial Assessment"),
    FULL_MEANS_ASSESSMENT("Full Means Test"),
    HARDSHIP_REVIEW_MAGS_COURT("Hardship Review - Magistrate"),
    HARDSHIP_REVIEW_CROWN_COURT("Hardship Review - Crown Court"),
    IOJ_APPEAL("IoJ Appeal");

    private final String name;
}
