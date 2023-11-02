package uk.gov.justice.laa.maat.orchestration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class HRSolicitorsCostsDTO extends GenericDTO {

    private Currency solicitorRate;
    private Double solicitorHours;
    private Currency solicitorVat;
    private Currency solicitorDisb;
    private Currency solicitorEstimatedTotalCost;

}
