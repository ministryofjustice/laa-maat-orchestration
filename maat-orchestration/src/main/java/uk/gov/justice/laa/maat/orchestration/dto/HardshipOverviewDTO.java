package uk.gov.justice.laa.maat.orchestration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class HardshipOverviewDTO extends GenericDTO {
    private HardshipReviewDTO magCourtHardship;
    private HardshipReviewDTO crownCourtHardship;

}