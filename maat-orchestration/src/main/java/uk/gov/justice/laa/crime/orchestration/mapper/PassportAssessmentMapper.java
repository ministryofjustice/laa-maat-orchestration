package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiCreatePassportedAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.DeclaredBenefit;
import uk.gov.justice.laa.crime.common.model.passported.PassportedAssessment;
import uk.gov.justice.laa.crime.common.model.passported.PassportedAssessmentMetadata;
import uk.gov.justice.laa.crime.enums.BenefitRecipient;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.PassportAssessmentDecision;
import uk.gov.justice.laa.crime.enums.PassportAssessmentDecisionReason;
import uk.gov.justice.laa.crime.enums.ReviewType;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.JobSeekerDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.NewWorkReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PartnerDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportConfirmationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ReviewTypeDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassportAssessmentMapper {

    private static final String ASSESSMENT_STATUS_DESCRIPTION = "Complete";

    private final UserMapper userMapper;
    private final PassportEvidenceMapper passportEvidenceMapper;

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

    private BenefitType mapBenefitType(PassportedDTO passportedDTO) {
        if (Boolean.TRUE.equals(passportedDTO.getBenefitEmploymentSupport())) {
            return BenefitType.ESA;
        } else if (Boolean.TRUE.equals(passportedDTO.getBenefitIncomeSupport())) {
            return BenefitType.INCOME_SUPPORT;
        } else if (passportedDTO.getBenefitJobSeeker() != null
                && passportedDTO.getBenefitJobSeeker().getIsJobSeeker()) {
            return BenefitType.JSA;
        } else if (Boolean.TRUE.equals(passportedDTO.getBenefitGaurenteedStatePension())) {
            return BenefitType.GSPC;
        } else if (Boolean.TRUE.equals(passportedDTO.getBenefitUniversalCredit())) {
            return BenefitType.UC;
        }

        return null;
    }

    private DeclaredBenefit mapDeclaredBenefit(ApplicationDTO applicationDTO) {
        PassportedDTO passportedDTO = applicationDTO.getPassportedDTO();
        BenefitType benefitType = mapBenefitType(passportedDTO);
        BenefitRecipient benefitRecipient =
                Boolean.TRUE.equals(applicationDTO.getPassportedDTO().getBenefitClaimedByPartner())
                        ? BenefitRecipient.PARTNER
                        : BenefitRecipient.APPLICANT;

        return new DeclaredBenefit()
                .withBenefitType(benefitType)
                .withLastSignOnDate(
                        BenefitType.JSA.equals(benefitType)
                                ? DateUtil.toLocalDateTime(
                                        passportedDTO.getBenefitJobSeeker().getLastSignedOn())
                                : null)
                .withBenefitRecipient(benefitRecipient)
                .withLegacyPartnerId(
                        BenefitRecipient.PARTNER.equals(benefitRecipient)
                                ? applicationDTO.getApplicantLinks().stream()
                                        .filter(applicant -> applicant.getUnlinked() == null)
                                        .map(applicant -> applicant
                                                .getPartnerDTO()
                                                .getId()
                                                .intValue())
                                        .findFirst()
                                        .orElse(null)
                                : null);
    }

    private PassportedAssessment applicationDTOToPassportedAssessment(ApplicationDTO applicationDTO) {
        PassportedDTO passportedDTO = applicationDTO.getPassportedDTO();

        return new PassportedAssessment()
                .withAssessmentDate(DateUtil.toLocalDateTime(passportedDTO.getDate()))
                .withAssessmentReason(
                        NewWorkReason.getFrom(passportedDTO.getNewWorkReason().getCode()))
                .withReviewType(ReviewType.getFrom(passportedDTO.getReviewType().getCode()))
                .withDeclaredUnder18(
                        passportedDTO.getUnder18HeardMagsCourt() || passportedDTO.getUnder18HeardYouthCourt())
                .withDeclaredBenefit(mapDeclaredBenefit(applicationDTO))
                .withAssessmentDecision(PassportAssessmentDecision.getFrom(passportedDTO.getResult()))
                .withDecisionReason(PassportAssessmentDecisionReason.getFrom(
                        passportedDTO.getPassportConfirmationDTO().getConfirmation()))
                .withNotes(passportedDTO.getNotes());
    }

    private PassportedAssessmentMetadata workflowRequestToPassportedAssessmentMetadata(
            WorkflowRequest workflowRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        PassportedDTO passportedDTO = applicationDTO.getPassportedDTO();

        return new PassportedAssessmentMetadata()
                .withLegacyApplicationId(applicationDTO.getRepId().intValue())
                .withUsn(passportedDTO.getUsn().intValue())
                .withCaseManagementUnitId(passportedDTO.getCmuId().intValue())
                .withUserSession(userMapper.userDtoToUserSession(workflowRequest.getUserDTO()));
    }

    public PassportedDTO apiGetPassportedAssessmentResponseToPassportedDTO(
            ApiGetPassportedAssessmentResponse assessment,
            ApiGetPassportEvidenceResponse evidence,
            ApplicantDTO partner) {

        AssessmentStatusDTO assessmentStatusDTO = AssessmentStatusDTO.builder()
                .status(AssessmentStatusDTO.COMPLETE)
                .description(ASSESSMENT_STATUS_DESCRIPTION)
                .build();

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
            if (partner != null) {
                dto.setBenefitClaimedByPartner(true);
                dto.setPartnerDetails(applicantDTOToPartnerDTO(partner));
            }

            switch (declaredBenefit.getBenefitType()) {
                case BenefitType.INCOME_SUPPORT -> dto.setBenefitIncomeSupport(true);
                case BenefitType.JSA -> dto.setBenefitJobSeeker(declaredBenefitToJobSeekerDTO(declaredBenefit));
                case BenefitType.GSPC -> dto.setBenefitGaurenteedStatePension(true);
                case BenefitType.ESA -> dto.setBenefitEmploymentSupport(true);
                case BenefitType.UC -> dto.setBenefitUniversalCredit(true);
            }
        }

        if (evidence != null) {
            dto.setPassportSummaryEvidenceDTO(
                    passportEvidenceMapper.apiGetPassportEvidenceResponseToIncomeEvidenceSummaryDTO(evidence));
        }

        return dto;
    }

    public UserActionDTO getUserActionDTO(WorkflowRequest workflowRequest) {
        NewWorkReason newWorkReason = NewWorkReason.getFrom(workflowRequest
                .getApplicationDTO()
                .getPassportedDTO()
                .getNewWorkReason()
                .getCode());

        return userMapper.getUserActionDTO(workflowRequest, Action.CREATE_PASSPORT_ASSESSMENT, newWorkReason);
    }

    public ApiCreatePassportedAssessmentRequest workflowRequestToApiCreatePassportedAssessmentRequest(
            WorkflowRequest workflowRequest) {
        return new ApiCreatePassportedAssessmentRequest()
                .withPassportedAssessment(applicationDTOToPassportedAssessment(workflowRequest.getApplicationDTO()))
                .withPassportedAssessmentMetadata(workflowRequestToPassportedAssessmentMetadata(workflowRequest));
    }
}
