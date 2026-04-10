package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.DeclaredBenefit;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.PassportAssessmentDecisionReason;
import uk.gov.justice.laa.crime.enums.ReviewType;
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

    private PartnerDTO applicantDTOToPartnerDTO(ApplicantDTO applicant) {
        return PartnerDTO.builder()
                .firstName(applicant.getFirstName())
                .surname(applicant.getLastName())
                .nationaInsuranceNumber(applicant.getNiNumber())
                .dateOfBirth(DateUtil.asDate(applicant.getDob()))
                .build();
    }

    private PassportConfirmationDTO passportAssessmentDecisionReasonToPassportConfirmationDTO(
            PassportAssessmentDecisionReason decisionReason) {
        return PassportConfirmationDTO.builder()
                .confirmation(decisionReason.getConfirmation())
                .description(decisionReason.getDescription())
                .build();
    }

    private NewWorkReasonDTO newWorkReasonToNewWorkReasonDTO(NewWorkReason reason) {
        return NewWorkReasonDTO.builder()
                .code(reason.getCode())
                .description(reason.getDescription())
                .type(reason.getType())
                .build();
    }

    private ReviewTypeDTO reviewTypeToReviewTypeDTO(ReviewType type) {
        return ReviewTypeDTO.builder()
                .code(type.getCode())
                .description(type.getDescription())
                .build();
    }

    private JobSeekerDTO declaredBenefitToJobSeekerDTO(DeclaredBenefit benefit) {
        return JobSeekerDTO.builder()
                .isJobSeeker(BenefitType.JSA.equals(benefit.getBenefitType()))
                .lastSignedOn(DateUtil.toDate(benefit.getLastSignOnDate()))
                .build();
    }

    public PassportedDTO apiGetPassportedAssessmentResponseToPassportedDTO(
            ApiGetPassportedAssessmentResponse assessment, ApiGetPassportEvidenceResponse evidence, ApplicantDTO applicant) {

        AssessmentStatusDTO assessmentStatusDTO = AssessmentStatusDTO.builder()
                .status(AssessmentStatusDTO.COMPLETE)
                .description(ASSESSMENT_STATUS_DESCRIPTION)
                .build();

        // TODO: Need to populate passportSummaryEvidenceDTO following completion of LCAM-2002
        // TODO: Might need to amend mapping of legacy age related values following completion of LCAM-2016
        // Not setting dwpResult and dwpWhoChecked as these are no longer used in MAAT and so can left as null
        PassportedDTO dto = PassportedDTO.builder()
                .passportedId(Long.valueOf(assessment.getLegacyAssessmentId()))
                .cmuId(Long.valueOf(assessment.getCaseManagementUnitId()))
                .date(DateUtil.toDate(assessment.getAssessmentDate()))
                .assessementStatusDTO(assessmentStatusDTO)
                .passportConfirmationDTO(
                        passportAssessmentDecisionReasonToPassportConfirmationDTO(assessment.getDecisionReason()))
                .newWorkReason(newWorkReasonToNewWorkReasonDTO(assessment.getAssessmentReason()))
                .notes(assessment.getNotes())
                .result(assessment.getAssessmentDecision().getCode())
                .under18HeardYouthCourt(assessment.getDeclaredUnder18())
                .build();

        if (assessment.getUsn() != null) {
            dto.setUsn(Long.valueOf(assessment.getUsn()));
        }

        if (assessment.getReviewType() != null) {
            dto.setReviewType(reviewTypeToReviewTypeDTO(assessment.getReviewType()));
        }

        DeclaredBenefit declaredBenefit = assessment.getDeclaredBenefit();
        if (declaredBenefit != null) {
            if (applicant != null) {
                dto.setBenefitClaimedByPartner(true);
                dto.setPartnerDetails(applicantDTOToPartnerDTO(applicant));
            }

            switch (declaredBenefit.getBenefitType()) {
                case BenefitType.INCOME_SUPPORT -> dto.setBenefitIncomeSupport(true);
                case BenefitType.JSA -> dto.setBenefitJobSeeker(declaredBenefitToJobSeekerDTO(declaredBenefit));
                case BenefitType.GSPC -> dto.setBenefitGaurenteedStatePension(true);
                case BenefitType.ESA -> dto.setBenefitEmploymentSupport(true);
                case BenefitType.UC -> dto.setBenefitUniversalCredit(true);
            }
        }

        return dto;
    }
}
