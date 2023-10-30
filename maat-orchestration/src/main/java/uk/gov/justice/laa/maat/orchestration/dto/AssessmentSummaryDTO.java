package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class AssessmentSummaryDTO extends GenericDTO {
    private Long id;
    private Date assessmentDate;
    private String type;
    private String reviewType;
    private String status;
    private String result;

    public AssessmentSummaryDTO() {
        reset();
    }

    public void reset() {

        this.id = null;
        this.assessmentDate = null;
        this.type = null;
        this.reviewType = null;
        this.status = null;
        this.result = null;
    }

    @Override
    public Long getKey() {
        return getId();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }
}