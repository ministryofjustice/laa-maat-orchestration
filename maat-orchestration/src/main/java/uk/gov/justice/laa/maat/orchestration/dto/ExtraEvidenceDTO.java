package uk.gov.justice.laa.maat.orchestration.dto;

public class ExtraEvidenceDTO extends EvidenceDTO {

    private String otherText;
    private Boolean mandatory;
    private String adhoc;        // valuesnull, A for applicant P for partner

    public ExtraEvidenceDTO() {
        reset();
    }

    private void reset() {
        setEvidenceTypeDTO(new EvidenceTypeDTO());
        otherText = null;
        mandatory = null;
        adhoc = null;

    }

    public void setAdhoc(String adhoc) {
        this.adhoc = adhoc;
    }

    public String getAdhoc() {
        return this.adhoc;
    }

    public void setOtherText(String otherText) {
        this.otherText = otherText;
    }

    public String getOtherText() {
        return this.otherText;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Boolean getMandatory() {
        return this.mandatory;
    }

}
