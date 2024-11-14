package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.evidence.*;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.FinancialAssessmentIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinAssIncomeEvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.util.DateUtil;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static uk.gov.justice.laa.crime.orchestration.mapper.MeansAssessmentMapper.mapChildWeightings;

@Component
@RequiredArgsConstructor
public class IncomeEvidenceMapper {

    private final UserMapper userMapper;
    private final MeansAssessmentMapper meansAssessmentMapper;

    public ApiCreateIncomeEvidenceRequest workflowRequestToApiCreateIncomeEvidenceRequest(WorkflowRequest workflowRequest) {
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        ApiApplicantDetails partnerDetails = getPartnerDetails(application.getApplicantLinks());
        Collection<AssessmentSectionSummaryDTO> initSectionSummaries = application.getAssessmentDTO()
                .getFinancialAssessmentDTO().getInitial().getSectionSummaries();

        return new ApiCreateIncomeEvidenceRequest()
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withApplicantDetails(getApplicantDetails(application.getApplicantDTO()))
                .withPartnerDetails(partnerDetails)
                .withApplicantPensionAmount(getPensionAmount(initSectionSummaries, false))
                .withPartnerPensionAmount(partnerDetails == null ? null : getPensionAmount(initSectionSummaries, true))
                .withMetadata(getMetadata(workflowRequest));
    }

    public MaatApiUpdateAssessment mapToMaatApiUpdateAssessment(WorkflowRequest workflowRequest,
                                                                RepOrderDTO repOrder,
                                                                ApiCreateIncomeEvidenceResponse evidenceResponse) {
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        String assessmentType = application.getAssessmentDTO().getFinancialAssessmentDTO().getFullAvailable() ? "FULL" : "INIT";
        InitialAssessmentDTO initialAssessment = application.getAssessmentDTO().getFinancialAssessmentDTO().getInitial();
        FullAssessmentDTO fullAssessment = application.getAssessmentDTO().getFinancialAssessmentDTO().getFull();
        Collection<AssessmentDetailDTO> assessmentDetails  = assessmentType.equals("FULL")
                ? fullAssessment.getSectionSummaries().stream().flatMap(section -> section.getAssessmentDetail().stream()).toList()
                : initialAssessment.getSectionSummaries().stream().flatMap(section -> section.getAssessmentDetail().stream()).toList();

        MaatApiUpdateAssessment updateAssessment = new MaatApiUpdateAssessment()
                .withFinancialAssessmentId(NumberUtils.toInteger(application.getAssessmentDTO().getFinancialAssessmentDTO().getId()))
                .withUserModified(workflowRequest.getUserDTO().getUserName())
                .withFinAssIncomeEvidences(getIncomeEvidences(workflowRequest, repOrder, evidenceResponse))
                .withLaaTransactionId(UUID.randomUUID().toString())
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withAssessmentType(assessmentType)
                .withCmuId(NumberUtils.toInteger(application.getCaseManagementUnitDTO().getCmuId()))
                .withFassInitStatus(initialAssessment.getAssessmnentStatusDTO().getStatus())
                .withInitialAssessmentDate(DateUtil.toLocalDateTime(initialAssessment.getAssessmentDate()))
                .withInitOtherBenefitNote(initialAssessment.getOtherBenefitNote())
                .withInitOtherIncomeNote(initialAssessment.getOtherIncomeNote())
                .withInitTotAggregatedIncome(BigDecimal.valueOf(initialAssessment.getTotalAggregatedIncome()))
                .withInitAdjustedIncomeValue(BigDecimal.valueOf(initialAssessment.getAdjustedIncomeValue()))
                .withInitNotes(initialAssessment.getNotes())
                .withInitResult(initialAssessment.getResult())
                .withInitResultReason(initialAssessment.getResultReason())
                .withInitialAscrId(NumberUtils.toInteger(initialAssessment.getCriteriaId()))
                .withInitApplicationEmploymentStatus(application.getApplicantDTO().getEmploymentStatusDTO().getCode())
                .withAssessmentDetails(meansAssessmentMapper.assessmentDetailsBuilder(assessmentDetails))
                .withChildWeightings(meansAssessmentMapper.childWeightingsBuilder(initialAssessment.getChildWeightings()))
                .withDateCompleted(application.getAssessmentDTO().getFinancialAssessmentDTO().getDateCompleted());

        if ( assessmentType.equals("FULL")) {
            updateAssessment.setFassFullStatus(fullAssessment.getAssessmnentStatusDTO().getStatus());
            updateAssessment.setFullAssessmentDate(DateUtil.toLocalDateTime(fullAssessment.getAssessmentDate()));
            updateAssessment.setFullResult(fullAssessment.getResult());
            updateAssessment.setFullResultReason(fullAssessment.getResultReason());
            updateAssessment.setFullAssessmentNotes(fullAssessment.getAssessmentNotes());
            updateAssessment.setFullAdjustedLivingAllowance(BigDecimal.valueOf(fullAssessment.getAdjustedLivingAllowance()));
            updateAssessment.setFullTotalAnnualDisposableIncome(BigDecimal.valueOf(fullAssessment.getTotalAnnualDisposableIncome()));
            updateAssessment.setFullOtherHousingNote(fullAssessment.getOtherHousingNote());
            updateAssessment.setFullTotalAggregatedExpenses(BigDecimal.valueOf(fullAssessment.getTotalAggregatedExpense()));
            updateAssessment.setFullAscrId(NumberUtils.toInteger(fullAssessment.getCriteriaId()));
        }

        return updateAssessment;
    }

