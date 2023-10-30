package uk.gov.justice.laa.maat.orchestration.dto;

public class HRProgressActionDTO extends GenericDTO {

    private String action;
    private String description;

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
