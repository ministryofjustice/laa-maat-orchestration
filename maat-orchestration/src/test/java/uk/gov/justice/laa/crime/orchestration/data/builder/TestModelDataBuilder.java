package uk.gov.justice.laa.crime.orchestration.data.builder;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipProgress;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;

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
                .withId(Constants.TEST_HARDSHIP_ID)
                .withCmuId(Constants.TEST_CMU_ID)
                .withNotes("Test note.")
                .withDecisionNotes("Test decision note.")
                .withReviewDate(LocalDateTime.now())
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                .withDisposableIncomeAfterHardship(BigDecimal.valueOf(99.99))
                .withNewWorkReason(NewWorkReason.PRI)
                .withSolicitorCosts(getSolicitorsCosts())
                .withStatus(HardshipReviewStatus.COMPLETE)
                .withReviewDetails(
                        getApiHardshipReviewDetails(BigDecimal.valueOf(99.99), HardshipReviewDetailType.EXPENDITURE))
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
                                                        .withAmount(BigDecimal.valueOf(2000))
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
                                        new ApiUserSession()
                                                .withUserName(Constants.TEST_USERNAME)
                                                .withSessionId(Constants.TEST_USER_SESSION)
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
                               .withId(1)
                               .withProgressResponse(HardshipReviewProgressResponse.FURTHER_RECEIVED)
                               .withRemovedDate(Constants.REMOVED_DATETIME)
                               .withDateCompleted(Constants.TEST_DATE_REVIEWED_DATETIME)
                               .withDateRequested(Constants.TEST_DATE_REQUESTED_DATETIME)
                               .withDateRequired(Constants.TEST_DATE_REQUIRED_DATETIME)
                               .withProgressAction(HardshipReviewProgressAction.OTHER)
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
                                .withDetailType(HardshipReviewDetailType.INCOME)
                                .withAmount(amount)
                                .withFrequency(Frequency.MONTHLY)
                                .withAccepted("N")
                                .withOtherDescription("Statutory sick pay")
                                .withDetailCode(HardshipReviewDetailCode.SUSPENDED_WORK)
                );
                case EXPENDITURE -> details.add(
                        new ApiHardshipDetail()
                                .withId(Constants.TEST_HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.EXPENDITURE)
                                .withAmount(amount)
                                .withFrequency(Frequency.TWO_WEEKLY)
                                .withAccepted("Y")
                                .withDetailReason(HardshipReviewDetailReason.COVERED_BY_LIVING_EXPENSE)
                                .withOtherDescription("Loan to family members")
                                .withDetailCode(HardshipReviewDetailCode.OTHER)
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

    public static ApplicationDTO getApplicationDTOWithHardship(CourtType courtType) {
        return ApplicationDTO.builder()
                .repId(Constants.TEST_REP_ID.longValue())
                .caseManagementUnitDTO(getCaseManagementUnitDTO())
                .courtType(courtType)
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
                .build();
    }

    private static CaseManagementUnitDTO getCaseManagementUnitDTO() {
        return CaseManagementUnitDTO.builder()
                .cmuId(Constants.TEST_CMU_ID.longValue())
                .build();
    }

    private static HardshipOverviewDTO getHardshipOverviewDTO(CourtType courtType) {
        HardshipOverviewDTO.HardshipOverviewDTOBuilder<?, ?> dtoBuilder = HardshipOverviewDTO.builder();

        HardshipReviewDTO reviewDTO = HardshipReviewDTO.builder()
                .id(Constants.TEST_HARDSHIP_REVIEW_ID.longValue())
                .disposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                .reviewResult(Constants.TEST_HARDSHIP_REVIEW_RESULT)
                .disposableIncomeAfterHardship(Constants.TEST_POST_HARDSHIP_DISPOSABLE_INCOME)
                .reviewDate(Constants.TEST_DATE_REVIEWED)
                .section(TestModelDataBuilder.getHrSectionDtosWithMixedTypes())
                .asessmentStatus(getAssessmentStatusDTO())
                .newWorkReason(getNewWorkReasonDTO())
                .notes(Constants.TEST_CASEWORKER_NOTES)
                .decisionNotes(Constants.TEST_CASEWORKER_DECISION_NOTES)
                .progress(getHrProgressDTOs())
                .build();

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

    @NotNull
    private static List<HRProgressDTO> getHrProgressDTOs() {
        return List.of(
                HRProgressDTO.builder()
                        .id(53L)
                        .progressAction(
                                HRProgressActionDTO.builder()
                                        .action("SOLICITOR INFORMED")
                                        .build()
                        )
                        .progressResponse(
                                HRProgressResponseDTO.builder()
                                        .response("ADDITIONAL PROVIDED")
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
                .build();
    }

    private static AssessmentStatusDTO getAssessmentStatusDTO() {
        return AssessmentStatusDTO.builder()
                .status("COMPLETE")
                .build();
    }

    private static HRSolicitorsCostsDTO getHRSolicitorsCostsDTO() {
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
                        .annualTotal(BigDecimal.valueOf(2000.00))
                        .detailType(HRDetailTypeDTO.builder()
                                            .type("EXPENDITURE")
                                            .build())
                        .detail(List.of(
                                        HRDetailDTO.builder()
                                                .id(Constants.TEST_HARDSHIP_DETAIL_ID.longValue())
                                                .detailDescription(
                                                        HRDetailDescriptionDTO.builder()
                                                                .code("CAR LOAN")
                                                                .description("Car Loan")
                                                                .build())
                                                .accepted(false)
                                                .frequency(FrequenciesDTO.builder()
                                                                   .code("ANNUALLY")
                                                                   .annualWeighting(1L)
                                                                   .build())
                                                .amountNumber(BigDecimal.valueOf(2000))
                                                .otherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                .reason(
                                                        HRReasonDTO.builder()
                                                                .accepted(false)
                                                                .reason("Evidence Supplied")
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
                        .annualTotal(BigDecimal.valueOf(1500.00))
                        .detailType(HRDetailTypeDTO.builder()
                                            .type("INCOME")
                                            .build())
                        .detail(List.of(
                                        HRDetailDTO.builder()
                                                .id(Constants.TEST_SECOND_HARDSHIP_DETAIL_ID.longValue())
                                                .accepted(true)
                                                .frequency(FrequenciesDTO.builder()
                                                                   .code("MONTHLY")
                                                                   .annualWeighting(12L)
                                                                   .build())
                                                .amountNumber(BigDecimal.valueOf(1500.00))
                                                .hrReasonNote(Constants.TEST_HARDSHIP_REASON_NOTE)
                                                .otherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                .reason(
                                                        HRReasonDTO.builder()
                                                                .accepted(true)
                                                                .reason("MEDICAL GROUNDS")
                                                                .build())
                                                .build()
                                )
                        )
                        .build()
        );
    }
}
