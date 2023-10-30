package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class ContributionSummaryDTO extends GenericDTO {
    private Long id;
    private Double monthlyContribs;
    private Double upfrontContribs;
    private String basedOn;
    private Boolean upliftApplied;
    private Date effectiveDate;
    private Date calcDate;
    private String fileName;
    private Date dateFileSent;
    private Date dateFileReceived;

    @Override
    public Long getKey() {
        return getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMonthlyContribs() {
        return monthlyContribs;
    }

    public void setMonthlyContribs(Double monthlyContribs) {
        this.monthlyContribs = monthlyContribs;
    }

    public Double getUpfrontContribs() {
        return upfrontContribs;
    }

    public void setUpfrontContribs(Double upfrontContribs) {
        this.upfrontContribs = upfrontContribs;
    }

    public String getBasedOn() {
        return basedOn;
    }

    public void setBasedOn(String basedOn) {
        this.basedOn = basedOn;
    }

    public Boolean getUpliftApplied() {
        return upliftApplied;
    }

    public void setUpliftApplied(Boolean upliftApplied) {
        this.upliftApplied = upliftApplied;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getCalcDate() {
        return calcDate;
    }

    public void setCalcDate(Date calcDate) {
        this.calcDate = calcDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getDateFileSent() {
        return dateFileSent;
    }

    public void setDateFileSent(Date dateFileSent) {
        this.dateFileSent = dateFileSent;
    }

    public Date getDateFileReceived() {
        return dateFileReceived;
    }

    public void setDateFileReceived(Date dateFileReceived) {
        this.dateFileReceived = dateFileReceived;
    }

}
