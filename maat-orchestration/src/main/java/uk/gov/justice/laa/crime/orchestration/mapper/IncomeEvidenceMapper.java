package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.evidence.*;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.FinancialAssessmentIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.common.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.EvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.util.DateUtil;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class IncomeEvidenceMapper {

    private final UserMapper userMapper;
    private final MeansAssessmentMapper meansAssessmentMapper;

    public ApiCreateIncomeEvidenceRequest workflowRequestToApiCreateIncomeEvidenceRequest(WorkflowRequest workflowRequest) {
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        ApiApplicantDetails partnerDetails = getPartnerDetails(application.getApplicantLinks());
        AssessmentDetailDTO pensionDetails = getPensionDetails(application);

        return new ApiCreateIncomeEvidenceRequest()
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withApplicantDetails(getApplicantDetails(application.getApplicantDTO()))
                .withPartnerDetails(partnerDetails)
                .withApplicantPensionAmount(getPensionAmount(pensionDetails, false))
                .withPartnerPensionAmount(partnerDetails == null ? null : getPensionAmount(pensionDetails, true))
                .withMetadata(getMetadata(application, workflowRequest.getUserDTO()));
    }

    private static @Nullable AssessmentDetailDTO getPensionDetails(ApplicationDTO application) {
        return application.getAssessmentDTO().getFinancialAssessmentDTO().getInitial().getSectionSummaries()
                .stream()
                .flatMap(section -> section.getAssessmentDetail().stream())
                .filter(detail -> detail.getDescription().equals("Income from Private Pension(s)"))
                .findFirst()
                .orElse(null);
    }

    public MaatApiUpdateAssessment mapToMaatApiUpdateAssessment(WorkflowRequest workflowRequest,
                                                                RepOrderDTO repOrder,
                                                                ApiCreateIncomeEvidenceResponse evidenceResponse) {
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        AssessmentType assessmentType = Boolean.TRUE.equals(application.getAssessmentDTO().getFinancialAssessmentDTO().getFullAvailable())
                ? AssessmentType.FULL : AssessmentType.INIT;
        InitialAssessmentDTO initialAssessment = application.getAssessmentDTO().getFinancialAssessmentDTO().getInitial();
        FullAssessmentDTO fullAssessment = application.getAssessmentDTO().getFinancialAssessmentDTO().getFull();
        Collection<AssessmentDetailDTO> assessmentDetails = assessmentType.equals(AssessmentType.FULL)
                ? fullAssessment.getSectionSummaries().stream().flatMap(section -> section.getAssessmentDetail().stream()).toList()
                : initialAssessment.getSectionSummaries().stream().flatMap(section -> section.getAssessmentDetail().stream()).toList();

        MaatApiUpdateAssessment updateAssessment = new MaatApiUpdateAssessment()
                .withFinancialAssessmentId(NumberUtils.toInteger(application.getAssessmentDTO().getFinancialAssessmentDTO().getId()))
                .withUserModified(workflowRequest.getUserDTO().getUserName())
                .withFinAssIncomeEvidences(getIncomeEvidences(workflowRequest, repOrder, evidenceResponse))
                .withLaaTransactionId(UUID.randomUUID().toString())
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withAssessmentType(assessmentType.getType())
                .withCmuId(NumberUtils.toInteger(application.getCaseManagementUnitDTO().getCmuId()))
                .withFassInitStatus(initialAssessment.getAssessmnentStatusDTO().getStatus())
                .withInitialAssessmentDate(DateUtil.toLocalDateTime(initialAssessment.getAssessmentDate()))
                .withInitOtherBenefitNote(initialAssessment.getOtherBenefitNote())
                .withInitOtherIncomeNote(initialAssessment.getOtherIncomeNote())
                .withInitTotAggregatedIncome(
                        BigDecimal.valueOf(ofNullable(initialAssessment.getTotalAggregatedIncome()).orElse(0.0)))
                .withInitAdjustedIncomeValue(
                        BigDecimal.valueOf(ofNullable(initialAssessment.getAdjustedIncomeValue()).orElse(0.0)))
                .withInitNotes(initialAssessment.getNotes())
                .withInitResult(initialAssessment.getResult())
                .withInitResultReason(initialAssessment.getResultReason())
                .withInitialAscrId(NumberUtils.toInteger(initialAssessment.getCriteriaId()))
                .withInitApplicationEmploymentStatus(application.getApplicantDTO().getEmploymentStatusDTO().getCode())
                .withAssessmentDetails(meansAssessmentMapper.assessmentDetailsBuilder(assessmentDetails))
                .withChildWeightings(meansAssessmentMapper.childWeightingsBuilder(initialAssessment.getChildWeightings()))
                .withDateCompleted(application.getAssessmentDTO().getFinancialAssessmentDTO().getDateCompleted());

        if (assessmentType.equals(AssessmentType.FULL)) {
            updateAssessment.setFassFullStatus(fullAssessment.getAssessmnentStatusDTO().getStatus());
            updateAssessment.setFullAssessmentDate(DateUtil.toLocalDateTime(fullAssessment.getAssessmentDate()));
            updateAssessment.setFullResult(fullAssessment.getResult());
            updateAssessment.setFullResultReason(fullAssessment.getResultReason());
            updateAssessment.setFullAssessmentNotes(fullAssessment.getAssessmentNotes());
            updateAssessment.setFullAdjustedLivingAllowance(
                    BigDecimal.valueOf(ofNullable(fullAssessment.getAdjustedLivingAllowance()).orElse(0.0)));
            updateAssessment.setFullTotalAnnualDisposableIncome(
                    BigDecimal.valueOf(ofNullable(fullAssessment.getTotalAnnualDisposableIncome()).orElse(0.0)));
            updateAssessment.setFullOtherHousingNote(fullAssessment.getOtherHousingNote());
            updateAssessment.setFullTotalAggregatedExpenses(
                    BigDecimal.valueOf(ofNullable(fullAssessment.getTotalAggregatedExpense()).orElse(0.0)));
            updateAssessment.setFullAscrId(NumberUtils.toInteger(fullAssessment.getCriteriaId()));
        }

        return updateAssessment;
    }

    public void maatApiAssessmentResponseToApplicationDTO(MaatApiAssessmentResponse assessmentResponse,
                                                          ApplicationDTO application) {
        IncomeEvidenceSummaryDTO incomeEvidenceSummary =
                application.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();
        List<uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceDTO> applicantEvidence = new ArrayList<>();
        List<uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceDTO> partnerEvidence = new ArrayList<>();

        Integer applicantId = NumberUtils.toInteger(application.getApplicantDTO().getId());
        for (uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence evidence : assessmentResponse.getFinAssIncomeEvidences()) {

            if (evidence.getApplicantId().equals(applicantId)) {
                applicantEvidence.add(meansAssessmentMapper.getEvidenceDTO(evidence));
            } else {
                partnerEvidence.add(meansAssessmentMapper.getEvidenceDTO(evidence));
            }
        }

        incomeEvidenceSummary.setApplicantIncomeEvidenceList(applicantEvidence);
        incomeEvidenceSummary.setPartnerIncomeEvidenceList(partnerEvidence);
        incomeEvidenceSummary.setEvidenceReceivedDate(DateUtil.toDate(assessmentResponse.getEvidenceReceivedDate()));
        incomeEvidenceSummary.setUpliftAppliedDate(DateUtil.toDate(assessmentResponse.getIncomeUpliftApplyDate()));
        incomeEvidenceSummary.setUpliftRemovedDate(DateUtil.toDate(assessmentResponse.getIncomeUpliftRemoveDate()));
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

        return partnerDetails.isEmpty() ? null : partnerDetails.get(0);
    }

    private ApiApplicantDetails getApplicantDetails(ApplicantDTO applicant) {
        return new ApiApplicantDetails()
                .withId(NumberUtils.toInteger(applicant.getId()))
                .withEmploymentStatus(EmploymentStatus.getFrom(applicant.getEmploymentStatusDTO().getCode()));
    }

    private BigDecimal getPensionAmount(AssessmentDetailDTO pensionDetails, boolean isPartner) {
        if (pensionDetails == null) return BigDecimal.ZERO;

        if (isPartner) {
            return calculatePensionAmount(pensionDetails.getPartnerAmount(),
                    pensionDetails.getPartnerFrequency().getAnnualWeighting());
        }

        return calculatePensionAmount(pensionDetails.getApplicantAmount(),
                pensionDetails.getApplicantFrequency().getAnnualWeighting());
    }

    private BigDecimal calculatePensionAmount(Double amount, Long weighting) {
        return weighting == null ? BigDecimal.ZERO : BigDecimal.valueOf(amount * weighting);
    }

    private ApiIncomeEvidenceMetadata getMetadata(ApplicationDTO application, UserDTO user) {

        return new ApiIncomeEvidenceMetadata()
                .withApplicationReceivedDate(application.getDateReceived().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .withEvidencePending(isEvidencePending(application.getAssessmentDTO().getFinancialAssessmentDTO()
                        .getIncomeEvidence()))
                .withNotes(application.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence()
                        .getIncomeEvidenceNotes())
                .withUserSession(userMapper.userDtoToUserSession(user));
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
        List<EvidenceDTO> existingEvidences = new ArrayList<>();
        repOrder.getFinancialAssessments()
                .forEach(financialAssessmentDTO -> existingEvidences.addAll(financialAssessmentDTO.getFinAssIncomeEvidences()));
        repOrder.getPassportAssessments()
                .forEach(passportAssessmentDTO -> existingEvidences.addAll(passportAssessmentDTO.getPassportAssessmentEvidences()));

        UserDTO user = workflowRequest.getUserDTO();

        return Stream.of(getEvidences(evidenceResponse.getApplicantEvidenceItems(), existingEvidences, user, Boolean.FALSE),
                        getEvidences(evidenceResponse.getPartnerEvidenceItems(), existingEvidences, user, Boolean.TRUE))
                .flatMap(List::stream)
                .toList();
    }

    private List<FinancialAssessmentIncomeEvidence> getEvidences(ApiIncomeEvidenceItems evidenceItems,
                                                                 List<EvidenceDTO> existingEvidences,
                                                                 UserDTO user,
                                                                 boolean isPartner) {
        if (null !=evidenceItems) {
            Integer applicantId = evidenceItems.getApplicantDetails().getId();

            return evidenceItems.getIncomeEvidenceItems()
                    .stream()
                    .map(evidence -> new FinancialAssessmentIncomeEvidence()
                            .withId(evidence.getId())
                            .withDateReceived(evidence.getEvidenceType().isExtra() ? DateUtil.convertDateToDateTime(evidence.getDateReceived())
                                    : getDateReceived(applicantId, evidence, existingEvidences))
                            .withActive("Y")
                            .withIncomeEvidence(evidence.getEvidenceType().getName())
                            .withMandatory(Boolean.TRUE.equals(evidence.getMandatory()) ? "Y" : "N")
                            .withApplicant(applicantId)
                            .withOtherText(evidence.getDescription())
                            .withAdhoc(getAdhoc(isPartner, evidence))
                            .withUserCreated(user.getUserName()))
                    .toList();
        }
        return Collections.emptyList();
    }

    private static String getAdhoc(boolean isPartner, ApiIncomeEvidence evidence) {
        
        if (evidence.getEvidenceType().isExtra()) {
            
            return isPartner ? ApplicantType.PARTNER.name() : ApplicantType.APPLICANT.name();
        }

        return null;
    }

    private LocalDateTime getDateReceived(Integer applicantId,
                                          ApiIncomeEvidence evidence,
                                          List<EvidenceDTO> existingEvidences) {

        return existingEvidences.stream()
                .filter(existingEvidence -> existingEvidence.getApplicant().getId().equals(applicantId)
                        && existingEvidence.getIncomeEvidence().equals(evidence.getEvidenceType().getName()))
                .max(Comparator.comparing(uk.gov.justice.laa.crime.orchestration.dto.maat_api.EvidenceDTO::getDateCreated))
                .map(uk.gov.justice.laa.crime.orchestration.dto.maat_api.EvidenceDTO::getDateReceived)
                .orElse(null);
    }

    public ApiUpdateIncomeEvidenceRequest workflowRequestToApiUpdateIncomeEvidenceRequest(ApplicationDTO applicationDTO, UserDTO userDTO) {
        ApiApplicantDetails partnerDetails = getPartnerDetails(applicationDTO.getApplicantLinks());
        AssessmentDetailDTO pensionDetails = getPensionDetails(applicationDTO);
        IncomeEvidenceSummaryDTO incomeEvidenceSummaryDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();
        ApiIncomeEvidenceItems applicantIncomeEvidenceItems = new ApiIncomeEvidenceItems()
                .withApplicantDetails(getApplicantDetails(applicationDTO.getApplicantDTO()))
                .withIncomeEvidenceItems(mapIncomeEvidenceItems(incomeEvidenceSummaryDTO.getApplicantIncomeEvidenceList(),
                        incomeEvidenceSummaryDTO.getExtraEvidenceList(),
                        false));

        return new ApiUpdateIncomeEvidenceRequest()
                .withMagCourtOutcome(MagCourtOutcome.getFrom(applicationDTO.getMagsOutcomeDTO().getOutcome()))
                .withEvidenceDueDate(DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getEvidenceDueDate()))
                .withEvidenceReceivedDate(DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getEvidenceReceivedDate()))
                .withApplicantPensionAmount(getPensionAmount(pensionDetails, false))
                .withApplicantEvidenceItems(applicantIncomeEvidenceItems)
                .withPartnerPensionAmount(partnerDetails == null ? null : getPensionAmount(pensionDetails, true))
                .withPartnerEvidenceItems(partnerDetails == null ? null : getPartnerIncomeEvidenceItems(incomeEvidenceSummaryDTO, partnerDetails))
                .withMetadata(getMetadata(applicationDTO, userDTO));
    }

    private ApiIncomeEvidenceItems getPartnerIncomeEvidenceItems(IncomeEvidenceSummaryDTO incomeEvidenceSummaryDTO,
                                                                 ApiApplicantDetails partnerDetails) {
        return new ApiIncomeEvidenceItems()
                .withApplicantDetails(partnerDetails)
                .withIncomeEvidenceItems(mapIncomeEvidenceItems(incomeEvidenceSummaryDTO.getPartnerIncomeEvidenceList(),
                        incomeEvidenceSummaryDTO.getExtraEvidenceList(),
                        true));
    }

    private List<ApiIncomeEvidence> mapIncomeEvidenceItems(Collection<uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceDTO> incomeEvidenceList,
                                                           Collection<ExtraEvidenceDTO> extraEvidenceList,
                                                           boolean isPartner) {

        List<ApiIncomeEvidence> incomeEvidences = new ArrayList<>();

        incomeEvidenceList.forEach(evidence -> incomeEvidences.add(new ApiIncomeEvidence()
                .withId(NumberUtils.toInteger(evidence.getId()))
                .withMandatory(true)
                .withDateReceived(evidence.getDateReceived() != null ? LocalDate.ofInstant(evidence.getDateReceived().toInstant(), ZoneId.systemDefault()) : null)
                .withEvidenceType(IncomeEvidenceType.getFrom(evidence.getEvidenceTypeDTO().getEvidence()))
        ));

        extraEvidenceList.stream()
                .filter(extraEvidenceDTO -> (isPartner && "P".equals(extraEvidenceDTO.getAdhoc()))
                        || (!isPartner && "A".equals(extraEvidenceDTO.getAdhoc())))
                .forEach(extraEvidence -> incomeEvidences.add(new ApiIncomeEvidence()
                        .withId(NumberUtils.toInteger(extraEvidence.getId()))
                        .withDescription(extraEvidence.getOtherText())
                        .withMandatory(true)
                        .withDateReceived(extraEvidence.getDateReceived() != null ? LocalDate.ofInstant(extraEvidence.getDateReceived().toInstant(), ZoneId.systemDefault()) : null)
                        .withEvidenceType(IncomeEvidenceType.getFrom(extraEvidence.getEvidenceTypeDTO().getEvidence()))
                ));

        return incomeEvidences;
    }

    public MaatApiUpdateAssessment mapUpdateEvidenceToMaatApiUpdateAssessment(WorkflowRequest workflowRequest,
                                                                              RepOrderDTO repOrderDTO,
                                                                              ApiUpdateIncomeEvidenceResponse evidenceResponse) {

        MaatApiUpdateAssessment maatApiUpdateAssessment = mapToMaatApiUpdateAssessment(workflowRequest, repOrderDTO, evidenceResponse);
        maatApiUpdateAssessment.withIncomeEvidenceDueDate(DateUtil.convertDateToDateTime(evidenceResponse.getDueDate()));
        maatApiUpdateAssessment.withEvidenceReceivedDate(DateUtil.convertDateToDateTime(evidenceResponse.getAllEvidenceReceivedDate()));
        maatApiUpdateAssessment.withIncomeUpliftApplyDate(DateUtil.convertDateToDateTime(evidenceResponse.getUpliftAppliedDate()));
        maatApiUpdateAssessment.withIncomeUpliftRemoveDate(DateUtil.convertDateToDateTime(evidenceResponse.getUpliftRemovedDate()));
        return maatApiUpdateAssessment;
    }
}
