package uk.gov.justice.laa.maat.orchestration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ContributionsDTO extends GenericDTO {
    private Long id;
    private SysGenCurrency monthlyContribs;
    private SysGenCurrency upfrontContribs;
    private SysGenDate effectiveDate;
    private SysGenDate calcDate;
    private SysGenCurrency capped;
    private boolean upliftApplied;
    private SysGenString basedOn;

}
