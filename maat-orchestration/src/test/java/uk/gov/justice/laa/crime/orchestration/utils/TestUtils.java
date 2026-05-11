package uk.gov.justice.laa.crime.orchestration.utils;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TestUtils {

    public static String formatDate(Date date) {
        DateTimeFormatter expectedDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");

        return date.toInstant().atOffset(ZoneOffset.UTC).format(expectedDateFormat);
    }
}
