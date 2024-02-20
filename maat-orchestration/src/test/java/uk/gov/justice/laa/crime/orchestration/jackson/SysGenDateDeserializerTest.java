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

    @Test
    void givenValidDate_whenDeserializeIsInvoked_thenSysGenDateIsDeserialized() throws IOException {
        JsonParser parser = factory.createParser("{\"value\": \"2021-09-30T22:00:00Z\"}");
        parser.setCodec(mapper);

        DeserializationContext deserializationContext = mapper.getDeserializationContext();
        SysGenDate result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result.getValue())
                .isEqualTo(Date.from(Instant.ofEpochMilli(1633039200000L)));
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

}