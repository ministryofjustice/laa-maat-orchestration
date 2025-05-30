package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AssessmentSummaryDTO extends GenericDTO {
    private Integer id;
    private Date assessmentDate;
    private String type;
    private String reviewType;
    private String status;
    private String result;
}