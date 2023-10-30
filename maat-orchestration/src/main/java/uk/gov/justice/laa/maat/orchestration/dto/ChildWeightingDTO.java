package uk.gov.justice.laa.maat.orchestration.dto;

public class ChildWeightingDTO extends GenericDTO {

    private Long id;
    private Long weightingId;
    private Integer lowerAgeRange;
    private Integer upperAgeRange;
    private Double weightingFactor;
    private Integer noOfChildren;

    public ChildWeightingDTO() {

    }

    public ChildWeightingDTO(AssessmentCriteriaChildWeightingDTO dto) {
        setWeightingId(dto.getId());
    }

    public ChildWeightingDTO(Long newWeightingId, Integer newNoOfChildren) {

        weightingId = newWeightingId;
        noOfChildren = newNoOfChildren;

    }

    public String toString() {
        StringBuffer op = new StringBuffer(128);
        op.append("ChildWeightingDTO - ");
        op.append("[").append(toDisplayString()).append("] ");
        op.append("weightingId[").append(weightingId).append("] ");
        op.append("noOfChildren[").append(noOfChildren).append("] ");

        return op.toString();

    }

    public String toDisplayString() {
        StringBuilder sb = new StringBuilder(40);
        sb.append("Number of Children aged ").append(this.lowerAgeRange).append(" - ").append(this.upperAgeRange);
        return sb.toString();
    }

    @Override
    public Object getKey() {
        return getId();
    }

    public Integer getNoOfChildren() {
        return noOfChildren;
    }

    public void setNoOfChildren(Integer newNoOfChildren) {
        this.noOfChildren = newNoOfChildren;
    }

    public Long getWeightingId() {
        return weightingId;
    }

    public void setWeightingId(Long newWeightingId) {
        this.weightingId = newWeightingId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLowerAgeRange() {
        return lowerAgeRange;
    }

    public void setLowerAgeRange(Integer lowerAgeRange) {
        this.lowerAgeRange = lowerAgeRange;
    }

    public Integer getUpperAgeRange() {
        return upperAgeRange;
    }

    public void setUpperAgeRange(Integer upperAgeRange) {
        this.upperAgeRange = upperAgeRange;
    }

    public Double getWeightingFactor() {
        return weightingFactor;
    }

    public void setWeightingFactor(Double weightingFactor) {
        this.weightingFactor = weightingFactor;
    }


}
