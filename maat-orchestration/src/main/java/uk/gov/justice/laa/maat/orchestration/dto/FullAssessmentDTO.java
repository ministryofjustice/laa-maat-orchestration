package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class FullAssessmentDTO extends GenericDTO {
    private Long criteriaId;
    private Date assessmentDate;
    private String assessmentNotes;
    private Collection<AssessmentSectionSummaryDTO> sectionSummaries;
    private Double adjustedLivingAllowance;
    private String otherHousingNote;
    private Double totalAggregatedExpense;
    private Double totalAnnualDisposableIncome;
    private Double threshold;
    private String result;
    private String resultReason;
    private AssessmentStatusDTO assessmnentStatusDTO;

    public FullAssessmentDTO() {
        reset();
    }

    private void reset() {
        assessmentNotes = "";
        otherHousingNote = "";

        sectionSummaries = new ArrayList<>();
        assessmnentStatusDTO = new AssessmentStatusDTO();

    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("FullAssmtDTO- id[N/A] ");
        sb.append("Ass.Date[").append(this.assessmentDate).append("] ");
        sb.append("result[").append(this.result).append("] ");
        sb.append("status[").append(this.assessmnentStatusDTO).append("] ");
        sb.append("Sections[").append(this.sectionSummaries).append("]");
        return sb.toString();
    }

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getAssessmentNotes() {
        return assessmentNotes;
    }

    public void setAssessmentNotes(String assessmentNotes) {
        this.assessmentNotes = assessmentNotes;
    }

    public Double getAdjustedLivingAllowance() {
        return adjustedLivingAllowance;
    }

    public void setAdjustedLivingAllowance(Double adjustedLivingAllowance) {
        this.adjustedLivingAllowance = adjustedLivingAllowance;
    }

    public String getOtherHousingNote() {
        return otherHousingNote;
    }

    public void setOtherHousingNote(String otherHousingNote) {
        this.otherHousingNote = otherHousingNote;
    }

    public Double getTotalAggregatedExpense() {
        return totalAggregatedExpense;
    }

    public void setTotalAggregatedExpense(Double totalAggregatedExpense) {
        this.totalAggregatedExpense = totalAggregatedExpense;
    }

    public Double getTotalAnnualDisposableIncome() {
        return totalAnnualDisposableIncome;
    }

    public void setTotalAnnualDisposableIncome(Double totalAnnualDisposableIncome) {
        this.totalAnnualDisposableIncome = totalAnnualDisposableIncome;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultReason() {
        return resultReason;
    }

    public void setResultReason(String resultReason) {
        this.resultReason = resultReason;
    }

    public Collection<AssessmentSectionSummaryDTO> getSectionSummaries() {
        return sectionSummaries;
    }

    public void setSectionSummaries(
            Collection<AssessmentSectionSummaryDTO> sectionSummaries) {
        this.sectionSummaries = sectionSummaries;
    }

    public AssessmentStatusDTO getAssessmnentStatusDTO() {
        return assessmnentStatusDTO;
    }

    public void setAssessmnentStatusDTO(AssessmentStatusDTO assessmnentStatusDTO) {
        this.assessmnentStatusDTO = assessmnentStatusDTO;
    }

    public boolean isInProgress() {
        return assessmnentStatusDTO.isInPROGRESS();
    }

    public boolean isComplete() {
        return assessmnentStatusDTO.isComplete();
    }

    public Long getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }

}
