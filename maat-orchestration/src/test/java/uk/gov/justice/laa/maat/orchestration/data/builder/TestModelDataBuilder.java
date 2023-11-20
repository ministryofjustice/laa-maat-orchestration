package uk.gov.justice.laa.maat.orchestration.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.maat.orchestration.enums.*;
import uk.gov.justice.laa.maat.orchestration.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.maat.orchestration.model.ApiHardshipDetail;
import uk.gov.justice.laa.maat.orchestration.model.ApiHardshipProgress;
import uk.gov.justice.laa.maat.orchestration.model.SolicitorCosts;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TestModelDataBuilder {
    public static final Integer CMU_ID = 50;
    public static final Integer HARDSHIP_ID = 1234;
    public static final Integer HARDSHIP_DETAIL_ID = 12345;
    public static final LocalDateTime REMOVED_DATE = LocalDateTime.of(2022, 12, 14, 0, 0, 0);
    public static final LocalDateTime COMPLETED_DATE = LocalDateTime.of(2022, 11, 14, 0, 0, 0);
    public static final LocalDateTime REQUESTED_DATE = LocalDateTime.of(2022, 10, 14, 0, 0, 0);
    public static final LocalDateTime REQUIRED_DATE = LocalDateTime.of(2022, 12, 15, 0, 0, 0);
    public static final BigDecimal TOTAL_DISPOSABLE_INCOME = BigDecimal.valueOf(500);

    // Solicitors Costs
    public static final Integer TEST_SOLICITOR_HOURS = 50;
    public static final BigDecimal TEST_SOLICITOR_RATE = BigDecimal.valueOf(200);
    public static final BigDecimal TEST_SOLICITOR_DISBURSEMENTS = BigDecimal.valueOf(375);
    public static final BigDecimal TEST_SOLICITOR_VAT = BigDecimal.valueOf(250);
    public static final BigDecimal TEST_SOLICITOR_ESTIMATED_COST = BigDecimal.valueOf(2500);
    public static final BigDecimal AMOUNT = BigDecimal.valueOf(99.99);

    public static ApiFindHardshipResponse getApiFindHardshipResponse() {
        return new ApiFindHardshipResponse()
                .withId(HARDSHIP_ID)
                .withCmuId(CMU_ID)
                .withNotes("Test note.")
                .withDecisionNotes("Test decision note.")
                .withReviewDate(LocalDateTime.now())
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(TOTAL_DISPOSABLE_INCOME)
                .withDisposableIncomeAfterHardship(BigDecimal.valueOf(99.99))
                .withNewWorkReason(NewWorkReason.PRI)
                .withSolicitorCosts(getSolicitorsCosts())
                .withStatus(HardshipReviewStatus.COMPLETE)
                .withReviewDetails(getApiHardshipReviewDetails(AMOUNT, HardshipReviewDetailType.EXPENDITURE))
                .withReviewProgressItems(getReviewProgressItems());
    }

    private static List<ApiHardshipProgress> getReviewProgressItems() {
        return List.of(new ApiHardshipProgress()
                .withId(1)
                .withProgressResponse(HardshipReviewProgressResponse.FURTHER_RECEIVED)
                .withRemovedDate(REMOVED_DATE)
                .withDateCompleted(COMPLETED_DATE)
                .withDateRequested(REQUESTED_DATE)
                .withDateRequired(REQUIRED_DATE)
                .withProgressAction(HardshipReviewProgressAction.OTHER)
        );
    }

    public static SolicitorCosts getSolicitorsCosts() {
        return new SolicitorCosts()
                .withVat(TEST_SOLICITOR_VAT)
                .withDisbursements(TEST_SOLICITOR_DISBURSEMENTS)
                .withRate(TEST_SOLICITOR_RATE)
                .withHours(TEST_SOLICITOR_HOURS)
                .withEstimatedTotal(TEST_SOLICITOR_ESTIMATED_COST);
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
                                .withId(HARDSHIP_DETAIL_ID)
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
}
