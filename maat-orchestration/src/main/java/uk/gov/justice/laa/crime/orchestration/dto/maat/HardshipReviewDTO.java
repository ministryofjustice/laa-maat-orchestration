package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.springframework.lang.Nullable;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class HardshipReviewDTO extends GenericDTO {

    private Integer id;
    private SupplierDTO supplier;
    private NewWorkReasonDTO newWorkReason;
    private Long cmuId;

    @Nullable
    private String reviewResult;

    private Date reviewDate;
    private String notes;
    private String decisionNotes;
    // Spelling mistake in MAAT
    private HRSolicitorsCostsDTO solictorsCosts;
    private BigDecimal disposableIncome;
    private BigDecimal disposableIncomeAfterHardship;
    private Collection<HRSectionDTO> section;
    // Spelling mistake in MAAT
    private AssessmentStatusDTO asessmentStatus;
}
