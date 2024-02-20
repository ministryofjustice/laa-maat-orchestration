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

    @Test
    void givenValidDate_whenDeserializeIsInvoked_thenLocalDateTimeIsDeserialized() throws IOException {
        JsonParser parser = factory.createParser("1633027200000");
        parser.setCodec(mapper);
        parser.nextToken();

        LocalDateTime result = deserializer.deserialize(parser, mapper.getDeserializationContext());

        LocalDateTime expected =
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(1633027200000L), ZoneId.systemDefault())
                        .toLocalDateTime();

        assertThat(result).isEqualTo(expected);
    }
}
