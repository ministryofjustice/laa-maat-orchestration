package uk.gov.justice.laa.crime.orchestration.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.orchestration.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.enums.MagCourtOutcome;

import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CrownCourtHelper {

    public static final List<CaseType> CC_CASE_TYPES =
            List.of(CaseType.INDICTABLE, CaseType.CC_ALREADY, CaseType.APPEAL_CC, CaseType.COMMITAL);

    public static boolean isCrownCourt(String caseType, String magsOutcome) {
        if (CC_CASE_TYPES.contains(CaseType.getFrom(caseType))) {
            return true;
        }
        return CaseType.getFrom(caseType) == CaseType.EITHER_WAY
                && MagCourtOutcome.RESOLVED_IN_MAGS != MagCourtOutcome.getFrom(magsOutcome);
    }
}
