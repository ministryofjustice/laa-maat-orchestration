package uk.gov.justice.laa.maat.orchestration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EvidenceTypeDTO extends GenericDTO {

    private String evidence;        // key field
    private String description;

}
