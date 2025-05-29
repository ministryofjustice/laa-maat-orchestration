package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FinancialAssessmentDTO extends GenericDTO {
    private Integer id;
    private Long criteriaId;
    private InitialAssessmentDTO initial;
    private FullAssessmentDTO full;
    private HardshipOverviewDTO hardship;
    private IncomeEvidenceSummaryDTO incomeEvidence;
    private Boolean fullAvailable;
    private Long usn;
    private LocalDateTime dateCompleted;

}