    public void maatApiAssessmentResponseToApplicationDTO(MaatApiAssessmentResponse assessmentResponse,
                                                          ApplicationDTO application) {
        // TODO: Check these not needing to map values: repId, dateCreated, userCreated, cmuId, updated, userModified, rtCode, initApplicationEmploymentStatus
        Boolean fullAvailable = assessmentResponse.getAssessmentType().equals("FULL");
        InitialAssessmentDTO initialAssessment = application.getAssessmentDTO().getFinancialAssessmentDTO().getInitial();
        FullAssessmentDTO fullAssessment = application.getAssessmentDTO().getFinancialAssessmentDTO().getFull();
        IncomeEvidenceSummaryDTO incomeEvidenceSummary = application.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();

        application.getAssessmentDTO().getFinancialAssessmentDTO().setId(Long.valueOf(assessmentResponse.getId()));
        application.getAssessmentDTO().getFinancialAssessmentDTO().setFullAvailable(fullAvailable);
        application.getAssessmentDTO().getFinancialAssessmentDTO().setDateCompleted(assessmentResponse.getDateCompleted());
        application.getAssessmentDTO().getFinancialAssessmentDTO().setUsn(Long.valueOf(assessmentResponse.getUsn()));
        mapIncomeEvidenceSummary(incomeEvidenceSummary, assessmentResponse);

        if (fullAvailable) {
            mapFullAssessment(fullAssessment, assessmentResponse);
        } else {
            mapInitialAssessment(initialAssessment, assessmentResponse);
        }
    }

    private ApiApplicantDetails getPartnerDetails(Collection<ApplicantLinkDTO> applicantLinks) {
        if (applicantLinks == null) return null;

        List<ApiApplicantDetails> partnerDetails = applicantLinks
                .stream()
                .filter(link -> link.getUnlinked() == null)
                .map(link -> getApplicantDetails(link.getPartnerDTO()))
                .toList();

        if (partnerDetails.size() > 1) {
            throw new ValidationException("The applicant has more than one partner linked.");
        }

        return partnerDetails.get(0);
    }

    private ApiApplicantDetails getApplicantDetails(ApplicantDTO applicant) {
        return new ApiApplicantDetails()
                .withId(NumberUtils.toInteger(applicant.getId()))
                .withEmploymentStatus(EmploymentStatus.getFrom(applicant.getEmploymentStatusDTO().getCode()));
    }

