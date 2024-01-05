package uk.gov.justice.laa.crime.orchestration.data;

import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class Constants {

    public static final BigDecimal DISPOSABLE_INCOME = BigDecimal.valueOf(12000);
    public static final BigDecimal POST_HARDSHIP_DISPOSABLE_INCOME = BigDecimal.valueOf(5000);
    public static final Integer FINANCIAL_ASSESSMENT_ID = 321;
    public static final Integer HARDSHIP_REVIEW_ID = 1000;
    public static final String HARDSHIP_REVIEW_RESULT = HardshipReviewResult.PASS.name();
    public static final Integer ASSESSMENT_SUMMARY_ID = 75;
    private static final LocalDateTime ASSESSMENT_SUMMARY_DATETIME = LocalDateTime.of(2022, 9, 3, 0, 0, 0);
    public static final Date ASSESSMENT_SUMMARY_DATE =
            Date.from(Instant.ofEpochSecond(ASSESSMENT_SUMMARY_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    public static final Integer CONTRIBUTIONS_ID = 43;
    public static final String DB_ASSESSMENT_POST_PROCESSING_PART_1 = "post_assessment_processing_part_1";
    public static final String DB_ASSESSMENT_POST_PROCESSING_PART_2 = "post_assessment_processing_part_2";
    public static final String DB_ASSESSMENT_POST_PROCESSING_PART_1_C3 = "post_assessment_processing_part_1_c3";
    public static final String DB_PRE_UPDATE_CC_APPLICATION = "pre_update_cc_application";
    public static final String DB_PACKAGE_ASSESSMENTS = "assessments";
    public static final String DB_PACKAGE_APPLICATION = "application";
    public static final Integer APPLICANT_ID = 999;

}
