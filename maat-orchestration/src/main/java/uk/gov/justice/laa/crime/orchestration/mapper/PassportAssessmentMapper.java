package uk.gov.justice.laa.crime.orchestration.mapper;

import jakarta.validation.ValidationException;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.enums.BenefitRecipient;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.JobSeekerDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.NewWorkReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportConfirmationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ReviewTypeDTO;

@Component
@RequiredArgsConstructor
public class PassportAssessmentMapper {

    private static final String ASSESSMENT_STATUS_DESCRIPTION = "Complete";

    public PassportedDTO apiGetPassportedAssessmentResponseToPassportedDTO(
        ApiGetPassportedAssessmentResponse response) {

        // TODO: Could split this out into seperate class as duplicate to ioj???
        AssessmentStatusDTO assessmentStatusDTO = AssessmentStatusDTO.builder()
            .status(AssessmentStatusDTO.COMPLETE)
            .description(ASSESSMENT_STATUS_DESCRIPTION)
            .build();

        PassportConfirmationDTO passportConfirmationDTO = PassportConfirmationDTO.builder()
            .confirmation(response.getDecisionReason().getConfirmation())
            .description(response.getDecisionReason().getDescription())
            .build();

        // TODO: Could split this out into seperate class as duplicate to ioj???
        NewWorkReasonDTO newWorkReasonDTO = NewWorkReasonDTO.builder()
            .code(response.getAssessmentReason().getCode())
            .description(response.getAssessmentReason().getDescription())
            .type(response.getAssessmentReason().getType())
            .build();

        // TODO: Need to populate passportSummaryEvidenceDTO following completion of investigation LCAM-2002
        // Not setting dwpResult and dwpWhoChecked as these are no longer used in MAAT and so can left as null.
        PassportedDTO dto = PassportedDTO.builder()
            .passportedId(Long.valueOf(response.getLegacyAssessmentId()))
            .cmuId(Long.valueOf(response.getCaseManagementUnitId()))
            .usn(Long.valueOf(response.getUsn()))
            .date(Date.from(response.getAssessmentDate().atZone(ZoneId.systemDefault()).toInstant()))
            .assessementStatusDTO(assessmentStatusDTO)
            .passportConfirmationDTO(passportConfirmationDTO)
            .newWorkReason(newWorkReasonDTO)
            .partnerDetails() // TODO: Use partner id in declared benefit to get partner details from MAAT API???
            .notes(response.getNotes())
            .result(response.getAssessmentDecision().getCode())
            .under18HeardYouthCourt(response.getDeclaredUnder18())
            .under18HeardMagsCourt() // TODO: Need to double check it's ok to not set these as we are not getting any detail back from crime assessment
            .under18FullEducation() // TODO: Need to double check it's ok to not set these as we are not getting any detail back from crime assessment
            .under16() // TODO: Need to double check it's ok to not set these as we are not getting any detail back from crime assessment
            .between1617() // TODO: Need to double check it's ok to not set these as we are not getting any detail back from crime assessment
            .build();

        if (response.getReviewType() != null) {
            ReviewTypeDTO reviewTypeDTO = ReviewTypeDTO.builder()
                .code(response.getReviewType().getCode())
                .description(response.getReviewType().getDescription())
                .build();
            dto.setReviewType(reviewTypeDTO);
        }

        if (response.getDeclaredBenefit() != null) {
            dto.setBenefitClaimedByPartner(BenefitRecipient.PARTNER.equals(
                response.getDeclaredBenefit().getBenefitRecipient()));

            switch (response.getDeclaredBenefit().getBenefitType()) {
                case BenefitType.INCOME_SUPPORT -> dto.setBenefitIncomeSupport(true);
                case BenefitType.JSA -> {
                    JobSeekerDTO jobSeekerDTO = JobSeekerDTO.builder()
                        .isJobSeeker(
                            BenefitType.JSA.equals(response.getDeclaredBenefit().getBenefitType()))
                        .lastSignedOn(Date.from(response.getDeclaredBenefit().getLastSignOnDate()
                            .atZone(ZoneId.systemDefault()).toInstant()))
                        .build();
                    dto.setBenefitJobSeeker(jobSeekerDTO);
                }
                case BenefitType.GSPC -> dto.setBenefitGaurenteedStatePension(true);
                case BenefitType.ESA -> dto.setBenefitEmploymentSupport(true);
                case BenefitType.UC -> dto.setBenefitUniversalCredit(true);
                default ->
                    throw new ValidationException("Valid benefit type not returned in response.");
            }
        }

        return dto;
    }

}
