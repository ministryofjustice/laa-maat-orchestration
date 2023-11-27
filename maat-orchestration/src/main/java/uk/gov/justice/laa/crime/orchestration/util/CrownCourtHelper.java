package uk.gov.justice.laa.crime.orchestration.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.enums.MagCourtOutcome;

import java.util.List;

@Slf4j
@Component
public class CrownCourtHelper {

    public static final List<CaseType> CC_CASE_TYPES =
            List.of(CaseType.INDICTABLE, CaseType.CC_ALREADY, CaseType.APPEAL_CC, CaseType.COMMITAL);

    public CourtType getCourtType(ApplicationDTO application) {
        String caseType = application.getCaseDetailsDTO().getCaseType();
        String magsOutcome = application.getMagsOutcomeDTO().getOutcome();
        return isCrownCourt(caseType, magsOutcome) ? CourtType.CROWN_COURT : CourtType.MAGISTRATE;
    }

    private boolean isCrownCourt(String caseType, String magsOutcome) {
        if (CC_CASE_TYPES.contains(CaseType.getFrom(caseType))) {
            return true;
        }
        return CaseType.getFrom(caseType) == CaseType.EITHER_WAY
                && MagCourtOutcome.RESOLVED_IN_MAGS != MagCourtOutcome.getFrom(magsOutcome);
    }
}
