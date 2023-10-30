package uk.gov.justice.laa.maat.orchestration.dto;

public class CaseTypeCriteriaDetailDTO extends GenericDTO {

    private String catyCaseType;
    private Long id;
    private Double applicantValue;
    private Double partnerValue;
    private String applicantFreq;
    private String partnerFreq;


    public CaseTypeCriteriaDetailDTO() {
        reset();
    }

    private void reset() {
        this.applicantValue = Double.valueOf(0.0d);
        this.applicantFreq = "";
        this.partnerValue = Double.valueOf(0.0d);
        this.partnerFreq = "";
    }

    @Override
    public Long getKey() {
        return getId();
    }


    public String toString() {
        StringBuilder op = new StringBuilder();
        op.append("CaseTypeCriteria: ");
        op.append("id[").append(this.id).append("], ");
        op.append("caseType[").append(this.catyCaseType).append("], ");
        op.append("applVal[").append(this.applicantValue).append("], ");
        op.append("applFrqCd[").append(this.applicantFreq).append("], ");
        op.append("partVal[").append(this.partnerValue).append("], ");
        op.append("partFrqCd[").append(this.partnerFreq).append("]");
        return op.toString();
    }

    public String getCatyCaseType() {
        return catyCaseType;
    }

    public void setCatyCaseType(String catyCaseType) {
        this.catyCaseType = catyCaseType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getApplicantValue() {
        return applicantValue;
    }

    public void setApplicantValue(Double applicantValue) {
        this.applicantValue = applicantValue;
    }

    public Double getPartnerValue() {
        return partnerValue;
    }

    public void setPartnerValue(Double partnerValue) {
        this.partnerValue = partnerValue;
    }

    public String getApplicantFreq() {
        return applicantFreq;
    }

    public void setApplicantFreq(String applicantFreq) {
        this.applicantFreq = applicantFreq;
    }

    public String getPartnerFreq() {
        return partnerFreq;
    }

    public void setPartnerFreq(String partnerFreq) {
        this.partnerFreq = partnerFreq;
    }

}
