package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Data
@SuperBuilder
@NoArgsConstructor
public class GenericDTO {
    private Timestamp timestamp;
    private Boolean selected;
}
