package uk.gov.justice.laa.maat.orchestration.dto;

public class FinancialAssessmentDTO extends GenericDTO {
    private Long id;
    private Long criteriaId;
    private InitialAssessmentDTO initial;
    private FullAssessmentDTO full;
    private HardshipOverviewDTO hardship;
    private IncomeEvidenceSummaryDTO incomeEvidence;
    private Boolean fullAvailable;
    private Long usn;

    public FinancialAssessmentDTO() {
        reset();
    }

    public void reset() {
        this.id = null;
        this.initial = new InitialAssessmentDTO();
        this.full = new FullAssessmentDTO();
        this.hardship = new HardshipOverviewDTO();
        this.incomeEvidence = new IncomeEvidenceSummaryDTO();
        this.fullAvailable = Boolean.FALSE;
        this.usn = null;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id[").append(this.id).append("] ");
        sb.append("Crit.Id.").append(this.criteriaId).append("] ");
        sb.append("Initial[").append(this.initial == null ? "null" : this.initial).append("] ");
        sb.append("Full[").append(this.full == null ? "null" : this.full).append("] ");
        sb.append("FullAss.Avail.[").append(this.fullAvailable == null ? "null" : this.fullAvailable.booleanValue()).append("] ");
        sb.append("usn[").append(this.usn).append("] ");
        return sb.toString();

    }

    public String toStringSummaryId() {
        StringBuffer sb = new StringBuffer();
        sb.append("id[").append(this.id).append("] ");
        sb.append("Crit.Id.").append(this.criteriaId).append("] ");
        sb.append("InitStat[").append(this.initial == null ? "null" : this.initial.getAssessmnentStatusDTO()).append("] ");
        sb.append("FullAss.Avail.[").append(this.fullAvailable == null ? "null" : this.fullAvailable.booleanValue()).append("] ");
        sb.append("FullStat[").append(this.full == null ? "null" : this.full.getAssessmnentStatusDTO()).append("] ");
        sb.append("IE.Avail.[").append(this.incomeEvidence == null ? "null" : this.incomeEvidence.getEnabled().booleanValue()).append("] ");
        sb.append("usn[").append(this.usn).append("] ");
        return sb.toString();

    }

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }

    public InitialAssessmentDTO getInitial() {
        return initial;
    }

    public void setInitial(InitialAssessmentDTO initial) {
        this.initial = initial;
    }

    public FullAssessmentDTO getFull() {
        return full;
    }

    public void setFull(FullAssessmentDTO full) {
        this.full = full;
    }

    public HardshipOverviewDTO getHardship() {
        return hardship;
    }

    public void setHardship(HardshipOverviewDTO hardship) {
        this.hardship = hardship;
    }

    public IncomeEvidenceSummaryDTO getIncomeEvidence() {
        return incomeEvidence;
    }

    public void setIncomeEvidence(IncomeEvidenceSummaryDTO incomeEvidence) {
        this.incomeEvidence = incomeEvidence;
    }

    public Boolean getFullAvailable() {
        return fullAvailable;
    }

    public void setFullAvailable(Boolean fullAvailable) {
        this.fullAvailable = fullAvailable;
    }

    public void setUsn(Long usn) {
        this.usn = usn;
    }

    public Long getUsn() {
        return usn;
    }

}
