package uk.gov.justice.laa.maat.orchestration.dto;

public class AssessmentCriteriaChildWeightingDTO extends GenericDTO implements Comparable<AssessmentCriteriaChildWeightingDTO> {

    private Long id;
    private Long assessmentCriteriaId;
    private Integer lowerAge;
    private Integer upperAge;
    private Double weightingFactor;


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

    public Long getAssessmentCriteriaId() {
        return assessmentCriteriaId;
    }

    public void setAssessmentCriteriaId(Long assessmentCriteriaId) {
        this.assessmentCriteriaId = assessmentCriteriaId;
    }

    public Integer getLowerAge() {
        return lowerAge;
    }

    public void setLowerAge(Integer lowerAge) {
        this.lowerAge = lowerAge;
    }

    public Integer getUpperAge() {
        return upperAge;
    }

    public void setUpperAge(Integer upperAge) {
        this.upperAge = upperAge;
    }

    public String toString() {
        StringBuffer op = new StringBuffer();

        op.append(" AssCritId - " + this.getAssessmentCriteriaId());
        op.append(" id -  " + this.getId());
        op.append(" lower age -  " + this.getLowerAge());
        op.append(" upper age -  " + this.getUpperAge());

        return op.toString();
    }


    public int compareTo(AssessmentCriteriaChildWeightingDTO o) {
        return lowerAge.compareTo(o.getLowerAge());
    }

    public Double getWeightingFactor() {
        return weightingFactor;
    }

    public void setWeightingFactor(Double weightingFactor) {
        this.weightingFactor = weightingFactor;
    }

}
