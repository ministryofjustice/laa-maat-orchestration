package uk.gov.justice.laa.crime.orchestration.data;

import uk.gov.justice.laa.crime.enums.HardshipReviewResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {

    public static final BigDecimal DISPOSABLE_INCOME = BigDecimal.valueOf(12000.0);
    public static final BigDecimal POST_HARDSHIP_DISPOSABLE_INCOME = BigDecimal.valueOf(5000);
    public static final Integer FINANCIAL_ASSESSMENT_ID = 321;
    public static final Integer HARDSHIP_REVIEW_ID = 1000;
    public static final String HARDSHIP_REVIEW_RESULT = HardshipReviewResult.PASS.name();
    public static final Integer ASSESSMENT_SUMMARY_ID = 75;
    private static final LocalDateTime ASSESSMENT_SUMMARY_DATETIME = LocalDateTime.of(2022, 9, 3, 0, 0, 0);
    public static final Date ASSESSMENT_SUMMARY_DATE =
            Date.from(Instant.ofEpochSecond(ASSESSMENT_SUMMARY_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    public static final Integer CONTRIBUTIONS_ID = 43;
    public static final Integer APPLICANT_ID = 999;
    public static final Integer PARTNER_ID = 666;
    public static final String FIRST_NAME = "Edward";
    public static final String LAST_NAME = "Munson";
    public static final String NI_NUMBER = "JR679802A";
    public static final LocalDate DATE_OF_BIRTH = LocalDate.of(1969, 11, 24);
    public static final String USERNAME = "mock-u";
    public static final String TEST_TRACE_ID = "test-trace-id";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final Integer USN = 123456789;
    public static final LocalDateTime ASSESSMENT_DATETIME = LocalDateTime.of(2022, 9, 3, 0, 0, 0);
    public static final String NOTES = "This is a test note.";
    public static final int PASSPORT_ASSESSMENT_ID = 123;
    public static final int CASE_MANAGEMENT_UNIT_ID = 50;
    public static final LocalDateTime LAST_SIGNON_DATETIME = LocalDateTime.of(2022, 8, 3, 0, 0, 0);
    public static final boolean WITH_PARTNER = true;
    public static final boolean WITHOUT_PARTNER = false;
    public static final boolean WITHOUT_AUTH = false;
}
