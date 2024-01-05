package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EquityDTO extends GenericDTO {
    private Long id;
    private PropertyDTO propertyDTO;
    private AssessmentStatusDTO assessmentStatus;
    private Date dateEntered;
    private Date verifiedDate;
    private String verifiedBy;
    private Boolean undeclared;

}