package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class IncomeEvidenceSummaryDTO extends GenericDTO {
    private static final long serialVersionUID = 1L;

    private Date evidenceDueDate;
    private Date evidenceReceivedDate;
    private Date upliftAppliedDate;
    private Date upliftRemovedDate;
    private Date firstReminderDate;
    private Date secondReminderDate;

    @Builder.Default
    private String incomeEvidenceNotes = "";

    @Builder.Default
    private Collection<EvidenceDTO> applicantIncomeEvidenceList = new ArrayList<>();

    @Builder.Default
    private Collection<EvidenceDTO> partnerIncomeEvidenceList = new ArrayList<>();

    @Builder.Default
    private Collection<ExtraEvidenceDTO> extraEvidenceList = new ArrayList<>();

    @Builder.Default
    private Boolean enabled = Boolean.FALSE;

    private Boolean upliftsAvailable;
}
