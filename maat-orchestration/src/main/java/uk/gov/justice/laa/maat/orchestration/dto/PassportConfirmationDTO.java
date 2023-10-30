package uk.gov.justice.laa.maat.orchestration.dto;

public class PassportConfirmationDTO extends GenericDTO {

    private String confirmation;
    private String description;


    @Override
    public Object getKey() {
        return getConfirmation();
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
