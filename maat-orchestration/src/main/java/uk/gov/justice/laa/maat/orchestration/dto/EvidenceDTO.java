package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class EvidenceDTO extends GenericDTO {
    private static final long serialVersionUID = 1L;

    private Long id;
    private EvidenceTypeDTO evidenceTypeDTO;
    private String otherDescription;
    private Date dateReceived;


    public EvidenceDTO() {
        reset();
    }

    private void reset() {

        this.evidenceTypeDTO = new EvidenceTypeDTO();
    }

    @Override
    public Object getKey() {
        return getEvidenceTypeDTO();
    }

    public String getOtherDescription() {
        return otherDescription;
    }

    public void setOtherDescription(String otherDescription) {
        this.otherDescription = otherDescription;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EvidenceTypeDTO getEvidenceTypeDTO() {
        return evidenceTypeDTO;
    }

    public void setEvidenceTypeDTO(EvidenceTypeDTO evidenceTypeDTO) {
        this.evidenceTypeDTO = evidenceTypeDTO;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id[").append(this.id).append("] ");
        sb.append("EvidenceDTO[").append(evidenceTypeDTO).append("] ");
        sb.append("date[").append(dateReceived).append("] ");
        return sb.toString();
    }
}
