package uk.gov.justice.laa.maat.orchestration.dto;

public class HRProgressResponseDTO extends GenericDTO {

    private String response;
    private String description;

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
