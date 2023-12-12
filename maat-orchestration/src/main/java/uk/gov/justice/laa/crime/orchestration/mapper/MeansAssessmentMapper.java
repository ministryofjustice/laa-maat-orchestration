package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOverview;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.*;
import uk.gov.justice.laa.crime.orchestration.util.NumberUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static uk.gov.justice.laa.crime.orchestration.util.DateUtil.toLocalDateTime;

@Component
@RequiredArgsConstructor
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
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withUserSession(userMapper.userDtoToUserSession(request.getUserDTO()))
                .withFinancialAssessmentId(BigDecimal.valueOf(financialAssessmentDTO.getId()))
                .withFullAssessmentDate(toLocalDateTime(fullAssessmentDTO.getAssessmentDate()))
                .withOtherHousingNote(fullAssessmentDTO.getOtherHousingNote())
                .withInitTotalAggregatedIncome(BigDecimal.valueOf(initialAssessmentDTO.getTotalAggregatedIncome()))
                .withFullAssessmentNotes(fullAssessmentDTO.getAssessmentNotes());
    }

    private List<ApiAssessmentChildWeighting> childWeightingsBuilder(Collection<ChildWeightingDTO> childWeightings) {
        List<ApiAssessmentChildWeighting> childWeightingList = new ArrayList<>();
        if (childWeightings != null) {
            for (ChildWeightingDTO childWeightingDTO : childWeightings) {
                childWeightingList.add(new ApiAssessmentChildWeighting()
                        .withChildWeightingId(NumberUtils.toInteger(childWeightingDTO.getWeightingId()))
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
                    .withUpliftRemovedDate(toLocalDateTime(evidenceSummaryDTO.getUpliftRemovedDate()))
                    ;
        } else {
            return null;
        }
    }

    private boolean isPartnerContraryInterest(ApplicationDTO application) {
        return application.getPartnerContraryInterestDTO() != null &&
                !StringUtils.equals(ContraryInterestDTO.NO_CONTRARY_INTEREST, application.getPartnerContraryInterestDTO().getCode());
    }

    private List<ApiAssessmentSectionSummary> sectionSummariesBuilder(Collection<AssessmentSectionSummaryDTO> sectionSummaries) {
        List<ApiAssessmentSectionSummary> apiAssessmentSectionSummaries = new ArrayList<>();
        if (sectionSummaries != null) {
            for (AssessmentSectionSummaryDTO sectionSummaryDTO : sectionSummaries) {
                apiAssessmentSectionSummaries.add(
                        new ApiAssessmentSectionSummary()
                                .withAnnualTotal(BigDecimal.valueOf(sectionSummaryDTO.getAnnualTotal()))
                                .withAssessmentDetails(assessmentDetailsBuilder(sectionSummaryDTO.getAssessmentDetail()))
                                .withSection(sectionSummaryDTO.getSection())
                                .withApplicantAnnualTotal(BigDecimal.valueOf(sectionSummaryDTO.getApplicantAnnualTotal()))
                                .withPartnerAnnualTotal(BigDecimal.valueOf(sectionSummaryDTO.getPartnerAnnualTotal()))
                );
            }
        }
        return apiAssessmentSectionSummaries;
    }

    private List<ApiAssessmentDetail> assessmentDetailsBuilder(Collection<AssessmentDetailDTO> assessmentDetails) {
        List<ApiAssessmentDetail> apiAssessmentDetails = new ArrayList<>();
        if (assessmentDetails != null) {
            for (AssessmentDetailDTO assessmentDetailDTO : assessmentDetails) {
                apiAssessmentDetails.add(
                        new ApiAssessmentDetail()
                                .withId(NumberUtils.toInteger(assessmentDetailDTO.getId()))
                                .withApplicantAmount(BigDecimal.valueOf(assessmentDetailDTO.getApplicantAmount()))
                                .withApplicantFrequency(Frequency.getFrom(assessmentDetailDTO.getApplicantFrequency().getCode()))
                                .withCriteriaDetailId(NumberUtils.toInteger(assessmentDetailDTO.getCriteriaDetailsId()))
                                .withPartnerAmount(BigDecimal.valueOf(assessmentDetailDTO.getPartnerAmount()))
                                .withPartnerFrequency(Frequency.getFrom(assessmentDetailDTO.getPartnerFrequency().getCode()))
                );
            }
        }
        return apiAssessmentDetails;
    }

    public FinancialAssessmentDTO getMeansAssessmentResponseToFinancialAssessmentDto(ApiGetMeansAssessmentResponse apiResponse) {
        return new FinancialAssessmentDTO();
    }

    public void meansAssessmentResponseToApplicationDto(final ApiMeansAssessmentResponse apiResponse, ApplicationDTO applicationDTO) {
        applicationDTO.setRepId(ofNullable(apiResponse.getRepId()).map(Integer::longValue).orElse(applicationDTO.getRepId()));
        applicationDTO.setPassportedDTO(new PassportedDTO());
        if (apiResponse.getApplicationTimestamp() != null) {
            applicationDTO.setTimestamp(Timestamp.valueOf(apiResponse.getApplicationTimestamp()));
        }
        FinancialAssessmentDTO financialAssessmentDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO();
        financialAssessmentDTO.setId(ofNullable(apiResponse.getAssessmentId()).map(Integer::longValue).orElse(0L));
        financialAssessmentDTO.setTimestamp(Timestamp.valueOf(apiResponse.getUpdated()));

        if (Boolean.TRUE.equals(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getFullAvailable())) {
            mapFullAssessmentDTO(financialAssessmentDTO.getFull(), apiResponse);
        } else {
            mapInitialAssessmentDTO(financialAssessmentDTO.getInitial(), apiResponse);
            financialAssessmentDTO.setFullAvailable(apiResponse.getFullAssessmentAvailable());
        }
    }

    private void mapFullAssessmentDTO(FullAssessmentDTO fullAssessmentDTO, ApiMeansAssessmentResponse apiResponse) {
        fullAssessmentDTO.setResult(ofNullable(apiResponse.getFullResult()).map(String::toString).orElse(""));
        fullAssessmentDTO.setAdjustedLivingAllowance(ofNullable(apiResponse.getAdjustedLivingAllowance()).map(BigDecimal::doubleValue).orElse(0.0));
        fullAssessmentDTO.setResultReason(ofNullable(apiResponse.getFullResultReason()).map(String::toString).orElse(""));
        fullAssessmentDTO.setTotalAggregatedExpense(ofNullable(apiResponse.getTotalAggregatedExpense()).map(BigDecimal::doubleValue).orElse(0.0));
        fullAssessmentDTO.setTotalAnnualDisposableIncome(ofNullable(apiResponse.getTotalAnnualDisposableIncome()).map(BigDecimal::doubleValue).orElse(0.0));
        fullAssessmentDTO.setThreshold(ofNullable(apiResponse.getFullThreshold()).map(BigDecimal::doubleValue).orElse(0.0));
        mapSectionSummaries(fullAssessmentDTO.getSectionSummaries(), apiResponse.getAssessmentSectionSummary());
    }

    private void mapInitialAssessmentDTO(InitialAssessmentDTO initialAssessmentDTO, ApiMeansAssessmentResponse apiResponse) {
        initialAssessmentDTO.setLowerThreshold(ofNullable(apiResponse.getLowerThreshold()).map(BigDecimal::doubleValue).orElse(0.0));
        initialAssessmentDTO.setUpperThreshold(ofNullable(apiResponse.getUpperThreshold()).map(BigDecimal::doubleValue).orElse(0.0));
        initialAssessmentDTO.setTotalAggregatedIncome(ofNullable(apiResponse.getTotalAggregatedIncome()).map(BigDecimal::doubleValue).orElse(0.0));
        initialAssessmentDTO.setAdjustedIncomeValue(ofNullable(apiResponse.getAdjustedIncomeValue()).map(BigDecimal::doubleValue).orElse(0.0));
        initialAssessmentDTO.setResult(ofNullable(apiResponse.getInitResult()).map(String::toString).orElse(""));
        initialAssessmentDTO.setResultReason(ofNullable(apiResponse.getInitResultReason()).map(String::toString).orElse(""));
        initialAssessmentDTO.setCriteriaId(ofNullable(apiResponse.getCriteriaId()).map(Integer::longValue).orElse(0L));
        initialAssessmentDTO.setId(ofNullable(apiResponse.getAssessmentId()).map(Integer::longValue).orElse(0L));
        mapReviewType(initialAssessmentDTO.getReviewType(), apiResponse.getReviewType());
        mapSectionSummaries(initialAssessmentDTO.getSectionSummaries(), apiResponse.getAssessmentSectionSummary());
        updateChildWeightingsId(initialAssessmentDTO.getChildWeightings(), apiResponse.getChildWeightings());
    }

    private void updateChildWeightingsId(Collection<ChildWeightingDTO> childWeightingDTOCollection, List<ApiAssessmentChildWeighting> apiAssessmentChildWeightingList) {
        for (ApiAssessmentChildWeighting responseChildWeighting : apiAssessmentChildWeightingList) {
            for (ChildWeightingDTO childWeightingDTO : childWeightingDTOCollection) {
                if (responseChildWeighting.getChildWeightingId().equals(childWeightingDTO.getWeightingId().intValue())) {
                    childWeightingDTO.setId(ofNullable(responseChildWeighting.getId()).map(Integer::longValue).orElse(0L));
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

    private void mapSectionSummaries(Collection<AssessmentSectionSummaryDTO> sectionSummaries, List<ApiAssessmentSectionSummary> assessmentSectionSummaries) {
        if (assessmentSectionSummaries != null) {
            for (ApiAssessmentSectionSummary assessmentSectionSummary : assessmentSectionSummaries) {
                for (AssessmentSectionSummaryDTO assessmentSectionSummaryDTO : sectionSummaries) {
                    if (assessmentSectionSummary.getSection().equals(assessmentSectionSummaryDTO.getSection())) {
                        assessmentSectionSummaryDTO.setAnnualTotal(ofNullable(assessmentSectionSummary.getAnnualTotal()).map(BigDecimal::doubleValue).orElse(0.0));
                        assessmentSectionSummaryDTO.setApplicantAnnualTotal(ofNullable(assessmentSectionSummary.getApplicantAnnualTotal()).map(BigDecimal::doubleValue).orElse(0.0));
                        assessmentSectionSummaryDTO.setPartnerAnnualTotal(ofNullable(assessmentSectionSummary.getPartnerAnnualTotal()).map(BigDecimal::doubleValue).orElse(0.0));
                        mapAssessmentDetail(assessmentSectionSummaryDTO.getAssessmentDetail(), assessmentSectionSummary.getAssessmentDetails());
                    }
                }
            }
        }
    }

    private void mapAssessmentDetail(Collection<AssessmentDetailDTO> assessmentDetail, List<ApiAssessmentDetail> assessmentDetailList) {
        if (assessmentDetailList != null) {
            for (ApiAssessmentDetail apiAssessmentDetail : assessmentDetailList) {
                for (AssessmentDetailDTO assessmentDetailDTO : assessmentDetail) {
                    if (apiAssessmentDetail.getCriteriaDetailId().equals(assessmentDetailDTO.getCriteriaDetailsId().intValue())) {
                        assessmentDetailDTO.setId(ofNullable(apiAssessmentDetail.getId()).map(Integer::longValue).orElse(0L));
                    }
                }
            }
        }
    }
}
