package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.evidence.ApiApplicantDetails;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceMetadata;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class IncomeEvidenceMapper {

    UserMapper userMapper;

    public ApiCreateIncomeEvidenceRequest workflowRequestToApiCreateIncomeEvidenceRequest(WorkflowRequest workflowRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        ApiApplicantDetails partnerDetails = getPartnerDetails(applicationDTO.getApplicantLinks());
        Collection<AssessmentSectionSummaryDTO> sectionSummaries = applicationDTO.getAssessmentDTO()
                .getFinancialAssessmentDTO().getInitial().getSectionSummaries();

        return new ApiCreateIncomeEvidenceRequest()
                .withMagCourtOutcome(MagCourtOutcome.getFrom(applicationDTO.getMagsOutcomeDTO().getOutcome()))
                .withApplicantDetails(getApplicantDetails(applicationDTO.getApplicantDTO()))
                .withPartnerDetails(partnerDetails)
                .withApplicantPensionAmount(getPensionAmount(sectionSummaries, false))
                .withPartnerPensionAmount(partnerDetails == null ? null : getPensionAmount(sectionSummaries, true))
                .withMetadata(getMetadata(workflowRequest));
    }

    private ApiApplicantDetails getPartnerDetails(Collection<ApplicantLinkDTO> applicantLinks) {
        if (applicantLinks == null) return null;

        List<ApiApplicantDetails> partnerDTOs = applicantLinks
                .stream()
                .filter(link -> link.getUnlinked() == null)
                .map(link -> getApplicantDetails(link.getPartnerDTO()))
                .toList();

        if (partnerDTOs.size() > 1) {
            throw new ValidationException("The applicant has more than one partner linked.");
        }

        return partnerDTOs.get(0);
    }

    private ApiApplicantDetails getApplicantDetails(ApplicantDTO applicantDTO) {
        return new ApiApplicantDetails()
                .withId(NumberUtils.toInteger(applicantDTO.getId()))
                .withEmploymentStatus(EmploymentStatus.getFrom(applicantDTO.getEmploymentStatusDTO().getCode()));
    }

    private BigDecimal getPensionAmount(Collection<AssessmentSectionSummaryDTO> sectionSummaries, boolean isPartner) {
        AssessmentDetailDTO pensionDetails = sectionSummaries
                .stream()
                .flatMap(section -> section.getAssessmentDetail().stream())
                .filter(detail -> detail.getDescription().equals("Income from Private Pension(s)"))
                .findFirst()
                .orElse(null);

        if (pensionDetails == null) return BigDecimal.ZERO;

        if (isPartner) {
            return calculatePensionAmount(pensionDetails.getPartnerAmount(),
                    pensionDetails.getPartnerFrequency().getAnnualWeighting());
        } else {
            return calculatePensionAmount(pensionDetails.getApplicantAmount(),
                    pensionDetails.getApplicantFrequency().getAnnualWeighting());
        }
    }

    private BigDecimal calculatePensionAmount(Double amount, Long weighting) {
        return weighting == null ? BigDecimal.ZERO : BigDecimal.valueOf(amount * weighting);
    }

    private ApiIncomeEvidenceMetadata getMetadata(WorkflowRequest workflowRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();


        return new ApiIncomeEvidenceMetadata()
                .withApplicationReceivedDate(applicationDTO.getDateReceived().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .withEvidencePending(isEvidencePending(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO()
                        .getIncomeEvidence()))
                .withNotes(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence()
                        .getIncomeEvidenceNotes())
                .withUserSession(userMapper.userDtoToUserSession(workflowRequest.getUserDTO()));
    }

    private Boolean isEvidencePending(IncomeEvidenceSummaryDTO incomeEvidence) {
        return !Stream.of(incomeEvidence.getApplicantIncomeEvidenceList(), incomeEvidence.getPartnerIncomeEvidenceList(),
                        incomeEvidence.getExtraEvidenceList())
                .flatMap(Collection::stream)
                .filter(evidence -> evidence.getDateReceived() == null)
                .toList()
                .isEmpty();
    }
}
