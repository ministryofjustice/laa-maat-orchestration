package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class DrcFileRefDTO extends GenericDTO {

    private Long contribFileId;
    private Date dateSent;
    private Date dateAcknowledged;
    private String acknowledgeCode;


    @Override
    public Object getKey() {

        return contribFileId;
    }

    public Long getContribFileId() {
        return contribFileId;
    }

    public void setContribFileId(Long contribFileId) {
        this.contribFileId = contribFileId;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getDateAcknowledged() {
        return dateAcknowledged;
    }

    public void setDateAcknowledged(Date dateAcknowledged) {
        this.dateAcknowledged = dateAcknowledged;
    }

    public String toString() {

        StringBuffer op = new StringBuffer();
        op.append("contribFileId[").append(this.contribFileId).append("] ");
        op.append("dateSent[").append(this.dateSent).append("] ");
        op.append("dateAcknowledged[").append(this.dateAcknowledged).append("] ");
        op.append("acknowledgeCode[").append(this.acknowledgeCode).append("] ");

        return op.toString();
    }

    public String getAcknowledgeCode() {
        return acknowledgeCode;
    }

    public void setAcknowledgeCode(String acknowledgeCode) {
        this.acknowledgeCode = acknowledgeCode;
    }

}
