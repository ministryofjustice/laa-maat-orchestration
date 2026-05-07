package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
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
        if (passportedDTO.getBenefitEmploymentSupport()) {
            return BenefitType.ESA;
        } else if (passportedDTO.getBenefitIncomeSupport()) {
            return BenefitType.INCOME_SUPPORT;
        } else if (passportedDTO.getBenefitJobSeeker() != null
                && passportedDTO.getBenefitJobSeeker().getIsJobSeeker()) {
            return BenefitType.JSA;
        } else if (passportedDTO.getBenefitGaurenteedStatePension()) {
            return BenefitType.GSPC;
        } else if (passportedDTO.getBenefitUniversalCredit()) {
            return BenefitType.UC;
        } else {
            return null;
        }
    }

    private DeclaredBenefit mapDeclaredBenefit(ApplicationDTO applicationDTO) {
        PassportedDTO passportedDTO = applicationDTO.getPassportedDTO();
        BenefitType benefitType = mapBenefitType(passportedDTO);
        BenefitRecipient benefitRecipient = applicationDTO.getPassportedDTO().getBenefitClaimedByPartner()
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
            ApiGetPassportedAssessmentResponse response, ApplicantDTO applicantDTO) {

        AssessmentStatusDTO assessmentStatusDTO = AssessmentStatusDTO.builder()
                .status(AssessmentStatusDTO.COMPLETE)
                .description(ASSESSMENT_STATUS_DESCRIPTION)
                .build();

        // TODO: Need to populate passportSummaryEvidenceDTO following completion of LCAM-2002
        // TODO: Might need to amend mapping of legacy age related values following completion of LCAM-2016
        // Not setting dwpResult and dwpWhoChecked as these are no longer used in MAAT and so can left as null
        PassportedDTO dto = PassportedDTO.builder()
                .passportedId(Long.valueOf(response.getLegacyAssessmentId()))
                .cmuId(Long.valueOf(response.getCaseManagementUnitId()))
                .date(DateUtil.toDate(response.getAssessmentDate()))
                .assessementStatusDTO(assessmentStatusDTO)
                .passportConfirmationDTO(
                        passportAssessmentDecisionReasonToPassportConfirmationDTO(response.getDecisionReason()))
                .newWorkReason(newWorkReasonToNewWorkReasonDTO(response.getAssessmentReason()))
                .notes(response.getNotes())
                .result(response.getAssessmentDecision().getCode())
                .under18HeardYouthCourt(response.getDeclaredUnder18())
                .build();

        if (response.getUsn() != null) {
            dto.setUsn(Long.valueOf(response.getUsn()));
        }

        if (response.getReviewType() != null) {
            dto.setReviewType(reviewTypeToReviewTypeDTO(response.getReviewType()));
        }

        DeclaredBenefit declaredBenefit = response.getDeclaredBenefit();
        if (declaredBenefit != null) {
            if (applicantDTO != null) {
                dto.setBenefitClaimedByPartner(true);
                dto.setPartnerDetails(applicantDTOToPartnerDTO(applicantDTO));
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
