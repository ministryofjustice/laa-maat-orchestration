package uk.gov.justice.laa.maat.orchestration.dto;


import java.util.Date;

public class JobSeekerDTO extends GenericDTO {
    private Boolean isJobSeeker;
    private Date lastSignedOn;

    public JobSeekerDTO() {
        reset();
    }


    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public Boolean getIsJobSeeker() {
        return isJobSeeker;
    }

    public void setIsJobSeeker(Boolean isJobSeeker) {
        this.isJobSeeker = isJobSeeker;
    }

    public Date getLastSignedOn() {
        return lastSignedOn;
    }

    public void setLastSignedOn(Date lastSignedOn) {
        this.lastSignedOn = lastSignedOn;
    }

    public void reset() {

        this.lastSignedOn = null;
        this.isJobSeeker = false;
    }

}