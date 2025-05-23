package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.meansassessment.*;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.util.DateUtil;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Optional.ofNullable;
import static uk.gov.justice.laa.crime.util.DateUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentMapper {
    private final UserMapper userMapper;

    public ApiCreateMeansAssessmentRequest workflowRequestToCreateAssessmentRequest(WorkflowRequest request) {
        ApplicationDTO application = request.getApplicationDTO();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initial = financialAssessmentDTO.getInitial();
        AssessmentType assessmentType = Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())
                ? AssessmentType.FULL : AssessmentType.INIT;
        return new ApiCreateMeansAssessmentRequest()
                .withLaaTransactionId(UUID.randomUUID().toString())
                .withAssessmentStatus(CurrentStatus.getFrom(initial.getAssessmnentStatusDTO().getStatus()))
                .withAssessmentType(assessmentType)
                .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                .withChildWeightings(childWeightingsBuilder(initial.getChildWeightings()))
                .withCmuId(NumberUtils.toInteger(application.getCaseManagementUnitDTO().getCmuId()))
                .withCrownCourtOverview(crownCourtOverviewBuilder(application.getCrownCourtOverviewDTO()))
                .withEmploymentStatus(application.getApplicantDTO().getEmploymentStatusDTO().getCode())
                .withHasPartner(application.getApplicantHasPartner())
                .withIncomeEvidenceSummary(incomeEvidenceSummaryBuilder(
                        financialAssessmentDTO.getIncomeEvidence()))
                .withInitAssessmentNotes(initial.getNotes())
                .withInitialAssessmentDate(toLocalDateTime(initial.getAssessmentDate()))
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withOtherBenefitNote(initial.getOtherBenefitNote())
                .withOtherIncomeNote(initial.getOtherIncomeNote())
                .withPartnerContraryInterest(
                        isPartnerContraryInterest(application)
                )
                .withSectionSummaries(sectionSummariesBuilder(initial.getSectionSummaries()))
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withUserSession(userMapper.userDtoToUserSession(request.getUserDTO()))
                .withNewWorkReason(NewWorkReason.getFrom(initial.getNewWorkReason().getCode()))
                .withReviewType(ReviewType.getFrom(initial.getReviewType().getCode()))
                .withUsn(NumberUtils.toInteger(application.getUsn()));
    }

    public ApiUpdateMeansAssessmentRequest workflowRequestToUpdateAssessmentRequest(WorkflowRequest request) {
        ApplicationDTO application = request.getApplicationDTO();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        AssessmentType assessmentType = Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())
                ? AssessmentType.FULL : AssessmentType.INIT;
        return new ApiUpdateMeansAssessmentRequest()
                .withLaaTransactionId(UUID.randomUUID().toString())
                .withAssessmentStatus(
                        CurrentStatus.getFrom(
                                assessmentType == AssessmentType.INIT ?
                                        initialAssessmentDTO.getAssessmnentStatusDTO().getStatus() :
                                        fullAssessmentDTO.getAssessmnentStatusDTO().getStatus()
                        )
                )
                .withAssessmentType(assessmentType)
                .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                .withChildWeightings(childWeightingsBuilder(initialAssessmentDTO.getChildWeightings()))
                .withCmuId(NumberUtils.toInteger(application.getCaseManagementUnitDTO().getCmuId()))
                .withCrownCourtOverview(crownCourtOverviewBuilder(application.getCrownCourtOverviewDTO()))
                .withEmploymentStatus(application.getApplicantDTO().getEmploymentStatusDTO().getCode())
                .withHasPartner(application.getApplicantHasPartner())
                .withIncomeEvidenceSummary(incomeEvidenceSummaryBuilder(
                        financialAssessmentDTO.getIncomeEvidence()))
                .withInitAssessmentNotes(initialAssessmentDTO.getNotes())
                .withInitialAssessmentDate(toLocalDateTime(initialAssessmentDTO.getAssessmentDate()))
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withOtherBenefitNote(initialAssessmentDTO.getOtherBenefitNote())
                .withOtherIncomeNote(initialAssessmentDTO.getOtherIncomeNote())
                .withPartnerContraryInterest(
                        isPartnerContraryInterest(application)
                )
                .withNewWorkReason(
                        assessmentType == AssessmentType.INIT ?
                                NewWorkReason.getFrom(initialAssessmentDTO.getNewWorkReason().getCode()) : null
                )
                .withSectionSummaries(
                        sectionSummariesBuilder(
                                assessmentType == AssessmentType.FULL ? fullAssessmentDTO.getSectionSummaries()
                                        : initialAssessmentDTO.getSectionSummaries()
                        )
                )
                .withIncomeEvidence(mapIncomeEvidence(application))
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withUserSession(userMapper.userDtoToUserSession(request.getUserDTO()))
                .withFinancialAssessmentId(financialAssessmentDTO.getId())
                .withFullAssessmentDate(toLocalDateTime(fullAssessmentDTO.getAssessmentDate()))
                .withOtherHousingNote(fullAssessmentDTO.getOtherHousingNote())
                .withInitTotalAggregatedIncome(BigDecimal.valueOf(initialAssessmentDTO.getTotalAggregatedIncome()))
                .withFullAssessmentNotes(fullAssessmentDTO.getAssessmentNotes());
    }

    private List<ApiIncomeEvidence> mapIncomeEvidence(ApplicationDTO applicationDTO) {

        IncomeEvidenceSummaryDTO incomeEvidenceSummary =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();
        int applicantId = NumberUtils.toInteger(applicationDTO.getApplicantDTO().getId());
        List<ApiIncomeEvidence> incomeEvidence = new ArrayList<>(incomeEvidenceSummary.getApplicantIncomeEvidenceList()
                .stream()
                .map(evidenceItem -> mapToApiIncomeEvidence(evidenceItem, applicantId))
                .toList());

        Optional<Integer> partnerId = getPartnerId(applicationDTO);
        partnerId.ifPresent(id -> incomeEvidence.addAll(
                incomeEvidenceSummary.getPartnerIncomeEvidenceList()
                        .stream()
                        .map(evidenceItem -> mapToApiIncomeEvidence(evidenceItem, id))
                        .toList()
        ));

        incomeEvidence.addAll(
                incomeEvidenceSummary.getExtraEvidenceList()
                        .stream()
                        .map(evidenceItem -> mapToExtraApiIncomeEvidence(evidenceItem, applicationDTO,
                                                                         partnerId.orElse(null)
                        ))
                        .toList()
        );

        return incomeEvidence;
    }
    
    private static Optional<Integer> getPartnerId(ApplicationDTO applicationDTO) {
        if (applicationDTO.getApplicantLinks() != null) {
            return applicationDTO.getApplicantLinks()
                    .stream()
                    .filter(link -> link.getUnlinked() == null)
                    .map(link -> NumberUtils.toInteger(link.getPartnerDTO().getId()))
                    .findFirst();
        }
        return Optional.empty();
    }

    private ApiIncomeEvidence mapToApiIncomeEvidence(EvidenceDTO evidenceItem, int applicantId) {
        return new ApiIncomeEvidence()
                .withId(NumberUtils.toInteger(evidenceItem.getId()))
                .withDateReceived(DateUtil.toLocalDateTime(evidenceItem.getDateReceived()))
                .withIncomeEvidence(evidenceItem.getEvidenceTypeDTO().getEvidence())
                .withApplicantId(applicantId);
    }

    private ApiIncomeEvidence mapToExtraApiIncomeEvidence(ExtraEvidenceDTO evidenceItem, ApplicationDTO applicationDTO, Integer partnerId) {
        Integer applicantId = evidenceItem.getAdhoc().equals("A") ?
                NumberUtils.toInteger(applicationDTO.getApplicantDTO().getId()) : partnerId;

        return new ApiIncomeEvidence()
                .withId(NumberUtils.toInteger(evidenceItem.getId()))
                .withDateReceived(DateUtil.toLocalDateTime(evidenceItem.getDateReceived()))
                .withIncomeEvidence(evidenceItem.getEvidenceTypeDTO().getEvidence())
                .withApplicantId(applicantId)
                .withMandatory("Y")
                .withAdhoc(evidenceItem.getAdhoc())
                .withOtherText(evidenceItem.getOtherText());
    }

    public List<ApiAssessmentChildWeighting> childWeightingsBuilder(Collection<ChildWeightingDTO> childWeightings) {
        List<ApiAssessmentChildWeighting> childWeightingList = new ArrayList<>();
        if (childWeightings != null) {
            for (ChildWeightingDTO childWeightingDTO : childWeightings) {
                childWeightingList.add(new ApiAssessmentChildWeighting()
                                               .withChildWeightingId(
                                                       NumberUtils.toInteger(childWeightingDTO.getWeightingId()))
                                               .withNoOfChildren(childWeightingDTO.getNoOfChildren())
                );
            }
        }
        return childWeightingList;
    }

    private ApiCrownCourtOverview crownCourtOverviewBuilder(CrownCourtOverviewDTO crownCourtOverviewDTO) {
        return new ApiCrownCourtOverview()
                .withAvailable(crownCourtOverviewDTO.getAvailable())
                .withCrownCourtSummary(crownCourtSummaryBuilder(crownCourtOverviewDTO.getCrownCourtSummaryDTO()))
                ;
    }

    private ApiCrownCourtSummary crownCourtSummaryBuilder(CrownCourtSummaryDTO crownCourtSummaryDTO) {
        if (crownCourtSummaryDTO.getCcRepId() != null) {
            return new ApiCrownCourtSummary()
                    .withRepOrderDecision(crownCourtSummaryDTO.getRepOrderDecision().getValue())
                    ;
        } else {
            return null;
        }
    }

    private ApiIncomeEvidenceSummary incomeEvidenceSummaryBuilder(IncomeEvidenceSummaryDTO evidenceSummaryDTO) {
        if (evidenceSummaryDTO != null) {
            return new ApiIncomeEvidenceSummary()
                    .withIncomeEvidenceNotes(evidenceSummaryDTO.getIncomeEvidenceNotes())
                    .withUpliftAppliedDate(toLocalDateTime(evidenceSummaryDTO.getUpliftAppliedDate()))
                    .withEvidenceDueDate(toLocalDateTime(evidenceSummaryDTO.getEvidenceDueDate()))
                    .withUpliftRemovedDate(toLocalDateTime(evidenceSummaryDTO.getUpliftRemovedDate()));
        } else {
            return null;
        }
    }

    private boolean isPartnerContraryInterest(ApplicationDTO application) {
        return application.getPartnerContraryInterestDTO() != null &&
                !StringUtils.equals(ContraryInterestDTO.NO_CONTRARY_INTEREST,
                                    application.getPartnerContraryInterestDTO().getCode()
                );
    }

    List<ApiAssessmentSectionSummary> sectionSummariesBuilder(
            Collection<AssessmentSectionSummaryDTO> sectionSummaries) {
        List<ApiAssessmentSectionSummary> apiAssessmentSectionSummaries = new ArrayList<>();
        if (sectionSummaries != null) {
            for (AssessmentSectionSummaryDTO sectionSummaryDTO : sectionSummaries) {
                if (sectionSummaryDTO == null) {
                    continue;
                }

                // Use default null values if any of the numeric fields are null.
                BigDecimal annualTotal = sectionSummaryDTO.getAnnualTotal() != null ?
                        BigDecimal.valueOf(sectionSummaryDTO.getAnnualTotal()) : null;
                BigDecimal applicantAnnualTotal = sectionSummaryDTO.getApplicantAnnualTotal() != null ?
                        BigDecimal.valueOf(sectionSummaryDTO.getApplicantAnnualTotal()) : null;
                BigDecimal partnerAnnualTotal = sectionSummaryDTO.getPartnerAnnualTotal() != null ?
                        BigDecimal.valueOf(sectionSummaryDTO.getPartnerAnnualTotal()) : null;

                apiAssessmentSectionSummaries.add(
                        new ApiAssessmentSectionSummary()
                                .withAnnualTotal(annualTotal)
                                .withAssessmentDetails(
                                        assessmentDetailsBuilder(sectionSummaryDTO.getAssessmentDetail()))
                                .withSection(sectionSummaryDTO.getSection())
                                .withApplicantAnnualTotal(applicantAnnualTotal)
                                .withPartnerAnnualTotal(partnerAnnualTotal)
                );
            }
        }
        return apiAssessmentSectionSummaries;
    }

    public List<ApiAssessmentDetail> assessmentDetailsBuilder(Collection<AssessmentDetailDTO> assessmentDetails) {
        List<ApiAssessmentDetail> apiAssessmentDetails = new ArrayList<>();
        if (assessmentDetails != null) {
            for (AssessmentDetailDTO assessmentDetailDTO : assessmentDetails) {
                apiAssessmentDetails.add(
                        new ApiAssessmentDetail()
                                .withId(NumberUtils.toInteger(assessmentDetailDTO.getId()))
                                .withApplicantAmount(BigDecimal.valueOf(assessmentDetailDTO.getApplicantAmount()))
                                .withApplicantFrequency(
                                        Frequency.getFrom(assessmentDetailDTO.getApplicantFrequency().getCode()))
                                .withCriteriaDetailId(NumberUtils.toInteger(assessmentDetailDTO.getCriteriaDetailsId()))
                                .withPartnerAmount(BigDecimal.valueOf(assessmentDetailDTO.getPartnerAmount()))
                                .withPartnerFrequency(
                                        Frequency.getFrom(assessmentDetailDTO.getPartnerFrequency().getCode()))
                );
            }
        }
        return apiAssessmentDetails;
    }

    public FinancialAssessmentDTO getMeansAssessmentResponseToFinancialAssessmentDto(
            ApiGetMeansAssessmentResponse apiResponse, int applicantId) {
        return FinancialAssessmentDTO.builder()
                .id(ofNullable(apiResponse.getId()).orElse(0))
                .criteriaId(ofNullable(apiResponse.getCriteriaId()).map(Integer::longValue).orElse(0L))
                .usn(ofNullable(apiResponse.getUsn()).map(Integer::longValue).orElse(0L))
                .fullAvailable(apiResponse.getFullAvailable())
                .full(fullAssessmentToDto(apiResponse.getFullAssessment()))
                .initial(initialAssessmentToDTO(apiResponse.getInitialAssessment()))
                .incomeEvidence(incomeEvidenceSummaryToDto(apiResponse.getIncomeEvidenceSummary(), applicantId))
                .build();

    }

    public UserActionDTO getUserActionDto(WorkflowRequest request, Action action) {
        ApplicationDTO application = request.getApplicationDTO();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessment = financialAssessmentDTO.getInitial();
        AssessmentType assessmentType = Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())
            ? AssessmentType.FULL : AssessmentType.INIT;
        NewWorkReason newWorkReason = assessmentType == AssessmentType.INIT ?
            NewWorkReason.getFrom(initialAssessment.getNewWorkReason().getCode()) : null;

        return userMapper.getUserActionDTO(request, action, newWorkReason);
    }

    private IncomeEvidenceSummaryDTO incomeEvidenceSummaryToDto(ApiIncomeEvidenceSummary incomeEvidenceSummary,
                                                                int applicantId) {
        IncomeEvidenceSummaryDTO incomeEvidenceSummaryDTO = IncomeEvidenceSummaryDTO.builder()
                .evidenceDueDate(toDate(incomeEvidenceSummary.getEvidenceDueDate()))
                .evidenceReceivedDate(toDate(incomeEvidenceSummary.getEvidenceReceivedDate()))
                .incomeEvidenceNotes(incomeEvidenceSummary.getIncomeEvidenceNotes())
                .firstReminderDate(toDate(incomeEvidenceSummary.getFirstReminderDate()))
                .secondReminderDate(toDate(incomeEvidenceSummary.getSecondReminderDate()))
                .upliftAppliedDate(toDate(incomeEvidenceSummary.getUpliftAppliedDate()))
                .upliftRemovedDate(toDate(incomeEvidenceSummary.getUpliftRemovedDate()))
                .enabled(Boolean.FALSE)
                .build();

        List<ExtraEvidenceDTO> extraEvidenceList = new ArrayList<>();
        List<EvidenceDTO> applicantEvidence = new ArrayList<>();
        List<EvidenceDTO> partnerEvidence = new ArrayList<>();
        for (ApiIncomeEvidence apiIncomeEvidence : incomeEvidenceSummary.getIncomeEvidence()) {
            if (StringUtils.isNotEmpty(apiIncomeEvidence.getAdhoc())) {
                ExtraEvidenceDTO extraEvidenceDTO = getExtraEvidenceDTO(apiIncomeEvidence);
                extraEvidenceList.add(extraEvidenceDTO);
            } else {
                EvidenceDTO evidenceDTO = getEvidenceDTO(apiIncomeEvidence);
                if (applicantId == apiIncomeEvidence.getApplicantId()) {
                    applicantEvidence.add(evidenceDTO);
                } else {
                    partnerEvidence.add(evidenceDTO);
                }
            }
        }
        incomeEvidenceSummaryDTO.setExtraEvidenceList(extraEvidenceList);
        incomeEvidenceSummaryDTO.setApplicantIncomeEvidenceList(applicantEvidence);
        incomeEvidenceSummaryDTO.setPartnerIncomeEvidenceList(partnerEvidence);

        return incomeEvidenceSummaryDTO;
    }

    public EvidenceDTO getEvidenceDTO(ApiIncomeEvidence apiIncomeEvidence) {
        EvidenceDTO evidenceDTO = new EvidenceDTO();
        evidenceDTO.setEvidenceTypeDTO(getEvidenceTypeDTO(apiIncomeEvidence.getIncomeEvidence()));
        evidenceDTO.setId(ofNullable(apiIncomeEvidence.getId()).map(Integer::longValue).orElse(0L));
        evidenceDTO.setOtherDescription(apiIncomeEvidence.getOtherText());
        evidenceDTO.setDateReceived(toDate(apiIncomeEvidence.getDateReceived()));
        evidenceDTO.setTimestamp(toZonedDateTime(apiIncomeEvidence.getDateModified()));
        return evidenceDTO;
    }

    private ExtraEvidenceDTO getExtraEvidenceDTO(ApiIncomeEvidence apiIncomeEvidence) {
        ExtraEvidenceDTO extraEvidenceDTO = new ExtraEvidenceDTO();
        extraEvidenceDTO.setAdhoc(apiIncomeEvidence.getAdhoc());
        extraEvidenceDTO.setId(ofNullable(apiIncomeEvidence.getId()).map(Integer::longValue).orElse(0L));
        extraEvidenceDTO.setDateReceived(toDate(apiIncomeEvidence.getDateReceived()));
        extraEvidenceDTO.setEvidenceTypeDTO(getEvidenceTypeDTO(apiIncomeEvidence.getIncomeEvidence()));
        extraEvidenceDTO.setMandatory(Boolean.valueOf(apiIncomeEvidence.getMandatory()));
        extraEvidenceDTO.setOtherText(apiIncomeEvidence.getOtherText());
        extraEvidenceDTO.setTimestamp(toZonedDateTime(apiIncomeEvidence.getDateModified()));
        return extraEvidenceDTO;
    }

    private EvidenceTypeDTO getEvidenceTypeDTO(String incomeEvidence) {
        EvidenceTypeDTO evidenceTypeDTO = new EvidenceTypeDTO();
        if (StringUtils.isNotBlank(incomeEvidence)) {
            evidenceTypeDTO.setEvidence(incomeEvidence);
            evidenceTypeDTO.setDescription(IncomeEvidenceType.getFrom(incomeEvidence).getDescription());
        }
        return evidenceTypeDTO;
    }

    private InitialAssessmentDTO initialAssessmentToDTO(ApiInitialMeansAssessment apiInitialMeansAssessment) {
        return InitialAssessmentDTO.builder()
                .id(ofNullable(apiInitialMeansAssessment.getId()).map(Integer::longValue).orElse(0L))
                .adjustedIncomeValue(
                        ofNullable(apiInitialMeansAssessment.getAdjustedIncomeValue()).map(BigDecimal::doubleValue)
                                .orElse(0.0))
                .assessmentDate(toDate(apiInitialMeansAssessment.getAssessmentDate()))
                .assessmnentStatusDTO(mapAssessmentStatus(apiInitialMeansAssessment.getAssessmentStatus()))
                .childWeightings(mapChildWeightings(apiInitialMeansAssessment.getChildWeighting()))
                .lowerThreshold(ofNullable(apiInitialMeansAssessment.getLowerThreshold()).map(BigDecimal::doubleValue)
                                        .orElse(0.0))
                .newWorkReason(mapNewWorkReason(apiInitialMeansAssessment.getNewWorkReason()))
                .notes(apiInitialMeansAssessment.getNotes())
                .otherBenefitNote(apiInitialMeansAssessment.getOtherBenefitNote())
                .otherIncomeNote(apiInitialMeansAssessment.getOtherIncomeNote())
                .reviewType(mapReviewType(apiInitialMeansAssessment.getReviewType()))
                .result(apiInitialMeansAssessment.getResult())
                .resultReason(apiInitialMeansAssessment.getResultReason())
                .sectionSummaries(getSectionSummaries(apiInitialMeansAssessment.getAssessmentSectionSummary()))
                .totalAggregatedIncome(
                        ofNullable(apiInitialMeansAssessment.getTotalAggregatedIncome()).map(BigDecimal::doubleValue)
                                .orElse(0.0))
                .upperThreshold(ofNullable(apiInitialMeansAssessment.getUpperThreshold()).map(BigDecimal::doubleValue)
                                        .orElse(0.0))
                .build();
    }

    private NewWorkReasonDTO mapNewWorkReason(ApiNewWorkReason apiNewWorkReason) {
        if (apiNewWorkReason != null) {
            return NewWorkReasonDTO.builder()
                    .code(apiNewWorkReason.getCode())
                    .description(apiNewWorkReason.getDescription())
                    .type(apiNewWorkReason.getType())
                    .build();
        }
        return NewWorkReasonDTO.builder().build();
    }

    private ReviewTypeDTO mapReviewType(ApiReviewType reviewType) {
        if (reviewType != null) {
            return ReviewTypeDTO.builder()
                    .code(reviewType.getCode())
                    .description(reviewType.getDescription())
                    .build();
        }
        return ReviewTypeDTO.builder().build();
    }

    public static List<ChildWeightingDTO> mapChildWeightings(List<ApiAssessmentChildWeighting> childWeightings) {
        List<ChildWeightingDTO> childWeightingDTOList = new ArrayList<>();
        for (ApiAssessmentChildWeighting apiAssessmentChildWeighting : childWeightings) {
            ChildWeightingDTO childWeightingDTO = new ChildWeightingDTO();
            childWeightingDTO.setId(ofNullable(apiAssessmentChildWeighting.getId()).map(Integer::longValue).orElse(0L));
            childWeightingDTO.setWeightingId(
                    ofNullable(apiAssessmentChildWeighting.getChildWeightingId()).map(Integer::longValue).orElse(0L));
            childWeightingDTO.setWeightingFactor(
                    ofNullable(apiAssessmentChildWeighting.getWeightingFactor()).map(BigDecimal::doubleValue)
                            .orElse(0.0));
            childWeightingDTO.setNoOfChildren(apiAssessmentChildWeighting.getNoOfChildren());
            childWeightingDTO.setLowerAgeRange(apiAssessmentChildWeighting.getLowerAgeRange());
            childWeightingDTO.setUpperAgeRange(apiAssessmentChildWeighting.getUpperAgeRange());
            childWeightingDTOList.add(childWeightingDTO);
        }
        return childWeightingDTOList;
    }

    private FullAssessmentDTO fullAssessmentToDto(ApiFullMeansAssessment apiFullMeansAssessment) {
        return FullAssessmentDTO.builder()
                .adjustedLivingAllowance(
                        ofNullable(apiFullMeansAssessment.getAdjustedLivingAllowance()).map(BigDecimal::doubleValue)
                                .orElse(0.0))
                .assessmentDate(toDate(apiFullMeansAssessment.getAssessmentDate()))
                .assessmentNotes(apiFullMeansAssessment.getAssessmentNotes())
                .assessmnentStatusDTO(mapAssessmentStatus(apiFullMeansAssessment.getAssessmentStatus()))
                .criteriaId(ofNullable(apiFullMeansAssessment.getCriteriaId()).map(Integer::longValue).orElse(0L))
                .otherHousingNote(apiFullMeansAssessment.getOtherHousingNote())
                .result(ofNullable(apiFullMeansAssessment.getResult()).map(String::toString).orElse(""))
                .resultReason(ofNullable(apiFullMeansAssessment.getResultReason()).map(String::toString).orElse(""))
                .sectionSummaries(getSectionSummaries(apiFullMeansAssessment.getAssessmentSectionSummary()))
                .threshold(ofNullable(apiFullMeansAssessment.getThreshold()).map(BigDecimal::doubleValue).orElse(0.0))
                .totalAggregatedExpense(
                        ofNullable(apiFullMeansAssessment.getTotalAggregatedExpense()).map(BigDecimal::doubleValue)
                                .orElse(0.0))
                .totalAnnualDisposableIncome(
                        ofNullable(apiFullMeansAssessment.getTotalAnnualDisposableIncome()).map(BigDecimal::doubleValue)
                                .orElse(0.0))
                .build();
    }

    private AssessmentStatusDTO mapAssessmentStatus(ApiAssessmentStatus apiAssessmentStatus) {
        if (apiAssessmentStatus != null) {
            return AssessmentStatusDTO.builder()
                    .status(apiAssessmentStatus.getStatus())
                    .description(apiAssessmentStatus.getDescription())
                    .build();
        }
        return AssessmentStatusDTO.builder().build();
    }

    private static List<AssessmentSectionSummaryDTO> getSectionSummaries(
            List<ApiAssessmentSectionSummary> assessmentSectionSummary) {
        List<AssessmentSectionSummaryDTO> sectionSummaryDTOS = new ArrayList<>();
        for (ApiAssessmentSectionSummary apiAssessmentSectionSummary : assessmentSectionSummary) {
            AssessmentSectionSummaryDTO assessmentSectionSummaryDTO = new AssessmentSectionSummaryDTO();
            assessmentSectionSummaryDTO.setAssessmentDetail(
                    getSectionDetail(apiAssessmentSectionSummary.getAssessmentDetails()));
            assessmentSectionSummaryDTO.setSection(apiAssessmentSectionSummary.getSection());
            assessmentSectionSummaryDTO.setAnnualTotal(
                    ofNullable(apiAssessmentSectionSummary.getAnnualTotal()).map(BigDecimal::doubleValue).orElse(0.0));
            assessmentSectionSummaryDTO.setApplicantAnnualTotal(
                    ofNullable(apiAssessmentSectionSummary.getApplicantAnnualTotal()).map(BigDecimal::doubleValue)
                            .orElse(0.0));
            assessmentSectionSummaryDTO.setPartnerAnnualTotal(
                    ofNullable(apiAssessmentSectionSummary.getPartnerAnnualTotal()).map(BigDecimal::doubleValue)
                            .orElse(0.0));
            sectionSummaryDTOS.add(assessmentSectionSummaryDTO);
        }
        return sectionSummaryDTOS;
    }

    private static List<AssessmentDetailDTO> getSectionDetail(List<ApiAssessmentDetail> assessmentDetailList) {
        List<AssessmentDetailDTO> assessmentDetailDTOS = new ArrayList<>();
        for (ApiAssessmentDetail apiAssessmentDetail : assessmentDetailList) {
            AssessmentDetailDTO assessmentDetailDTO = new AssessmentDetailDTO();
            assessmentDetailDTO.setDetailCode(apiAssessmentDetail.getAssessmentDetailCode());
            assessmentDetailDTO.setDescription(apiAssessmentDetail.getAssessmentDescription());
            assessmentDetailDTO.setPartnerAmount(
                    ofNullable(apiAssessmentDetail.getPartnerAmount()).map(BigDecimal::doubleValue).orElse(0.0));
            assessmentDetailDTO.setApplicantAmount(
                    ofNullable(apiAssessmentDetail.getApplicantAmount()).map(BigDecimal::doubleValue).orElse(0.0));
            assessmentDetailDTO.setCriteriaDetailsId(
                    ofNullable(apiAssessmentDetail.getCriteriaDetailId()).map(Integer::longValue).orElse(0L));
            assessmentDetailDTO.setId(ofNullable(apiAssessmentDetail.getId()).map(Integer::longValue).orElse(0L));
            assessmentDetailDTO.setTimestamp(toZonedDateTime(apiAssessmentDetail.getDateModified()));
            assessmentDetailDTO.setApplicantFrequency(getFrequency(apiAssessmentDetail.getApplicantFrequency()));
            assessmentDetailDTO.setPartnerFrequency(getFrequency(apiAssessmentDetail.getPartnerFrequency()));
            assessmentDetailDTOS.add(assessmentDetailDTO);
        }
        return assessmentDetailDTOS;
    }

    private static FrequenciesDTO getFrequency(Frequency frequency) {
        FrequenciesDTO frequenciesDTO = new FrequenciesDTO();
        if (frequency != null) {
            frequenciesDTO.setDescription(frequency.getDescription());
            frequenciesDTO.setAnnualWeighting((long) frequency.getWeighting());
            frequenciesDTO.setCode(frequency.getCode());
        }
        return frequenciesDTO;
    }

    public void meansAssessmentResponseToApplicationDto(final ApiMeansAssessmentResponse apiResponse,
                                                        ApplicationDTO applicationDTO) {
        applicationDTO.setRepId(
                ofNullable(apiResponse.getRepId()).map(Integer::longValue).orElse(applicationDTO.getRepId()));
        applicationDTO.setPassportedDTO(PassportedDTO.builder().build());
        if (apiResponse.getApplicationTimestamp() != null) {
            applicationDTO.setTimestamp(toZonedDateTime(apiResponse.getApplicationTimestamp()));
        }
        FinancialAssessmentDTO financialAssessmentDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO();
        financialAssessmentDTO.setId(ofNullable(apiResponse.getAssessmentId()).orElse(0));
        financialAssessmentDTO.setTimestamp(toZonedDateTime(apiResponse.getUpdated()));
        financialAssessmentDTO.setDateCompleted(apiResponse.getDateCompleted());

        if (Boolean.TRUE.equals(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getFullAvailable())) {
            mapFullAssessmentDTO(financialAssessmentDTO.getFull(), apiResponse);
        } else {
            mapInitialAssessmentDTO(financialAssessmentDTO.getInitial(), apiResponse);
            financialAssessmentDTO.setFullAvailable(apiResponse.getFullAssessmentAvailable());

            // Temp fix, wipe the income evidence before progressing until we can populate from means assessment response
            IncomeEvidenceSummaryDTO incomeEvidence =
                    applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();
            incomeEvidence.getExtraEvidenceList().clear();
            incomeEvidence.getApplicantIncomeEvidenceList().clear();
            incomeEvidence.getPartnerIncomeEvidenceList().clear();
        }
    }

    private void mapFullAssessmentDTO(FullAssessmentDTO fullAssessmentDTO, ApiMeansAssessmentResponse apiResponse) {
        fullAssessmentDTO.setResult(ofNullable(apiResponse.getFullResult()).map(String::toString).orElse(""));
        fullAssessmentDTO.setAdjustedLivingAllowance(
                ofNullable(apiResponse.getAdjustedLivingAllowance()).map(BigDecimal::doubleValue).orElse(0.0));
        fullAssessmentDTO.setResultReason(
                ofNullable(apiResponse.getFullResultReason()).map(String::toString).orElse(""));
        fullAssessmentDTO.setTotalAggregatedExpense(
                ofNullable(apiResponse.getTotalAggregatedExpense()).map(BigDecimal::doubleValue).orElse(0.0));
        fullAssessmentDTO.setTotalAnnualDisposableIncome(
                ofNullable(apiResponse.getTotalAnnualDisposableIncome()).map(BigDecimal::doubleValue).orElse(0.0));
        fullAssessmentDTO.setThreshold(
                ofNullable(apiResponse.getFullThreshold()).map(BigDecimal::doubleValue).orElse(0.0));
        mapSectionSummaries(fullAssessmentDTO.getSectionSummaries(), apiResponse.getAssessmentSectionSummary());

    }

    private void mapInitialAssessmentDTO(InitialAssessmentDTO initialAssessmentDTO,
                                         ApiMeansAssessmentResponse apiResponse) {
        initialAssessmentDTO.setLowerThreshold(
                ofNullable(apiResponse.getLowerThreshold()).map(BigDecimal::doubleValue).orElse(0.0));
        initialAssessmentDTO.setUpperThreshold(
                ofNullable(apiResponse.getUpperThreshold()).map(BigDecimal::doubleValue).orElse(0.0));
        initialAssessmentDTO.setTotalAggregatedIncome(
                ofNullable(apiResponse.getTotalAggregatedIncome()).map(BigDecimal::doubleValue).orElse(0.0));
        initialAssessmentDTO.setAdjustedIncomeValue(
                ofNullable(apiResponse.getAdjustedIncomeValue()).map(BigDecimal::doubleValue).orElse(0.0));
        initialAssessmentDTO.setResult(ofNullable(apiResponse.getInitResult()).map(String::toString).orElse(""));
        initialAssessmentDTO.setResultReason(
                ofNullable(apiResponse.getInitResultReason()).map(String::toString).orElse(""));
        initialAssessmentDTO.setCriteriaId(ofNullable(apiResponse.getCriteriaId()).map(Integer::longValue).orElse(0L));
        initialAssessmentDTO.setId(ofNullable(apiResponse.getAssessmentId()).map(Integer::longValue).orElse(0L));
        mapReviewType(initialAssessmentDTO.getReviewType(), apiResponse.getReviewType());
        mapSectionSummaries(initialAssessmentDTO.getSectionSummaries(), apiResponse.getAssessmentSectionSummary());
        updateChildWeightingsId(initialAssessmentDTO.getChildWeightings(), apiResponse.getChildWeightings());
    }

    private void updateChildWeightingsId(Collection<ChildWeightingDTO> childWeightingDTOCollection,
                                         List<ApiAssessmentChildWeighting> apiAssessmentChildWeightingList) {
        for (ApiAssessmentChildWeighting responseChildWeighting : apiAssessmentChildWeightingList) {
            for (ChildWeightingDTO childWeightingDTO : childWeightingDTOCollection) {
                if (responseChildWeighting.getChildWeightingId()
                        .equals(childWeightingDTO.getWeightingId().intValue())) {
                    childWeightingDTO.setId(
                            ofNullable(responseChildWeighting.getId()).map(Integer::longValue).orElse(0L));
                }
            }
        }
    }

    private void mapReviewType(ReviewTypeDTO reviewTypeDTO, ReviewType reviewType) {
        if (reviewType != null) {
            reviewTypeDTO.setCode(reviewType.getCode());
            reviewTypeDTO.setDescription(reviewType.getDescription());
        }
    }

    private void mapSectionSummaries(Collection<AssessmentSectionSummaryDTO> sectionSummaries,
                                     List<ApiAssessmentSectionSummary> assessmentSectionSummaries) {
        if (assessmentSectionSummaries != null) {
            for (ApiAssessmentSectionSummary assessmentSectionSummary : assessmentSectionSummaries) {
                for (AssessmentSectionSummaryDTO assessmentSectionSummaryDTO : sectionSummaries) {
                    if (assessmentSectionSummary.getSection().equals(assessmentSectionSummaryDTO.getSection())) {
                        assessmentSectionSummaryDTO.setAnnualTotal(
                                ofNullable(assessmentSectionSummary.getAnnualTotal()).map(BigDecimal::doubleValue)
                                        .orElse(0.0));
                        assessmentSectionSummaryDTO.setApplicantAnnualTotal(
                                ofNullable(assessmentSectionSummary.getApplicantAnnualTotal()).map(
                                        BigDecimal::doubleValue).orElse(0.0));
                        assessmentSectionSummaryDTO.setPartnerAnnualTotal(
                                ofNullable(assessmentSectionSummary.getPartnerAnnualTotal()).map(
                                        BigDecimal::doubleValue).orElse(0.0));
                        mapAssessmentDetail(assessmentSectionSummaryDTO.getAssessmentDetail(),
                                            assessmentSectionSummary.getAssessmentDetails()
                        );
                    }
                }
            }
        }
    }

    private void mapAssessmentDetail(Collection<AssessmentDetailDTO> assessmentDetail,
                                     List<ApiAssessmentDetail> assessmentDetailList) {
        if (assessmentDetailList != null) {
            for (ApiAssessmentDetail apiAssessmentDetail : assessmentDetailList) {
                for (AssessmentDetailDTO assessmentDetailDTO : assessmentDetail) {
                    if (apiAssessmentDetail.getCriteriaDetailId()
                            .equals(assessmentDetailDTO.getCriteriaDetailsId().intValue())) {
                        assessmentDetailDTO.setId(
                                ofNullable(apiAssessmentDetail.getId()).map(Integer::longValue).orElse(0L));
                    }
                }
            }
        }
    }

    public void apiRollbackMeansAssessmentResponseToApplicationDto(ApiRollbackMeansAssessmentResponse response,
                                                                   ApplicationDTO applicationDTO) {
        if (AssessmentType.INIT.getType().equals(response.getAssessmentType())) {
            InitialAssessmentDTO initialAssessmentDTO =
                    applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getInitial();
            initialAssessmentDTO.setResult(response.getInitResult());
            initialAssessmentDTO.setAssessmnentStatusDTO(mapAssessmentStatus(response.getFassInitStatus()));
        } else if (AssessmentType.FULL.getType().equals(response.getAssessmentType())) {
            FullAssessmentDTO fullAssessmentDTO =
                    applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getFull();
            fullAssessmentDTO.setResult(response.getFullResult());
            fullAssessmentDTO.setAssessmnentStatusDTO(mapAssessmentStatus(response.getFassFullStatus()));
        }
    }

    private AssessmentStatusDTO mapAssessmentStatus(CurrentStatus apiAssessmentStatus) {
        if (apiAssessmentStatus != null) {
            return AssessmentStatusDTO.builder()
                    .status(apiAssessmentStatus.getStatus())
                    .description(apiAssessmentStatus.getDescription())
                    .build();
        }
        return AssessmentStatusDTO.builder().build();
    }
}
