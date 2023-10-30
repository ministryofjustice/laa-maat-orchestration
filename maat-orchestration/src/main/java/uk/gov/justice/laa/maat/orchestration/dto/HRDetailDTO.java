package uk.gov.justice.laa.maat.orchestration.dto;

import java.sql.Timestamp;
import java.util.Date;

public class HRDetailDTO extends GenericDTO {

    private Long id;
    private FrequenciesDTO frequency;
    private Date dateReceived;
    private HRDetailDescriptionDTO detailDescription;
    private String otherDescription;
    private Currency amountNumber;
    private Date dateDue;
    private boolean accepted;
    private HRReasonDTO reason;
    private Timestamp timeStamp;
    private String hrReasonNote;

    public HRDetailDTO() {
        reset();
    }

    public void reset() {
        this.frequency = new FrequenciesDTO();
        this.detailDescription = new HRDetailDescriptionDTO();
        this.reason = new HRReasonDTO();
    }

    @Override
    public Object getKey() {
        return getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FrequenciesDTO getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequenciesDTO frequency) {
        this.frequency = frequency;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public HRDetailDescriptionDTO getDetailDescription() {
        return detailDescription;
    }

    public void setDetailDescription(HRDetailDescriptionDTO detailDescription) {
        this.detailDescription = detailDescription;
    }

    public String getOtherDescription() {
        return otherDescription;
    }

    public void setOtherDescription(String otherDescription) {
        this.otherDescription = otherDescription;
    }

    public Currency getAmountNumber() {
        return amountNumber;
    }

    public void setAmountNumber(Currency amountNumber) {
        this.amountNumber = amountNumber;
    }

    public Date getDateDue() {
        return dateDue;
    }

    public void setDateDue(Date dateDue) {
        this.dateDue = dateDue;
    }

    public boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public HRReasonDTO getReason() {
        return reason;
    }

    public void setReason(HRReasonDTO reason) {
        this.reason = reason;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHrReasonNote() {
        return hrReasonNote;
    }

    public void setHrReasonNote(String hrReasonNote) {
        this.hrReasonNote = hrReasonNote;
    }

}
