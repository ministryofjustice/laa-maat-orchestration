package uk.gov.justice.laa.crime.orchestration.dto.maat_api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinancialAssessmentDTO {
    private Integer id;
    private Integer repId;
    private String replaced;
    private LocalDateTime dateCompleted;
    private LocalDateTime initialAssessmentDate;
    private LocalDateTime fullAssessmentDate;
}
