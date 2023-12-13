package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Collection;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CapitalOtherDTO extends GenericDTO {

    private static final long serialVersionUID = 1L;

    private Long id;
    private CapitalTypeDTO capitalTypeDTO;
    private String otherDescription;
    private BigDecimal assetAmount;
    private BigDecimal verifiedAmount;
    private SysGenDate dateEntered;
    private SysGenDate verifiedDate;
    private SysGenString verifiedBy;
    private AssessmentStatusDTO assessmentStatus;
    private Collection<EvidenceDTO> capitalEvidence;
    private Collection<ExtraEvidenceDTO> extraEvidence;
    private Boolean undeclared;
    private String bankName;
    private String branchSortCode;
    private String accountOwner;

}
