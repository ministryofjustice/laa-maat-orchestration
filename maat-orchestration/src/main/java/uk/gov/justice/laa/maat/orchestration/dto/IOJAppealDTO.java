package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class IOJAppealDTO extends GenericDTO {
    private Long iojId;
    private Long cmuId;
    private Date receivedDate;
    private Date decisionDate;
    private String appealSetUpResult;
    private String appealDecisionResult;
    private String notes;

    private IOJDecisionReasonDTO appealReason;
    private AssessmentStatusDTO assessmentStatusDTO;
    private NewWorkReasonDTO newWorkReasonDTO;

    public IOJAppealDTO() {
        reset();
    }

    public void reset() {
        this.iojId = null;
        this.cmuId = null;
        this.receivedDate = null;
        this.decisionDate = null;
        this.appealSetUpResult = "";
        this.appealDecisionResult = "";
        this.notes = "";
        this.appealReason = new IOJDecisionReasonDTO();
        this.assessmentStatusDTO = new AssessmentStatusDTO();
        this.newWorkReasonDTO = new NewWorkReasonDTO();
    }

    @Override
    public Object getKey() {
        return getIojId();
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getAppealSetUpResult() {
        return appealSetUpResult;
    }

    public void setAppealSetUpResult(String appealSetUpResult) {
        this.appealSetUpResult = appealSetUpResult;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getIojId() {
        return iojId;
    }

    public void setIojId(Long iojId) {
        this.iojId = iojId;
    }

    public Long getCmuId() {
        return cmuId;
    }

    public void setCmuId(Long cmuId) {
        this.cmuId = cmuId;
    }

    public String getAppealDecisionResult() {
        return appealDecisionResult;
    }

    public void setAppealDecisionResult(String appealDecisionResult) {
        this.appealDecisionResult = appealDecisionResult;
    }

    public AssessmentStatusDTO getAssessmentStatusDTO() {
        return assessmentStatusDTO;
    }

    public void setAssessmentStatusDTO(AssessmentStatusDTO assessmentStatusDTO) {
        this.assessmentStatusDTO = assessmentStatusDTO;
    }

    public NewWorkReasonDTO getNewWorkReasonDTO() {
        return newWorkReasonDTO;
    }

    public void setNewWorkReasonDTO(NewWorkReasonDTO newWorkReasonDTO) {
        this.newWorkReasonDTO = newWorkReasonDTO;
    }

    public IOJDecisionReasonDTO getAppealReason() {
        return appealReason;
    }

    public void setAppealReason(IOJDecisionReasonDTO appealReason) {
        this.appealReason = appealReason;
    }
}