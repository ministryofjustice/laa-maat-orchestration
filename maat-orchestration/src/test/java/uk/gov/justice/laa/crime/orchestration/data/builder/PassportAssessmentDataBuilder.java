package uk.gov.justice.laa.crime.orchestration.data.builder;

import static uk.gov.justice.laa.crime.orchestration.data.Constants.*;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.DeclaredBenefit;
import uk.gov.justice.laa.crime.enums.BenefitRecipient;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.PassportAssessmentDecision;
import uk.gov.justice.laa.crime.enums.PassportAssessmentDecisionReason;
import uk.gov.justice.laa.crime.enums.ReviewType;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;

@Component
public class PassportAssessmentDataBuilder {

    private static DeclaredBenefit getDeclaredBenefit() {
        return new DeclaredBenefit()
            .withBenefitType(BenefitType.JSA)
            .withLastSignOnDate(LAST_SIGNON_DATETIME)
            .withBenefitRecipient(BenefitRecipient.APPLICANT)
            .withLegacyPartnerId(null);
    }

    public static ApiGetPassportedAssessmentResponse getApiGetPassportedAssessmentResponse() {
        return new ApiGetPassportedAssessmentResponse()
            .withAssessmentId("deb7a9a4-2ad3-4ac8-95a4-ef0746c52ed0")
            .withLegacyAssessmentId(PASSPORT_ASSESSMENT_ID)
            .withUsn(USN)
            .withAssessmentDate(ASSESSMENT_DATETIME)
            .withAssessmentReason(NewWorkReason.FMA)
            .withReviewType(ReviewType.ER)
            .withDeclaredUnder18(false)
            .withDeclaredBenefit(getDeclaredBenefit())
            .withAssessmentDecision(PassportAssessmentDecision.PASS)
            .withDecisionReason(PassportAssessmentDecisionReason.DWP_CHECK)
            .withNotes(NOTES);
    }

    // TODO: Populate this with actual test values once questions been resolved
    public static PassportedDTO getPassportedDTO() {
        return PassportedDTO.builder().build();
    }
}
