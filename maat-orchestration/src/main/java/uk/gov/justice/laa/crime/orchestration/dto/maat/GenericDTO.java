package uk.gov.justice.laa.crime.orchestration.dto.maat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.justice.laa.crime.orchestration.jackson.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
public class GenericDTO {
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
    private Boolean selected;
}
