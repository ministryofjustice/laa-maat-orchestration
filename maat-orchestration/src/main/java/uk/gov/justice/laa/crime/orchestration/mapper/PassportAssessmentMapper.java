package uk.gov.justice.laa.crime.orchestration.mapper;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.DeclaredBenefit;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.JobSeekerDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.NewWorkReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PartnerDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportConfirmationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ReviewTypeDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassportAssessmentMapper {

    private static final String ASSESSMENT_STATUS_DESCRIPTION = "Complete";

    public PassportedDTO apiGetPassportedAssessmentResponseToPassportedDTO(
            ApiGetPassportedAssessmentResponse response, ApplicantDTO applicantDTO) {

        AssessmentStatusDTO assessmentStatusDTO = AssessmentStatusDTO.builder()
                .status(AssessmentStatusDTO.COMPLETE)
                .description(ASSESSMENT_STATUS_DESCRIPTION)
                .build();

        PassportConfirmationDTO passportConfirmationDTO = PassportConfirmationDTO.builder()
                .confirmation(response.getDecisionReason().getConfirmation())
                .description(response.getDecisionReason().getDescription())
                .build();

        NewWorkReasonDTO newWorkReasonDTO = NewWorkReasonDTO.builder()
                .code(response.getAssessmentReason().getCode())
                .description(response.getAssessmentReason().getDescription())
                .type(response.getAssessmentReason().getType())
                .build();

        // TODO: Need to populate passportSummaryEvidenceDTO following completion of investigation LCAM-2002
        // TODO: Might need to amend mapping of legacy age related values based on feedback from the business
        // Not setting dwpResult and dwpWhoChecked as these are no longer used in MAAT and so can left as null.
        PassportedDTO dto = PassportedDTO.builder()
                .passportedId(Long.valueOf(response.getLegacyAssessmentId()))
                .cmuId(Long.valueOf(response.getCaseManagementUnitId()))
                .usn(Long.valueOf(response.getUsn()))
                .date(DateUtil.toDate(response.getAssessmentDate()))
                .assessementStatusDTO(assessmentStatusDTO)
                .passportConfirmationDTO(passportConfirmationDTO)
                .newWorkReason(newWorkReasonDTO)
                .notes(response.getNotes())
                .result(response.getAssessmentDecision().getCode())
                .under18HeardYouthCourt(response.getDeclaredUnder18())
                .build();

        if (response.getReviewType() != null) {
            ReviewTypeDTO reviewTypeDTO = ReviewTypeDTO.builder()
                    .code(response.getReviewType().getCode())
                    .description(response.getReviewType().getDescription())
                    .build();
            dto.setReviewType(reviewTypeDTO);
        }

        DeclaredBenefit declaredBenefit = response.getDeclaredBenefit();
        if (declaredBenefit != null) {
            if (applicantDTO != null) {
                dto.setBenefitClaimedByPartner(true);

                PartnerDTO partnerDTO = PartnerDTO.builder()
                        .firstName(applicantDTO.getFirstName())
                        .surname(applicantDTO.getLastName())
                        .nationaInsuranceNumber(applicantDTO.getNiNumber())
                        .dateOfBirth(DateUtil.asDate(applicantDTO.getDob()))
                        .build();
                dto.setPartnerDetails(partnerDTO);
            }

            switch (declaredBenefit.getBenefitType()) {
                case BenefitType.INCOME_SUPPORT -> dto.setBenefitIncomeSupport(true);
                case BenefitType.JSA -> {
                    JobSeekerDTO jobSeekerDTO = JobSeekerDTO.builder()
                            .isJobSeeker(BenefitType.JSA.equals(declaredBenefit.getBenefitType()))
                            .lastSignedOn(DateUtil.toDate(declaredBenefit.getLastSignOnDate()))
                            .build();
                    dto.setBenefitJobSeeker(jobSeekerDTO);
                }
                case BenefitType.GSPC -> dto.setBenefitGaurenteedStatePension(true);
                case BenefitType.ESA -> dto.setBenefitEmploymentSupport(true);
                case BenefitType.UC -> dto.setBenefitUniversalCredit(true);
                default -> throw new ValidationException("Valid benefit type not returned in response.");
            }
        }

        return dto;
    }
}
