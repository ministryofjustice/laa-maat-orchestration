package uk.gov.justice.laa.crime.orchestration.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CaseDetailDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.OutcomeDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.enums.MagCourtOutcome;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith({MockitoExtension.class})
class CrownCourtHelperTest {

    CrownCourtHelper crownCourtHelper = new CrownCourtHelper();

    private ApplicationDTO getApplicationDTOWithCaseTypeAndMagsOutcome(CaseType caseType,
                                                                       MagCourtOutcome magCourtOutcome) {
        ApplicationDTO.ApplicationDTOBuilder<?, ?> builder = ApplicationDTO.builder()
                .caseDetailsDTO(
                        CaseDetailDTO.builder()
                                .caseType(caseType.getCaseType())
                                .build());

        if (magCourtOutcome != null) {
            builder.magsOutcomeDTO(
                    OutcomeDTO.builder()
                            .outcome(magCourtOutcome.getOutcome())
                            .build());
        } else {
            builder.magsOutcomeDTO(OutcomeDTO.builder().build());
        }
        return builder.build();
    }

    @Test
    void givenIndictableCaseTypeAndNoMagsOutcome_whenGetCourtTypeIsInvoked_thenCrownCourtTypeIsReturned() {
        ApplicationDTO applicationDTO =
                getApplicationDTOWithCaseTypeAndMagsOutcome(CaseType.INDICTABLE, null);
        CourtType courtType = crownCourtHelper.getCourtType(applicationDTO);
        assertThat(courtType).isEqualTo(CourtType.CROWN_COURT);
    }

    @Test
    void givenEitherWayCaseTypeAndNoMagsOutcome_whenGetCourtTypeIsInvoked_thenMagsCourtTypeIsReturned() {
        ApplicationDTO applicationDTO =
                getApplicationDTOWithCaseTypeAndMagsOutcome(CaseType.EITHER_WAY, null);
        CourtType courtType = crownCourtHelper.getCourtType(applicationDTO);
        assertThat(courtType).isEqualTo(CourtType.MAGISTRATE);
    }

    @Test
    void givenCCAlreadyCaseType_whenGetCourtTypeIsInvoked_thenCrownCourtTypeIsReturned() {
        ApplicationDTO applicationDTO =
                getApplicationDTOWithCaseTypeAndMagsOutcome(CaseType.CC_ALREADY, MagCourtOutcome.COMMITTED);
        CourtType courtType = crownCourtHelper.getCourtType(applicationDTO);
        assertThat(courtType).isEqualTo(CourtType.CROWN_COURT);
    }

    @Test
    void givenEitherWayCaseTypeAndOutcomeIsCommittedForTrial_whenGetCourtTypeIsInvoked_thenCrownCourtTypeIsReturned() {
        ApplicationDTO applicationDTO =
                getApplicationDTOWithCaseTypeAndMagsOutcome(CaseType.EITHER_WAY, MagCourtOutcome.COMMITTED_FOR_TRIAL);
        CourtType courtType = crownCourtHelper.getCourtType(applicationDTO);
        assertThat(courtType).isEqualTo(CourtType.CROWN_COURT);
    }

    @Test
    void givenEitherWayCaseTypeAndOutcomeIsResolvedInMags_whenGetCourtTypeIsInvoked_thenMagsCourtTypeIsReturned() {
        ApplicationDTO applicationDTO =
                getApplicationDTOWithCaseTypeAndMagsOutcome(CaseType.EITHER_WAY, MagCourtOutcome.RESOLVED_IN_MAGS);
        CourtType courtType = crownCourtHelper.getCourtType(applicationDTO);
        assertThat(courtType).isEqualTo(CourtType.MAGISTRATE);
    }

    @Test
    void givenSummaryOnlyCaseTypeAndOutcomeIsResolvedInMags_whenGetCourtTypeIsInvoked_thenMagsCourtTypeIsReturned() {
        ApplicationDTO applicationDTO =
                getApplicationDTOWithCaseTypeAndMagsOutcome(CaseType.SUMMARY_ONLY, MagCourtOutcome.RESOLVED_IN_MAGS);
        CourtType courtType = crownCourtHelper.getCourtType(applicationDTO);
        assertThat(courtType).isEqualTo(CourtType.MAGISTRATE);
    }
}
