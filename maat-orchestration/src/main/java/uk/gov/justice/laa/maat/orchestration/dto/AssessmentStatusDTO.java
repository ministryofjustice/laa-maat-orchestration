package uk.gov.justice.laa.maat.orchestration.dto;

import org.apache.commons.lang3.StringUtils;

public class AssessmentStatusDTO extends GenericDTO {

    public static final String COMPLETE = "COMPLETE";
    public static final String INCOMPLETE = "IN PROGRESS";
    public static final String EVIDENCE = "EVIDENCE";

    private String status;
    private String description;

    public AssessmentStatusDTO() {
    }

    public AssessmentStatusDTO(String code, String description) {
        this.status = code;
        this.description = description;
    }

    @Override
    public String getKey() {
        return getStatus();
    }

    public boolean isComplete() {
        return StringUtils.equals(AssessmentStatusDTO.COMPLETE, getStatus());
    }

    public boolean isInPROGRESS() {
        return StringUtils.equals(AssessmentStatusDTO.INCOMPLETE, getStatus());
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static AssessmentStatusDTO example() {
        return new AssessmentStatusDTO("PASS", "??");
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Status[").append(this.status).append("]");
        return sb.toString();
    }
}
