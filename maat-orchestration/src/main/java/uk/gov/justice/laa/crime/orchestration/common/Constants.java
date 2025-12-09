package uk.gov.justice.laa.crime.orchestration.common;

import uk.gov.justice.laa.crime.enums.CaseType;

import java.util.Set;

public final class Constants {
    private Constants() {}

    public static final String WRN_MSG_REASSESSMENT = "A reassessment is required";
    public static final String WRN_MSG_INCOMPLETE_ASSESSMENT =
            "Incomplete assessment has been cleared due to this change.";

    public static final Set<CaseType> MAGS_COURT_CASE_TYPES =
            Set.of(CaseType.SUMMARY_ONLY, CaseType.EITHER_WAY, CaseType.INDICTABLE);

    // Error messages
    public static final String MISSING_REGISTRATION_ID = "registrationId cannot be null";
}
