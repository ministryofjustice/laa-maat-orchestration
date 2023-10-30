package uk.gov.justice.laa.maat.orchestration.dto;

public class OffenceDTO extends GenericDTO {

    private String offenceType;
    private String description;
    private Double contributionCap;

    public OffenceDTO() {
        offenceType = null;
        description = null;
        contributionCap = 0.0d;
    }

    @Override
    public String getKey() {
        return getOffenceType();
    }

    public String getOffenceType() {
        return offenceType;
    }

    public void setOffenceType(String offenceType) {
        this.offenceType = offenceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getContributionCap() {
        return contributionCap;
    }

    public void setContributionCap(Double contributionCap) {
        this.contributionCap = contributionCap;
    }

}
