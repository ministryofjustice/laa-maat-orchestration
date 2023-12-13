package uk.gov.justice.laa.crime.orchestration.data.builder;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOverview;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipProgress;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TestModelDataBuilder {

    public static ApiFindHardshipResponse getApiFindHardshipResponse() {
        return new ApiFindHardshipResponse()
                .withId(Constants.TEST_HARDSHIP_REVIEW_ID)
                .withCmuId(Constants.TEST_CMU_ID)
                .withNotes(Constants.TEST_CASEWORKER_NOTES)
                .withDecisionNotes(Constants.TEST_CASEWORKER_DECISION_NOTES)
                .withReviewDate(Constants.TEST_DATE_REVIEWED_DATETIME)
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                .withDisposableIncomeAfterHardship(Constants.TEST_POST_HARDSHIP_DISPOSABLE_INCOME)
                .withNewWorkReason(NewWorkReason.NEW)
                .withSolicitorCosts(getSolicitorsCosts())
                .withStatus(HardshipReviewStatus.COMPLETE)
                .withReviewDetails(
                        Stream.concat(
                                getApiHardshipReviewDetails(BigDecimal.valueOf(2000.00),
                                        HardshipReviewDetailType.EXPENDITURE
                                ).stream(),
                                getApiHardshipReviewDetails(BigDecimal.valueOf(1500.00),
                                        HardshipReviewDetailType.INCOME
                                ).stream()
                        ).toList())
                .withReviewProgressItems(getReviewProgressItems());
    }

    public static ApiPerformHardshipResponse getApiPerformHardshipResponse() {
        return new ApiPerformHardshipResponse()
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                .withHardshipReviewId(Constants.TEST_HARDSHIP_REVIEW_ID)
                .withPostHardshipDisposableIncome(Constants.TEST_POST_HARDSHIP_DISPOSABLE_INCOME);
    }

    public static ApiPerformHardshipRequest getApiPerformHardshipRequest() {
        return new ApiPerformHardshipRequest()
                .withHardship(
                        new HardshipReview()
                                .withCourtType(CourtType.MAGISTRATE)
                                .withDeniedIncome(
                                        List.of(
                                                new DeniedIncome()
                                                        .withAmount(BigDecimal.valueOf(1500))
                                                        .withFrequency(Frequency.MONTHLY)
                                                        .withAccepted(true)
                                                        .withDescription(
                                                                Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                        .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                                        .withReasonNote(Constants.TEST_HARDSHIP_REASON_NOTE)
                                        )
                                )
                                .withExtraExpenditure(
                                        List.of(
                                                new ExtraExpenditure()
                                                        .withAmount(BigDecimal.valueOf(2000.00))
                                                        .withFrequency(Frequency.ANNUALLY)
                                                        .withAccepted(false)
                                                        .withReasonCode(HardshipReviewDetailReason.EVIDENCE_SUPPLIED)
                                                        .withDescription(
                                                                Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                        .withItemCode(ExtraExpenditureDetailCode.CAR_LOAN)
                                        )
                                )
                                .withReviewDate(Constants.TEST_DATE_REVIEWED_DATETIME)
                                .withSolicitorCosts(
                                        new SolicitorCosts()
                                                .withVat(Constants.TEST_SOLICITOR_VAT)
                                                .withRate(Constants.TEST_SOLICITOR_RATE)
                                                .withHours(Constants.TEST_SOLICITOR_HOURS)
                                                .withDisbursements(Constants.TEST_SOLICITOR_DISBURSEMENTS)
                                                .withEstimatedTotal(Constants.TEST_SOLICITOR_ESTIMATED_COST)
                                )
                                .withTotalAnnualDisposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                )
                .withHardshipMetadata(
                        new HardshipMetadata()
                                .withRepId(Constants.TEST_REP_ID)
                                .withCmuId(Constants.TEST_CMU_ID)
                                .withHardshipReviewId(Constants.TEST_HARDSHIP_REVIEW_ID)
                                .withFinancialAssessmentId(Constants.TEST_FINANCIAL_ASSESSMENT_ID)
                                .withReviewReason(NewWorkReason.getFrom(Constants.TEST_NEW_WORK_REASON_STRING))
                                .withReviewStatus(HardshipReviewStatus.COMPLETE)
                                .withNotes(Constants.TEST_CASEWORKER_NOTES)
                                .withDecisionNotes(Constants.TEST_CASEWORKER_DECISION_NOTES)
                                .withUserSession(
                                        getUserSession()
                                )
                                .withProgressItems(
                                        List.of(
                                                new HardshipProgress()
                                                        .withAction(HardshipReviewProgressAction.SOLICITOR_INFORMED)
                                                        .withResponse(
                                                                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                                        .withDateTaken(Constants.TEST_DATE_REQUESTED_DATETIME)
                                                        .withDateRequired(Constants.TEST_DATE_REQUIRED_DATETIME)
                                                        .withDateCompleted(Constants.TEST_DATE_COMPLETED_DATETIME)
                                        )
                                )
                );
    }

    private static ApiUserSession getUserSession() {
        return new ApiUserSession()
                .withUserName(Constants.TEST_USERNAME)
                .withSessionId(Constants.TEST_USER_SESSION);
    }

    public static ApplicationDTO getApplicationDTOWithBlankHardship(CourtType courtType) {
        ApplicationDTO applicationDTOWithHardship;
        if (courtType == CourtType.MAGISTRATE) {
            applicationDTOWithHardship = getApplicationDTOWithHardship(CourtType.MAGISTRATE);
            applicationDTOWithHardship.getAssessmentDTO()
                    .getFinancialAssessmentDTO()
                    .getHardship()
                    .setMagCourtHardship(HardshipReviewDTO.builder().build());
        } else {
            applicationDTOWithHardship = getApplicationDTOWithHardship(CourtType.CROWN_COURT);
            applicationDTOWithHardship.getAssessmentDTO()
                    .getFinancialAssessmentDTO()
                    .getHardship()
                    .setCrownCourtHardship(HardshipReviewDTO.builder().build());
        }
        return applicationDTOWithHardship;
    }

    private static List<ApiHardshipProgress> getReviewProgressItems() {
        return List.of(new ApiHardshipProgress()
                .withId(Constants.TEST_HARDSHIP_REVIEW_PROGRESS_ID)
                .withProgressResponse(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                .withDateCompleted(Constants.TEST_DATE_COMPLETED_DATETIME)
                .withDateRequested(Constants.TEST_DATE_REQUESTED_DATETIME)
                .withDateRequired(Constants.TEST_DATE_REQUIRED_DATETIME)
                .withProgressAction(HardshipReviewProgressAction.SOLICITOR_INFORMED)
        );
    }

    public static SolicitorCosts getSolicitorsCosts() {
        return new SolicitorCosts()
                .withVat(Constants.TEST_SOLICITOR_VAT)
                .withDisbursements(Constants.TEST_SOLICITOR_DISBURSEMENTS)
                .withRate(Constants.TEST_SOLICITOR_RATE)
                .withHours(Constants.TEST_SOLICITOR_HOURS)
                .withEstimatedTotal(Constants.TEST_SOLICITOR_ESTIMATED_COST);
    }

    public static List<ApiHardshipDetail> getApiHardshipReviewDetails(BigDecimal amount,
                                                                      HardshipReviewDetailType... detailTypes) {
        List<ApiHardshipDetail> details = new ArrayList<>();

        Arrays.stream(detailTypes).forEach(type -> {
            switch (type) {
                case FUNDING -> details.add(
                        new ApiHardshipDetail()
                                .withDetailType(HardshipReviewDetailType.FUNDING)
                                .withAmount(amount)
                                .withDateDue(LocalDateTime.now())
                );
                case INCOME -> details.add(
                        new ApiHardshipDetail()
                                .withId(Constants.TEST_HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.INCOME)
                                .withAmount(amount)
                                .withFrequency(Frequency.MONTHLY)
                                .withAccepted("Y")
                                .withOtherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                .withReasonNote(Constants.TEST_HARDSHIP_REASON_NOTE)
                                .withDetailCode(HardshipReviewDetailCode.MEDICAL_GROUNDS)
                );
                case EXPENDITURE -> details.add(
                        new ApiHardshipDetail()
                                .withId(Constants.TEST_HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.EXPENDITURE)
                                .withAmount(amount)
                                .withFrequency(Frequency.ANNUALLY)
                                .withAccepted("F")
                                .withDetailReason(HardshipReviewDetailReason.EVIDENCE_SUPPLIED)
                                .withOtherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                .withDetailCode(HardshipReviewDetailCode.CAR_LOAN)
                );
                case SOL_COSTS -> details.add(
                        new ApiHardshipDetail()
                                .withDetailType(HardshipReviewDetailType.SOL_COSTS)
                                .withAmount(amount)
                                .withFrequency(Frequency.ANNUALLY)
                                .withAccepted("Y")
                );
            }
        });
        return details;
    }

    public static AssessmentSummaryDTO getAssessmentSummaryDTO() {
        return AssessmentSummaryDTO.builder()
                .id(Constants.TEST_ASSESSMENT_SUMMARY_ID.longValue())
                .status(AssessmentStatusDTO.COMPLETE)
                .result(ReviewResult.PASS.getResult())
                .assessmentDate(Constants.TEST_ASSESSMENT_SUMMARY_DATE)
                .build();
    }

    public static AssessmentSummaryDTO getAssessmentSummaryDTOFromHardship(CourtType courtType) {
        AssessmentSummaryDTO assessmentSummaryDTO = getAssessmentSummaryDTO();
        if (courtType == CourtType.CROWN_COURT) {
            assessmentSummaryDTO.setType("Hardship Review - Crown Court");
        } else {
            assessmentSummaryDTO.setType("Hardship Review - Magistrate");
        }
        return assessmentSummaryDTO;
    }

    public static List<HRSectionDTO> getHrSectionDtosWithMixedTypes() {
        return Stream.concat(
                getHrSectionDtosWithExpenditureType().stream(),
                getHrSectionDtosWithDeniedIncomeType().stream()
        ).collect(Collectors.toList());
    }

    public static WorkflowRequest buildWorkflowRequestWithHardship(CourtType courtType) {
        return WorkflowRequest.builder()
                .userDTO(getUserDTO())
                .applicationDTO(getApplicationDTOWithHardship(courtType))
                .build();
    }

    public static ContributionsDTO getContributionsDTO() {
        return ContributionsDTO.builder()
                .id(Constants.TEST_CONTRIBUTIONS_ID.longValue())
                .upliftApplied(false)
                .basedOn(Constants.CONTRIBUTION_BASED_ON)
                .calcDate(Constants.TEST_CONTRIBUTION_CALCULATION_DATE)
                .effectiveDate(Constants.TEST_CONTRIBUTION_EFFECTIVE_DATE)
                .monthlyContribs(Constants.TEST_MONTHLY_CONTRIBUTION_AMOUNT)
                .upfrontContribs(Constants.TEST_UPFRONT_CONTRIBUTION_AMOUNT)
                .build();
    }

    public static ApplicationDTO getApplicationDTOWithHardship(CourtType courtType) {
        return ApplicationDTO.builder()
                .courtType(courtType)
                .repId(Constants.TEST_REP_ID.longValue())
                .caseManagementUnitDTO(getCaseManagementUnitDTO())
                .crownCourtOverviewDTO(CrownCourtOverviewDTO.builder().build())
                .assessmentDTO(
                        AssessmentDTO.builder()
                                .financialAssessmentDTO(getFinancialAssessmentDTO(courtType))
                                .build()
                ).build();
    }

    private static FinancialAssessmentDTO getFinancialAssessmentDTO(CourtType courtType) {
        return FinancialAssessmentDTO.builder()
                .id(Constants.TEST_FINANCIAL_ASSESSMENT_ID.longValue())
                .hardship(getHardshipOverviewDTO(courtType))
                .initial(getInitialAssessment())
                .build();
    }

    private static InitialAssessmentDTO getInitialAssessment() {
        return InitialAssessmentDTO.builder()
                .assessmnentStatusDTO(AssessmentStatusDTO.builder().status("COMPLETE").build())
                .result(AssessmentResult.PASS.toString())
                .build();
    }

    private static CaseManagementUnitDTO getCaseManagementUnitDTO() {
        return CaseManagementUnitDTO.builder()
                .cmuId(Constants.TEST_CMU_ID.longValue())
                .build();
    }

    public static HardshipOverviewDTO getHardshipOverviewDTO(CourtType courtType) {
        HardshipOverviewDTO.HardshipOverviewDTOBuilder<?, ?> dtoBuilder = HardshipOverviewDTO.builder();

        HardshipReviewDTO reviewDTO = getHardshipReviewDTO();

        if (courtType == CourtType.CROWN_COURT) {
            dtoBuilder.crownCourtHardship(reviewDTO);
            // Mimic Maat passing an instance with null fields
            reviewDTO.setSolictorsCosts(HRSolicitorsCostsDTO.builder().build());
        } else {
            dtoBuilder.magCourtHardship(reviewDTO);
            reviewDTO.setSolictorsCosts(getHRSolicitorsCostsDTO());
        }
        return dtoBuilder.build();
    }

    public static HardshipReviewDTO getHardshipReviewDTO() {
        return HardshipReviewDTO.builder()
                .id(Constants.TEST_HARDSHIP_REVIEW_ID.longValue())
                .cmuId(Constants.TEST_CMU_ID.longValue())
                .disposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                .reviewResult(Constants.TEST_HARDSHIP_REVIEW_RESULT)
                .disposableIncomeAfterHardship(Constants.TEST_POST_HARDSHIP_DISPOSABLE_INCOME)
                .reviewDate(Constants.TEST_DATE_REVIEWED)
                .section(TestModelDataBuilder.getHrSectionDtosWithMixedTypes())
                .asessmentStatus(getAssessmentStatusDTO())
                .newWorkReason(getNewWorkReasonDTO())
                .notes(Constants.TEST_CASEWORKER_NOTES)
                .decisionNotes(Constants.TEST_CASEWORKER_DECISION_NOTES)
                .solictorsCosts(getHRSolicitorsCostsDTO())
                .progress(getHrProgressDTOs())
                .build();
    }

    @NotNull
    private static List<HRProgressDTO> getHrProgressDTOs() {
        return List.of(
                HRProgressDTO.builder()
                        .id(Constants.TEST_HARDSHIP_REVIEW_PROGRESS_ID.longValue())
                        .progressAction(
                                HRProgressActionDTO.builder()
                                        .action(HardshipReviewProgressAction.SOLICITOR_INFORMED.getAction())
                                        .description(HardshipReviewProgressAction.SOLICITOR_INFORMED.getDescription())
                                        .build()
                        )
                        .progressResponse(
                                HRProgressResponseDTO.builder()
                                        .response(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED.getResponse())
                                        .description(
                                                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED.getDescription())
                                        .build()
                        )
                        .dateRequired(Constants.TEST_DATE_REQUIRED)
                        .dateRequested(Constants.TEST_DATE_REQUESTED)
                        .dateCompleted(Constants.TEST_DATE_COMPLETED)
                        .build()
        );
    }

    private static NewWorkReasonDTO getNewWorkReasonDTO() {
        return NewWorkReasonDTO.builder()
                .code(Constants.TEST_NEW_WORK_REASON_STRING)
                .description("New")
                .build();
    }

    private static AssessmentStatusDTO getAssessmentStatusDTO() {
        return AssessmentStatusDTO.builder()
                .status(AssessmentStatusDTO.COMPLETE)
                .description("Complete")
                .build();
    }

    public static HRSolicitorsCostsDTO getHRSolicitorsCostsDTO() {
        return HRSolicitorsCostsDTO.builder()
                .solicitorRate(Constants.TEST_SOLICITOR_RATE)
                .solicitorHours(Constants.TEST_SOLICITOR_HOURS.doubleValue())
                .solicitorDisb(Constants.TEST_SOLICITOR_DISBURSEMENTS)
                .solicitorVat(Constants.TEST_SOLICITOR_VAT)
                .solicitorEstimatedTotalCost(Constants.TEST_SOLICITOR_ESTIMATED_COST)
                .build();
    }

    private static UserDTO getUserDTO() {
        return UserDTO.builder()
                .userName(Constants.TEST_USERNAME)
                .userSession(Constants.TEST_USER_SESSION)
                .build();
    }

    public static List<HRSectionDTO> getHrSectionDtosWithExpenditureType() {
        return List.of(
                HRSectionDTO.builder()
                        .detailType(HRDetailTypeDTO.builder()
                                .type(HardshipReviewDetailType.EXPENDITURE.getType())
                                .description(HardshipReviewDetailType.EXPENDITURE.getDescription())
                                .build())
                        .detail(List.of(
                                        HRDetailDTO.builder()
                                                .id(Constants.TEST_HARDSHIP_DETAIL_ID.longValue())
                                                .detailDescription(
                                                        HRDetailDescriptionDTO.builder()
                                                                .code(HardshipReviewDetailCode.CAR_LOAN.getCode())
                                                                .description(HardshipReviewDetailCode.CAR_LOAN.getDescription())
                                                                .build())
                                                .accepted(false)
                                                .frequency(FrequenciesDTO.builder()
                                                        .code(Frequency.ANNUALLY.getCode())
                                                        .annualWeighting(
                                                                (long) Frequency.ANNUALLY.getAnnualWeighting())
                                                        .description(Frequency.ANNUALLY.getDescription())
                                                        .build())
                                                .amountNumber(BigDecimal.valueOf(2000.00))
                                                .otherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                .reason(
                                                        HRReasonDTO.builder()
                                                                .reason(HardshipReviewDetailReason.EVIDENCE_SUPPLIED.getReason())
                                                                .build())
                                                .build()
                                )
                        )
                        .build()
        );
    }

    public static List<HRSectionDTO> getHrSectionDtosWithDeniedIncomeType() {
        return List.of(
                HRSectionDTO.builder()
                        .detailType(HRDetailTypeDTO.builder()
                                .type(HardshipReviewDetailType.INCOME.getType())
                                .description(HardshipReviewDetailType.INCOME.getDescription())
                                .build())
                        .detail(List.of(
                                        HRDetailDTO.builder()
                                                .id(Constants.TEST_HARDSHIP_DETAIL_ID.longValue())
                                                .detailDescription(
                                                        HRDetailDescriptionDTO.builder()
                                                                .code(HardshipReviewDetailCode.MEDICAL_GROUNDS.getCode())
                                                                .description(
                                                                        HardshipReviewDetailCode.MEDICAL_GROUNDS.getDescription())
                                                                .build())
                                                .accepted(true)
                                                .frequency(FrequenciesDTO.builder()
                                                        .code(Frequency.MONTHLY.getCode())
                                                        .annualWeighting(
                                                                (long) Frequency.MONTHLY.getAnnualWeighting())
                                                        .description(Frequency.MONTHLY.getDescription())
                                                        .build())
                                                .amountNumber(BigDecimal.valueOf(1500.00))
                                                .hrReasonNote(Constants.TEST_HARDSHIP_REASON_NOTE)
                                                .otherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                .reason(HRReasonDTO.builder().build())
                                                .build()
                                )
                        )
                        .build()
        );
    }

    public static ApiGetMeansAssessmentResponse getApiGetMeansAssessmentResponse() {
        return new ApiGetMeansAssessmentResponse()
                .withId(Constants.TEST_FINANCIAL_ASSESSMENT_ID)
                .withCriteriaId(Constants.TEST_CRITERIA_ID)
                .withFullAvailable(true)
                .withUsn(Constants.TEST_USN)
                .withFullAssessment(getApiFullAssessment(CurrentStatus.COMPLETE));
    }

    public static ApiFullMeansAssessment getApiFullAssessment(CurrentStatus currentStatus) {
        return new ApiFullMeansAssessment()
                .withCriteriaId(Constants.TEST_CRITERIA_ID)
                .withAssessmentDate(Constants.ASSESSMENT_DATE)
                .withAssessmentNotes(Constants.TEST_ASSESSMENT_NOTES)
                .withAdjustedLivingAllowance(BigDecimal.valueOf(15600.00))
                .withOtherHousingNote(Constants.TEST_OTHER_HOUSING_NOTE)
                .withTotalAggregatedExpense(Constants.TEST_AGGREGATED_EXPENSE)
                .withTotalAnnualDisposableIncome(Constants.TEST_ANNUAL_DISPOSABLE_INCOME)
                .withThreshold(Constants.THRESHOLD)
                .withResult(AssessmentResult.PASS.toString())
                .withResultReason("FullAssessmentResult.PASS.getReason()")
                .withAssessmentStatus(new ApiAssessmentStatus()
                        .withStatus(currentStatus.getValue())
                        .withDescription(currentStatus.getDescription()));
    }

    public static ApiCreateMeansAssessmentRequest getApiCreateMeansAssessmentRequest() {
            return new ApiCreateMeansAssessmentRequest()
                    .withLaaTransactionId(Constants.TRANSACTION_ID)
                    .withAssessmentType(AssessmentType.INIT)
                    .withReviewType(ReviewType.NAFI)
                    .withRepId(Constants.TEST_REP_ID)
                    .withCmuId(Constants.TEST_CMU_ID)
                    .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                    .withNewWorkReason(NewWorkReason.NEW)
                    .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                    .withHasPartner(true)
                    .withPartnerContraryInterest(false)
                    .withCaseType(CaseType.EITHER_WAY)
                    .withAssessmentStatus(CurrentStatus.COMPLETE)
                    .withChildWeightings(getAssessmentChildWeightings())
                    .withUserSession(getUserSession())
                    .withEmploymentStatus(Constants.TEST_EMPLOYMENT_STATUS)
                    .withUsn(Constants.TEST_USN)
                    .withCrownCourtOverview(new ApiCrownCourtOverview()
                            .withAvailable(true)
                            .withCrownCourtSummary(
                                    new ApiCrownCourtSummary()
                                            .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                            )
                    )
                    .withSectionSummaries(List.of(getApiAssessmentSectionSummary()));
        }
    public static ApiIncomeEvidenceSummary getApiIncomeEvidenceSummary() {
        return new ApiIncomeEvidenceSummary()
                .withIncomeEvidenceNotes(Constants.TEST_INCOME_EVIDENCE_NOTES)
                .withEvidenceDueDate(Constants.TEST_INCOME_EVIDENCE_DUE_DATE)
                .withUpliftAppliedDate(Constants.TEST_INCOME_UPLIFT_APPLY_DATE)
                .withUpliftRemovedDate(Constants.TEST_INCOME_UPLIFT_REMOVE_DATE);
    }

    public static List<ApiAssessmentChildWeighting> getAssessmentChildWeightings() {
        return List.of(
                new ApiAssessmentChildWeighting()
                        .withId(1234)
                        .withChildWeightingId(37)
                        .withNoOfChildren(1)
                ,
                new ApiAssessmentChildWeighting()
                        .withId(2345)
                        .withChildWeightingId(38)
                        .withNoOfChildren(2)
        );
    }

    public static ApiAssessmentSectionSummary getApiAssessmentSectionSummary() {
        return new ApiAssessmentSectionSummary()
                .withApplicantAnnualTotal(Constants.TEST_APPLICANT_ANNUAL_TOTAL)
                .withAnnualTotal(Constants.TEST_APPLICANT_ANNUAL_TOTAL)
                .withPartnerAnnualTotal(BigDecimal.ZERO)
                .withSection("INITA")
                .withAssessmentDetails(
                        new ArrayList<>(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(Constants.TEST_CRITERIA_DETAIL_ID)
                                                .withApplicantAmount(Constants.TEST_APPLICANT_VALUE)
                                                .withApplicantFrequency(Constants.TEST_FREQUENCY)
                                )
                        )
                );
    }

    public static ApiUpdateMeansAssessmentRequest getApiUpdateMeansAssessmentRequest() {
        return new ApiUpdateMeansAssessmentRequest()
                .withLaaTransactionId(Constants.TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withRepId(Constants.TEST_REP_ID)
                .withCmuId(Constants.TEST_CMU_ID)
                .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withFullAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withOtherHousingNote(Constants.TEST_OTHER_HOUSING_NOTE)
                .withInitTotalAggregatedIncome(Constants.TEST_AGGREGATED_EXPENSE)
                .withFullAssessmentNotes(Constants.TEST_FULL_ASSESSMENT_NOTES)
                .withCaseType(CaseType.EITHER_WAY)
                .withEmploymentStatus(Constants.TEST_EMPLOYMENT_STATUS)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getAssessmentChildWeightings())
                .withUserSession(getUserSession())
                .withCrownCourtOverview(new ApiCrownCourtOverview()
                        .withAvailable(true)
                        .withCrownCourtSummary(
                                new ApiCrownCourtSummary()
                                        .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                        )
                )
                .withSectionSummaries(List.of(getApiAssessmentSectionSummary()));
    }
}
