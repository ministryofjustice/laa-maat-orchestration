package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommonPlatformDataDTO extends GenericDTO {

    private Long repOrderId;
    private String caseURN;
    private String defendantId;
    private Timestamp dateCreated;
    private String userCreated;
    private Timestamp dateModified;
    private String userModified;
    private String inCommonPlatform;

}