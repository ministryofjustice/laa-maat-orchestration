package uk.gov.justice.laa.maat.orchestration.dto;

public class HRDetailDescriptionDTO extends GenericDTO {

    private String code;
    private String description;

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
