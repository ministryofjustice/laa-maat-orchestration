package uk.gov.justice.laa.crime.orchestration.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final String NULL_VALUE = "null";

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws
            IOException {

        String dateTime = jsonParser.getValueAsString();
        try {
            if (StringUtils.isNotBlank(dateTime) && !dateTime.trim().equals(NULL_VALUE)) {
                if (!dateTime.contains(".") && !dateTime.contains("+")) {
                    return LocalDateTime.parse(dateTime, ISO_LOCAL_DATE_TIME);
                }
                else if (dateTime.contains(".") && !dateTime.contains("+")) {
                    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                            .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
                            .toFormatter();
                    return LocalDateTime.parse(dateTime, formatter).truncatedTo(ChronoUnit.SECONDS);
                } else return LocalDateTime.parse(dateTime, ISO_OFFSET_DATE_TIME).truncatedTo(ChronoUnit.SECONDS);
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid date value: " + dateTime, e);
        }
        return null;
    }
}