    private BigDecimal getPensionAmount(Collection<AssessmentSectionSummaryDTO> initSectionSummaries, boolean isPartner) {
        AssessmentDetailDTO pensionDetails = initSectionSummaries
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
        ApplicationDTO application = workflowRequest.getApplicationDTO();


        return new ApiIncomeEvidenceMetadata()
                .withApplicationReceivedDate(application.getDateReceived().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .withEvidencePending(isEvidencePending(application.getAssessmentDTO().getFinancialAssessmentDTO()
                        .getIncomeEvidence()))
                .withNotes(application.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence()
                        .getIncomeEvidenceNotes())
                .withUserSession(userMapper.userDtoToUserSession(workflowRequest.getUserDTO()));
    }

    private Boolean isEvidencePending(IncomeEvidenceSummaryDTO incomeEvidence) {
        return !Stream.of(incomeEvidence.getApplicantIncomeEvidenceList(),
                        incomeEvidence.getPartnerIncomeEvidenceList(),
                        incomeEvidence.getExtraEvidenceList())
                .flatMap(Collection::stream)
                .filter(evidence -> evidence.getDateReceived() == null)
                .toList()
                .isEmpty();
    }

    private List<FinancialAssessmentIncomeEvidence> getIncomeEvidences(WorkflowRequest workflowRequest,
                                                                       RepOrderDTO repOrder,
                                                                       ApiCreateIncomeEvidenceResponse evidenceResponse) {
        Integer financialAssessmentId = NumberUtils.toInteger(
                workflowRequest.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getId());
        FinancialAssessmentDTO existingFinancialAssessment = repOrder.getFinancialAssessments()
                .stream()
                .filter(assessment -> assessment.getId().equals(financialAssessmentId))
                .findFirst()
                .orElse(null);
        UserDTO user = workflowRequest.getUserDTO();

        return Stream.of(getEvidences(evidenceResponse.getApplicantEvidenceItems(), existingFinancialAssessment, user),
                        getEvidences(evidenceResponse.getPartnerEvidenceItems(), existingFinancialAssessment, user))
                .flatMap(List::stream)
                .toList();
    }

    private List<FinancialAssessmentIncomeEvidence> getEvidences(ApiIncomeEvidenceItems evidenceItems,
                                                                 FinancialAssessmentDTO existingFinancialAssessment,
                                                                 UserDTO user) {
        Integer applicantId = evidenceItems.getApplicantDetails().getId();

        return evidenceItems.getIncomeEvidenceItems()
                .stream()
                .map(evidence -> new FinancialAssessmentIncomeEvidence()
                        .withId(evidence.getId())
                        .withDateReceived(getDateReceived(applicantId, evidence, existingFinancialAssessment))
                        .withActive("Y")
                        .withIncomeEvidence(evidence.getEvidenceType().getName())
                        .withMandatory(evidence.getMandatory() ? "Y" : "N")
                        .withApplicant(applicantId)
                        .withOtherText(evidence.getDescription())
                        .withUserCreated(user.getUserName()))
                .toList();
    }

    private LocalDateTime getDateReceived(Integer applicantId,
                                          ApiIncomeEvidence evidence,
                                          FinancialAssessmentDTO existingFinancialAssessment) {
        return existingFinancialAssessment.getFinAssIncomeEvidences()
                .stream()
                .filter(existingEvidence -> existingEvidence.getApplicant().getId().equals(applicantId)
                        && existingEvidence.getIncomeEvidence().equals(evidence.getEvidenceType().getName()))
                .map(FinAssIncomeEvidenceDTO::getDateReceived)
                .findFirst()
                .orElse(null);
    }

    private void mapIncomeEvidenceSummary(IncomeEvidenceSummaryDTO incomeEvidenceSummary,
                                          MaatApiAssessmentResponse assessmentResponse) {
        incomeEvidenceSummary.setEvidenceDueDate(DateUtil.toDate(assessmentResponse.getIncomeEvidenceDueDate()));
        incomeEvidenceSummary.setUpliftAppliedDate(DateUtil.toDate(assessmentResponse.getIncomeUpliftApplyDate()));
        incomeEvidenceSummary.setUpliftRemovedDate(DateUtil.toDate(assessmentResponse.getIncomeUpliftRemoveDate()));
        incomeEvidenceSummary.setIncomeEvidenceNotes(assessmentResponse.getIncomeEvidenceNotes());
        // TODO: How to map flat list of evidences to three seperate lists in appDTO for appl, partner and extra
        // incomeEvidences
    }

    private void mapInitialAssessment(InitialAssessmentDTO initialAssessment,
                                      MaatApiAssessmentResponse assessmentResponse) {
        initialAssessment.setCriteriaId(Long.valueOf(assessmentResponse.getInitialAscrId()));
        initialAssessment.setNewWorkReason(buildNewWorkReason(NewWorkReason.getFrom(assessmentResponse.getNworCode())));
        initialAssessment.setAssessmnentStatusDTO(buildAssessmentStatus(CurrentStatus.getFrom(assessmentResponse.getFassInitStatus())));
        initialAssessment.setAssessmentDate(DateUtil.toDate(assessmentResponse.getInitialAssessmentDate()));
        initialAssessment.setOtherBenefitNote(assessmentResponse.getInitOtherBenefitNote());
        initialAssessment.setOtherIncomeNote(assessmentResponse.getInitOtherIncomeNote());
        initialAssessment.setTotalAggregatedIncome(assessmentResponse.getInitTotAggregatedIncome().doubleValue());
        initialAssessment.setAdjustedIncomeValue(assessmentResponse.getInitAdjustedIncomeValue().doubleValue());
        initialAssessment.setNotes(assessmentResponse.getInitNotes());
        initialAssessment.setResult(assessmentResponse.getInitResult());
        initialAssessment.setResultReason(assessmentResponse.getInitResultReason());
        initialAssessment.setChildWeightings(mapChildWeightings(assessmentResponse.getChildWeightings()));
        // TODO: How to organise the assessment details received into the appropriate section summaries assessment DTOs???
        // "assessmentDetails",
    }

    private void mapFullAssessment(FullAssessmentDTO fullAssessment, MaatApiAssessmentResponse assessmentResponse) {
        fullAssessment.setCriteriaId(Long.valueOf(assessmentResponse.getFullAscrId()));
        fullAssessment.setAssessmnentStatusDTO(buildAssessmentStatus(CurrentStatus.getFrom(assessmentResponse.getFassFullStatus())));
        fullAssessment.setAssessmentDate(DateUtil.toDate(assessmentResponse.getFullAssessmentDate()));
        fullAssessment.setResult(assessmentResponse.getFullResult());
        fullAssessment.setResultReason(assessmentResponse.getFullResultReason());
        fullAssessment.setAssessmentNotes(assessmentResponse.getFullAssessmentNotes());
        fullAssessment.setAdjustedLivingAllowance(assessmentResponse.getFullAdjustedLivingAllowance().doubleValue());
        fullAssessment.setTotalAnnualDisposableIncome(assessmentResponse.getFullTotalAnnualDisposableIncome().doubleValue());
        fullAssessment.setOtherHousingNote(assessmentResponse.getFullOtherHousingNote());
        fullAssessment.setTotalAggregatedExpense(assessmentResponse.getFullTotalAggregatedExpenses().doubleValue());
        // TODO: How to organise the assessment details received into the appropriate section summaries assessment DTOs???
        // "assessmentDetails",
    }

    private NewWorkReasonDTO buildNewWorkReason(NewWorkReason newWorkReason) {
        return NewWorkReasonDTO.builder()
                .code(newWorkReason.getCode())
                .description(newWorkReason.getDescription())
                .type(newWorkReason.getType())
                .build();
    }

    private AssessmentStatusDTO buildAssessmentStatus(CurrentStatus assessmentStatus) {
        return AssessmentStatusDTO.builder()
                .status(assessmentStatus.getStatus())
                .description(assessmentStatus.getDescription())
                .build();
    }

}
