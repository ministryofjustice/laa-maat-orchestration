package uk.gov.justice.laa.maat.orchestration.dto;

public class MagsCourtDTO extends GenericDTO {

    private String court;
    private String description;
    private Boolean wales;


    @Override
    public String getKey() {
        return getCourt();
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getWales() {
        return wales;
    }

    public void setWales(Boolean wales) {
        this.wales = wales;
    }

}
