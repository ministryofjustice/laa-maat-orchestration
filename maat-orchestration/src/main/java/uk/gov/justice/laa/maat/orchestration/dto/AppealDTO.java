package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class AppealDTO extends GenericDTO {

    private Boolean available;

    private Date appealReceivedDate;
    private Date appealSentenceOrderDate;
    private Date appealSentOrderDateSet;

    private AppealTypeDTO appealTypeDTO;

    public AppealDTO() {

        reset();
    }

    private void reset() {

        this.appealReceivedDate = null;
        this.appealSentenceOrderDate = null;
        this.appealSentOrderDateSet = null;

        this.appealTypeDTO = new AppealTypeDTO();

    }

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Date getAppealReceivedDate() {
        return appealReceivedDate;
    }

    public void setAppealReceivedDate(Date appealReceivedDate) {
        this.appealReceivedDate = appealReceivedDate;
    }

    public Date getAppealSentenceOrderDate() {
        return appealSentenceOrderDate;
    }

    public void setAppealSentenceOrderDate(Date appealSentenceOrderDate) {
        this.appealSentenceOrderDate = appealSentenceOrderDate;
    }

    public AppealTypeDTO getAppealTypeDTO() {
        return appealTypeDTO;
    }

    public void setAppealTypeDTO(AppealTypeDTO appealTypeDTO) {
        this.appealTypeDTO = appealTypeDTO;
    }

    public Date getAppealSentOrderDateSet() {
        return appealSentOrderDateSet;
    }

    public void setAppealSentOrderDateSet(Date appealSentOrderDateSet) {
        this.appealSentOrderDateSet = appealSentOrderDateSet;
    }
}
