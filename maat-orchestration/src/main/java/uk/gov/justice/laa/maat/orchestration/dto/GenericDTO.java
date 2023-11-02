package uk.gov.justice.laa.maat.orchestration.dto;

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
    private Boolean mDirty;
}
