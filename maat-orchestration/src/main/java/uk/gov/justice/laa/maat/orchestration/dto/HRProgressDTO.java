package uk.gov.justice.laa.maat.orchestration.dto;

import java.sql.Timestamp;
import java.util.Date;

public class HRProgressDTO extends GenericDTO {

    private Long id;
    private HRProgressActionDTO progressAction;
    private HRProgressResponseDTO progressResponse;
    private Date dateRequested;
    private Date dateRequired;
    private Date dateCompleted;
    private Timestamp timeStamp;

    public HRProgressDTO() {
        reset();
    }

    public void reset() {
        this.progressAction = new HRProgressActionDTO();
        this.progressResponse = new HRProgressResponseDTO();
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

    public HRProgressActionDTO getProgressAction() {
        return progressAction;
    }

    public void setProgressAction(HRProgressActionDTO progressAction) {
        this.progressAction = progressAction;
    }

    public HRProgressResponseDTO getProgressResponse() {
        return progressResponse;
    }

    public void setProgressResponse(HRProgressResponseDTO progressResponse) {
        this.progressResponse = progressResponse;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getDateRequired() {
        return dateRequired;
    }

    public void setDateRequired(Date dateRequired) {
        this.dateRequired = dateRequired;
    }
}
