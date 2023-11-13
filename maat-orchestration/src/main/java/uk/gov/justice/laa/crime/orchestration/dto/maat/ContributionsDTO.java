package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ContributionsDTO extends GenericDTO {
    private Long id;
    private BigDecimal monthlyContribs;
    private BigDecimal upfrontContribs;
    private Date effectiveDate;
    private Date calcDate;
    private BigDecimal capped;
    private boolean upliftApplied;
    private SysGenString basedOn;

}
