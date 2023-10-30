package uk.gov.justice.laa.maat.orchestration.dto;

public class HRDetailTypeDTO extends GenericDTO {

    private String type;
    private String description;

    @Override
    public Object getKey() {
        return getType();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
