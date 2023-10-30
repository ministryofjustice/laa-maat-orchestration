package uk.gov.justice.laa.maat.orchestration.dto;

public class EvidenceTypeDTO extends GenericDTO {

    private String evidence;        // key field
    private String description;

    @Override
    public String getKey() {
        return getEvidence();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Cd[").append(getKey()).append("]-[").append(getDescription()).append("] ");
        return sb.toString();
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
