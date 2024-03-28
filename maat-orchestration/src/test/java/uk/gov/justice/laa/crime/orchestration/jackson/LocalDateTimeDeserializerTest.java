package uk.gov.justice.laa.crime.orchestration.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDateTimeDeserializerTest {

    private final JsonFactory factory = new JsonFactory();
    private final ObjectMapper mapper = new ObjectMapper();
    private final LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer();

    private static final String ISO_DATE = "2024-01-27T10:15:30";

    @Test
    void givenValidDate_whenDeserializeIsInvoked_thenLocalDateTimeIsDeserialized() throws IOException {
        String content = String.format("\"%s\"", ISO_DATE);
        JsonParser parser = factory.createParser(content);
        parser.setCodec(mapper);
        parser.nextToken();

        LocalDateTime expected = LocalDateTime.of(2024, 1, 27, 10, 15, 30);
        LocalDateTime result = deserializer.deserialize(parser, mapper.getDeserializationContext());
        assertThat(result).isEqualTo(expected);
    }
}
