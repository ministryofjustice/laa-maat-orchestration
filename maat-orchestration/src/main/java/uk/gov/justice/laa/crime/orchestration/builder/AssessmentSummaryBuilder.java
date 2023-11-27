package uk.gov.justice.laa.crime.orchestration.builder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;

@Slf4j
@Component
public class AssessmentSummaryBuilder {

    public AssessmentSummaryDTO build(HardshipReviewDTO hardshipReviewDTO, CourtType courtType) {
        return AssessmentSummaryDTO.builder()
                .id(hardshipReviewDTO.getId())
                .status(hardshipReviewDTO.getAsessmentStatus().getStatus())
                .type(courtType == CourtType.CROWN_COURT
                              ? "Hardship Review - Crown Court"
                              : "Hardship Review - Magistrate"
                )
                .result(hardshipReviewDTO.getReviewResult())
                .assessmentDate(hardshipReviewDTO.getReviewDate()).build();
    }
}
