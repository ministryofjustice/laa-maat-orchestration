package uk.gov.justice.laa.crime.orchestration.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.orchestration.dto.maat.SysGenDate;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SysGenDateDeserializerTest {

    private final JsonFactory factory = new JsonFactory();
    private final ObjectMapper mapper = new ObjectMapper();
    private final SysGenDateDeserializer deserializer = new SysGenDateDeserializer();

    private static final long EPOCH_DATE = 1633039200000L;
    private static final String ISO_DATE = "2021-09-30T22:00:00Z";

    @Test
    void givenValidDate_whenDeserializeIsInvoked_thenSysGenDateIsDeserialized() throws IOException {
        String content = String.format("{\"value\": \"%s\"}", ISO_DATE);
        JsonParser parser = factory.createParser(content);
        parser.setCodec(mapper);

        DeserializationContext deserializationContext = mapper.getDeserializationContext();
        SysGenDate result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result.getValue())
                .isEqualTo(Date.from(Instant.ofEpochMilli(EPOCH_DATE)));
    }

    @Test
    void givenInvalidDate_whenDeserializeIsInvoked_thenIllegalArgumentExceptionIsThrown() throws IOException {
        JsonParser parser = factory.createParser("{\"value\": \"invalid\"}");
        parser.setCodec(mapper);

        DeserializationContext deserializationContext = mapper.getDeserializationContext();
        assertThatThrownBy(() -> deserializer.deserialize(parser, deserializationContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid date value:");
    }
    @Test
    void givenANullDate_whenDeserializeIsInvoked_thenNullIsReturned() throws IOException {
        JsonParser parser = factory.createParser("{\"value\":\"\"}");
        parser.setCodec(mapper);
        DeserializationContext deserializationContext = mapper.getDeserializationContext();
        assertThat(deserializer.deserialize(parser, deserializationContext)).isNull();
    }

    @Test
    void givenANullValue_whenDeserializeIsInvoked_thenNullIsReturned() throws IOException {
        JsonParser parser = factory.createParser("{\"value\":\"null\"}");
        parser.setCodec(mapper);
        DeserializationContext deserializationContext = mapper.getDeserializationContext();
        assertThat(deserializer.deserialize(parser, deserializationContext)).isNull();
    }

}