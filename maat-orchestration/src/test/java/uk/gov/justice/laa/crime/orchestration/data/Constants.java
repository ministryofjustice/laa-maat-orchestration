package uk.gov.justice.laa.crime.orchestration.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class Constants {

    public static final BigDecimal TEST_SOLICITOR_ESTIMATED_COST = BigDecimal.valueOf(2500);
    public static final Integer TEST_CMU_ID = 50;
    public static final Integer TEST_HARDSHIP_ID = 1234;
    public static final Integer TEST_HARDSHIP_DETAIL_ID = 12345;
    public static final Integer TEST_SECOND_HARDSHIP_DETAIL_ID = 12346;
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
    public static final BigDecimal TEST_DISPOSABLE_INCOME = BigDecimal.valueOf(12000);
    public static final BigDecimal TEST_POST_HARDSHIP_DISPOSABLE_INCOME = BigDecimal.valueOf(5000);
    public static final Integer TEST_REP_ID = 200;
    public static final Integer TEST_FINANCIAL_ASSESSMENT_ID = 321;
    public static final Integer TEST_HARDSHIP_REVIEW_ID = 1000;
    public static final String TEST_USERNAME = "mock-u";
    public static final String TEST_USER_SESSION = "8ab0bab5-c27e-471a-babf-c3992c7a4471";
    public static final String TEST_NEW_WORK_REASON_STRING = "NEW";
    public static final String TEST_CASEWORKER_NOTES = "Mock caseworker notes";
    public static final String TEST_CASEWORKER_DECISION_NOTES = "Mock caseworker decision notes";
    public static final String TEST_HARDSHIP_OTHER_DESCRIPTION = "Mock other description";
    public static final String TEST_HARDSHIP_REASON_NOTE = "Mock reason note";
    public static final String TEST_HARDSHIP_REVIEW_RESULT = "PASS";
    public static final Integer TEST_HARDSHIP_REVIEW_PROGRESS_ID = 53;
}