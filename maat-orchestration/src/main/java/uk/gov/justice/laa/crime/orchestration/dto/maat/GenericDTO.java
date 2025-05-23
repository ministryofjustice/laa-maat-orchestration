package uk.gov.justice.laa.crime.orchestration.dto.maat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.justice.laa.crime.jackson.ZonedDateTimeDeserializer;

import java.time.ZonedDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
public class GenericDTO {
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime timestamp;
    private Boolean selected;
}
