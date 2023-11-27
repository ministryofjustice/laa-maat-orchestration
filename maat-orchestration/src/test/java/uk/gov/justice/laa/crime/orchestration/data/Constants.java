package uk.gov.justice.laa.crime.orchestration.data;

import uk.gov.justice.laa.crime.orchestration.dto.maat.SysGenString;
import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.orchestration.enums.NewWorkReason;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class Constants {

    public static final Integer TEST_CMU_ID = 50;
    public static final Integer TEST_HARDSHIP_DETAIL_ID = 12345;
    public static final LocalDateTime REMOVED_DATETIME = LocalDateTime.of(2022, 12, 14, 0, 0, 0);
    public static final LocalDateTime TEST_DATE_COMPLETED_DATETIME = LocalDateTime.of(2022, 11, 14, 0, 0, 0);
    public static final Date TEST_DATE_COMPLETED =
            Date.from(Instant.ofEpochSecond(TEST_DATE_COMPLETED_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    public static final LocalDateTime TEST_DATE_REVIEWED_DATETIME = LocalDateTime.of(2022, 11, 12, 0, 0, 0);
    public static final Date TEST_DATE_REVIEWED =
            Date.from(Instant.ofEpochSecond(TEST_DATE_REVIEWED_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    public static final LocalDateTime TEST_DATE_REQUESTED_DATETIME = LocalDateTime.of(2022, 11, 11, 0, 0, 0);
    public static final Date TEST_DATE_REQUESTED =
            Date.from(Instant.ofEpochSecond(TEST_DATE_REQUESTED_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    public static final LocalDateTime TEST_DATE_REQUIRED_DATETIME = LocalDateTime.of(2022, 12, 15, 0, 0, 0);
    public static final Date TEST_DATE_REQUIRED =
            Date.from(Instant.ofEpochSecond(TEST_DATE_REQUIRED_DATETIME.toEpochSecond(ZoneOffset.UTC)));

    // Solicitors Costs
    public static final BigDecimal TEST_SOLICITOR_HOURS = BigDecimal.valueOf(50)
            .setScale(1, RoundingMode.DOWN);
    public static final BigDecimal TEST_SOLICITOR_RATE = BigDecimal.valueOf(200);
    public static final BigDecimal TEST_SOLICITOR_DISBURSEMENTS = BigDecimal.valueOf(375);
    public static final BigDecimal TEST_SOLICITOR_VAT = BigDecimal.valueOf(250);
    public static final BigDecimal TEST_SOLICITOR_ESTIMATED_COST = BigDecimal.valueOf(2500);

    public static final BigDecimal TEST_DISPOSABLE_INCOME = BigDecimal.valueOf(12000);
    public static final BigDecimal TEST_POST_HARDSHIP_DISPOSABLE_INCOME = BigDecimal.valueOf(5000);
    public static final Integer TEST_REP_ID = 200;
    public static final Integer TEST_FINANCIAL_ASSESSMENT_ID = 321;
    public static final Integer TEST_HARDSHIP_REVIEW_ID = 1000;
    public static final String TEST_USERNAME = "mock-u";
    public static final String TEST_USER_SESSION = "8ab0bab5-c27e-471a-babf-c3992c7a4471";
    public static final String TEST_NEW_WORK_REASON_STRING = NewWorkReason.NEW.getCode();
    public static final String TEST_CASEWORKER_NOTES = "Mock caseworker notes";
    public static final String TEST_CASEWORKER_DECISION_NOTES = "Mock caseworker decision notes";
    public static final String TEST_HARDSHIP_OTHER_DESCRIPTION = "Mock other description";
    public static final String TEST_HARDSHIP_REASON_NOTE = "Mock reason note";
    public static final String TEST_HARDSHIP_REVIEW_RESULT = HardshipReviewResult.PASS.name();
    public static final Integer TEST_HARDSHIP_REVIEW_PROGRESS_ID = 53;

    public static final Integer TEST_ASSESSMENT_SUMMARY_ID = 75;
    public static final LocalDateTime TEST_ASSESSMENT_SUMMARY_DATETIME = LocalDateTime.of(2022, 9, 3, 0, 0, 0);
    public static final Date TEST_ASSESSMENT_SUMMARY_DATE =
            Date.from(Instant.ofEpochSecond(TEST_ASSESSMENT_SUMMARY_DATETIME.toEpochSecond(ZoneOffset.UTC)));


    public static final Integer TEST_CONTRIBUTIONS_ID = 43;
    public static final BigDecimal TEST_MONTHLY_CONTRIBUTION_AMOUNT = BigDecimal.valueOf(150.00);
    public static final BigDecimal TEST_UPFRONT_CONTRIBUTION_AMOUNT = BigDecimal.valueOf(2000.00);
    public static final LocalDateTime TEST_CONTRIBUTION_EFFECTIVE_DATETIME = LocalDateTime.of(2022, 10, 5, 0, 0, 0);
    public static final Date TEST_CONTRIBUTION_EFFECTIVE_DATE =
            Date.from(Instant.ofEpochSecond(TEST_CONTRIBUTION_EFFECTIVE_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    public static final LocalDateTime TEST_CONTRIBUTION_CALCULATION_DATETIME = LocalDateTime.of(2022, 10, 5, 0, 0, 0);
    public static final Date TEST_CONTRIBUTION_CALCULATION_DATE =
            Date.from(Instant.ofEpochSecond(TEST_CONTRIBUTION_CALCULATION_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    public static final SysGenString CONTRIBUTION_BASED_ON = new SysGenString("Means");

}
