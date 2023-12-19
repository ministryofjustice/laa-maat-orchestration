package uk.gov.justice.laa.crime.orchestration.data;

import uk.gov.justice.laa.crime.orchestration.dto.maat.SysGenString;
import uk.gov.justice.laa.crime.orchestration.enums.Frequency;
import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.orchestration.enums.NewWorkReason;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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

    public static final Integer TEST_CRITERIA_ID = 34;
    public static final String TEST_ASSESSMENT_NOTES = "Mock assessment notes";;
    public static final String TEST_OTHER_HOUSING_NOTE = "Mock other housing note";
    public static final Integer TEST_USN = 123456789;
    public static final LocalDateTime ASSESSMENT_DATE = LocalDateTime.parse("2022-10-09T15:02:25");
    public static final BigDecimal TEST_AGGREGATED_EXPENSE = BigDecimal.valueOf(22000.00);
    public static final BigDecimal TEST_ANNUAL_DISPOSABLE_INCOME = BigDecimal.valueOf(1000.00);
    public static final BigDecimal THRESHOLD = BigDecimal.valueOf(5000.00);
    public static final String TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final LocalDateTime TEST_INCOME_EVIDENCE_DUE_DATE =
            LocalDateTime.of(2020, 10, 5, 0, 0, 0);
    public static final LocalDateTime TEST_INCOME_UPLIFT_APPLY_DATE =
            LocalDateTime.of(2021, 12, 12, 0, 0, 0);
    public static final LocalDateTime TEST_INCOME_UPLIFT_REMOVE_DATE =
            TEST_INCOME_UPLIFT_APPLY_DATE.plusDays(10);
    public static final String TEST_INCOME_EVIDENCE_NOTES = "Mock Income evidence notes";
    public static final String TEST_EMPLOYMENT_STATUS = "EMPLOY";
    public static final BigDecimal TEST_APPLICANT_ANNUAL_TOTAL = BigDecimal.valueOf(12000);
    public static final Integer TEST_CRITERIA_DETAIL_ID = 135;
    public static final BigDecimal TEST_APPLICANT_VALUE = BigDecimal.valueOf(1000);
    public static final Frequency TEST_FREQUENCY = Frequency.MONTHLY;
    public static final String TEST_FULL_ASSESSMENT_NOTES = "Mock full assessment notes";;


    // TODO: Better organise these constants
    public static final LocalDateTime TEST_DATETIME_RECEIVED = LocalDateTime.of(2022, 10, 13, 0, 0, 0);
    public static final LocalDateTime TEST_COMMITAL_DATETIME = TEST_DATETIME_RECEIVED.plus(1, ChronoUnit.DAYS);
    public static final LocalDateTime TEST_DECISION_DATETIME = TEST_COMMITAL_DATETIME.plus(1, ChronoUnit.DAYS);
    public static final Date TEST_ASSESSMENT_DATE =
            Date.from(TEST_DATETIME_RECEIVED.plus(2, ChronoUnit.DAYS).toInstant(ZoneOffset.UTC));
    public static final LocalDateTime TEST_CC_REP_ORDER_DATETIME = LocalDateTime.of(2022, 10, 13, 0, 0, 0);
    public static final LocalDateTime TEST_SENTENCE_ORDER_DATETIME = TEST_CC_REP_ORDER_DATETIME.plus(1, ChronoUnit.DAYS);
    public static final SysGenString TEST_CC_REP_TYPE_THROUGH_ORDER = new SysGenString("Through Order");
    public static final SysGenString TEST_REP_ORDER_DECISION_GRANTED = new SysGenString("Granted");
    public static final String TEST_RESULT_PASS = "PASS";
    public static final String TEST_RESULT_FAIL = "FAIL";
    public static final Integer TEST_APPLICANT_HISTORY_ID = 666;
    public static final Integer TEST_PASSPORTED_ID = 777;
    public static final String TEST_EVIDENCE_FEE_LEVEL_1 = "LEVEL1";
    public static final String INCOME_EVIDENCE = "TAX RETURN";
    public static final String INCOME_EVIDENCE_DESCRIPTION = "Tax Return";
    public static final Integer EVIDENCE_ID = 9552473;

    public static final String DB_ASSESSMENT_POST_PROCESSING_PART_1 = "post_assessment_processing_part_1";
    public static final String DB_ASSESSMENT_POST_PROCESSING_PART_2 = "post_assessment_processing_part_2";
    public static final String DB_ASSESSMENT_POST_PROCESSING_PART_1_C3 = "post_assessment_processing_part_1_c3";
    public static final String DB_PRE_UPDATE_CC_APPLICATION = "pre_update_cc_application";
    public static final String DB_PACKAGE_ASSESSMENTS = "assessments";
    public static final String DB_PACKAGE_APPLICATION = " application";
}
