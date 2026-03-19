package uk.gov.justice.laa.crime.orchestration.data.builder;

import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.DeclaredBenefit;
import uk.gov.justice.laa.crime.enums.BenefitRecipient;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.PassportAssessmentDecision;
import uk.gov.justice.laa.crime.enums.PassportAssessmentDecisionReason;
import uk.gov.justice.laa.crime.enums.ReviewType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.maat.JobSeekerDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.NewWorkReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PartnerDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportConfirmationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import org.springframework.stereotype.Component;

@Component
public class PassportAssessmentDataBuilder {

    private static DeclaredBenefit getDeclaredBenefit() {
        return new DeclaredBenefit()
                .withBenefitType(BenefitType.INCOME_SUPPORT)
                .withLastSignOnDate(Constants.LAST_SIGNON_DATETIME)
                .withBenefitRecipient(BenefitRecipient.APPLICANT)
                .withLegacyPartnerId(null);
    }

    private static DeclaredBenefit getDeclaredPartnerBenefit() {
        return new DeclaredBenefit()
                .withBenefitType(BenefitType.INCOME_SUPPORT)
                .withLastSignOnDate(Constants.LAST_SIGNON_DATETIME)
                .withBenefitRecipient(BenefitRecipient.PARTNER)
                .withLegacyPartnerId(Constants.PARTNER_ID);
    }

    private static PassportConfirmationDTO getPassportConfirmationDTO(PassportAssessmentDecisionReason reason) {
        return PassportConfirmationDTO.builder()
                .confirmation(reason.getConfirmation())
                .description(reason.getDescription())
                .build();
    }

    private static NewWorkReasonDTO getNewWorkReasonDTO(NewWorkReason reason) {
        return NewWorkReasonDTO.builder()
                .code(reason.getCode())
                .description(reason.getDescription())
                .type(reason.getType())
                .build();
    }

    private static PartnerDTO getPartnerDTO() {
        return PartnerDTO.builder()
                .firstName(Constants.FIRST_NAME)
                .surname(Constants.LAST_NAME)
                .nationaInsuranceNumber(Constants.NI_NUMBER)
                .dateOfBirth(DateUtil.asDate(Constants.DATE_OF_BIRTH))
                .build();
    }

    public static ApiGetPassportedAssessmentResponse getApiGetPassportedAssessmentResponse(boolean hasPartner) {
        return new ApiGetPassportedAssessmentResponse()
                .withAssessmentId("deb7a9a4-2ad3-4ac8-95a4-ef0746c52ed0")
                .withLegacyAssessmentId(Constants.PASSPORT_ASSESSMENT_ID)
                .withUsn(Constants.USN)
                .withCaseManagementUnitId(Constants.CASE_MANAGEMENT_UNIT_ID)
                .withAssessmentDate(Constants.ASSESSMENT_DATETIME)
                .withAssessmentReason(NewWorkReason.FMA)
                .withReviewType(ReviewType.ER)
                .withDeclaredUnder18(false)
                .withDeclaredBenefit(hasPartner ? getDeclaredPartnerBenefit() : getDeclaredBenefit())
                .withAssessmentDecision(PassportAssessmentDecision.PASS)
                .withDecisionReason(PassportAssessmentDecisionReason.DWP_CHECK)
                .withNotes(Constants.NOTES);
    }

    public static ApplicantDTO getApplicantDTO() {
        return ApplicantDTO.builder()
                .firstName(Constants.FIRST_NAME)
                .lastName(Constants.LAST_NAME)
                .niNumber(Constants.NI_NUMBER)
                .dob(Constants.DATE_OF_BIRTH)
                .build();
    }

    public static JobSeekerDTO getJobSeekerDTO() {
        return JobSeekerDTO.builder()
                .isJobSeeker(true)
                .lastSignedOn(DateUtil.toDate(Constants.LAST_SIGNON_DATETIME))
                .build();
    }

    public static PassportedDTO getPassportedDTO(boolean hasPartner) {
        return PassportedDTO.builder()
                .passportedId(Long.valueOf(Constants.PASSPORT_ASSESSMENT_ID))
                .cmuId(Long.valueOf(Constants.CASE_MANAGEMENT_UNIT_ID))
                .usn(Long.valueOf(Constants.USN))
                .date(DateUtil.toDate(Constants.ASSESSMENT_DATETIME))
                .assessementStatusDTO(MeansAssessmentDataBuilder.getAssessmentStatusDTO(CurrentStatus.COMPLETE))
                .passportConfirmationDTO(getPassportConfirmationDTO(PassportAssessmentDecisionReason.DWP_CHECK))
                .newWorkReason(getNewWorkReasonDTO(NewWorkReason.FMA))
                .notes(Constants.NOTES)
                .result(PassportAssessmentDecision.PASS.getCode())
                .reviewType(MeansAssessmentDataBuilder.getReviewTypeDTO())
                .benefitIncomeSupport(true)
                .benefitClaimedByPartner(hasPartner)
                .partnerDetails(hasPartner ? getPartnerDTO() : new PartnerDTO())
                .under18HeardYouthCourt(false)
                .build();
    }
}
