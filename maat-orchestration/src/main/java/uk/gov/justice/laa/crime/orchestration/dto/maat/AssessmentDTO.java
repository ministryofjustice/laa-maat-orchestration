package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class AssessmentDTO {
    private IOJAppealDTO iojAppeal;
    private FinancialAssessmentDTO financialAssessmentDTO;

}
