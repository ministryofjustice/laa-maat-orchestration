package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class OutcomeDTO extends GenericDTO {
    private String outcome;
    private String description;
    private String outComeType;
    private Date dateSet;

    public static final String COMPLETE = "COMPLETE";

    public OutcomeDTO() {
    }

    public OutcomeDTO(String code, String description) {
        this.outcome = code;
        this.description = description;

    }

    public OutcomeDTO(String code, String description, String outcomeType) {
        this.outcome = code;
        this.description = description;
        this.outComeType = outcomeType;
    }


    public String toString() {
        return getOutcome() + " >>> " + getDescription() + " >>> " + getOutComeType();
    }

    @Override
    public Object getKey() {
        return getOutcome();
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateSet() {
        return dateSet;
    }

    public void setDateSet(Date dateSet) {
        this.dateSet = dateSet;
    }

    public String getOutComeType() {
        return outComeType;
    }

    public void setOutComeType(String outComeType) {
        this.outComeType = outComeType;
    }

}
