package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class InitialAssessmentDTO extends GenericDTO {

    private Long id;
    private Long criteriaId;
    private Date assessmentDate;
    private String otherBenefitNote;
    private String otherIncomeNote;
    private Double totalAggregatedIncome;
    private Double adjustedIncomeValue;
    private String notes;
    private Double lowerThreshold;
    private Double upperThreshold;
    private String result;
    private String resultReason;
    private AssessmentStatusDTO assessmnentStatusDTO;
    private NewWorkReasonDTO newWorkReason;
    private ReviewTypeDTO reviewType;
    private SupplierDTO supplierDTO;
    private Collection<AssessmentSectionSummaryDTO> sectionSummaries;
    private Collection<ChildWeightingDTO> childWeightings;

    public InitialAssessmentDTO() {
        reset();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("InitAssmtDTO- id[").append(this.id).append("] ");
        sb.append("Ass.Date[").append(this.assessmentDate).append("] ");
        sb.append("result[").append(this.result).append("] ");
        sb.append("status[").append(this.assessmnentStatusDTO).append("] \n\r");
        sb.append("Sections[").append(this.sectionSummaries).append("] \n\r");
        return sb.toString();
    }

    public void reset() {
        this.id = null;
        this.otherBenefitNote = "";
        this.otherIncomeNote = "";
        this.notes = "";
        this.assessmnentStatusDTO = new AssessmentStatusDTO();
        this.newWorkReason = new NewWorkReasonDTO();
        this.reviewType = new ReviewTypeDTO();
        this.supplierDTO = new SupplierDTO();
        this.sectionSummaries = new ArrayList<>();
        this.childWeightings = new ArrayList<>();

    }

    public boolean isComplete() {
        return this.assessmnentStatusDTO.isComplete();
    }

    public boolean isInProgress() {
        return this.assessmnentStatusDTO.isInPROGRESS();
    }


    @Override
    public Object getKey() {
        return getId();
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getOtherBenefitNote() {
        return otherBenefitNote;
    }

    public void setOtherBenefitNote(String otherBenefitNote) {
        this.otherBenefitNote = otherBenefitNote;
    }

    public String getOtherIncomeNote() {
        return otherIncomeNote;
    }

    public void setOtherIncomeNote(String otherIncomeNote) {
        this.otherIncomeNote = otherIncomeNote;
    }

    public Double getTotalAggregatedIncome() {
        return totalAggregatedIncome;
    }

    public void setTotalAggregatedIncome(Double totalAggregatedIncome) {
        this.totalAggregatedIncome = totalAggregatedIncome;
    }

    public Double getAdjustedIncomeValue() {
        return adjustedIncomeValue;
    }

    public void setAdjustedIncomeValue(Double adjustedIncomeValue) {
        this.adjustedIncomeValue = adjustedIncomeValue;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public SupplierDTO getSupplierDTO() {
        return supplierDTO;
    }

    public void setSupplierDTO(SupplierDTO supplierDTO) {
        this.supplierDTO = supplierDTO;
    }

    public Collection<AssessmentSectionSummaryDTO> getSectionSummaries() {
        return sectionSummaries;
    }

    public void setSectionSummaries(Collection<AssessmentSectionSummaryDTO> sectionSummaries) {
        this.sectionSummaries = sectionSummaries;
    }

    public Collection<ChildWeightingDTO> getChildWeightings() {
        return childWeightings;
    }

    public void setChildWeightings(Collection<ChildWeightingDTO> childWeightings) {
        this.childWeightings = childWeightings;
    }

    public Double getLowerThreshold() {
        return lowerThreshold;
    }

    public void setLowerThreshold(Double lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }

    public Double getUpperThreshold() {
        return upperThreshold;
    }

    public void setUpperThreshold(Double upperThreshold) {
        this.upperThreshold = upperThreshold;
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

    public AssessmentStatusDTO getAssessmnentStatusDTO() {
        return assessmnentStatusDTO;
    }

    public void setAssessmnentStatusDTO(AssessmentStatusDTO assessmnentStatusDTO) {
        this.assessmnentStatusDTO = assessmnentStatusDTO;
    }

    public NewWorkReasonDTO getNewWorkReason() {
        return newWorkReason;
    }

    public void setNewWorkReason(NewWorkReasonDTO newWorkReason) {
        this.newWorkReason = newWorkReason;
    }

    public void setReviewType(ReviewTypeDTO reviewType) {

        this.reviewType = reviewType;

    }

    public ReviewTypeDTO getReviewType() {
        return reviewType;
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

}