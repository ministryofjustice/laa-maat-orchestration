package uk.gov.justice.laa.maat.orchestration.dto;

public class FdcAdjustmentReasonDTO extends GenericDTO {

    private String code;
    private String description;

    public FdcAdjustmentReasonDTO() {
    }

    public FdcAdjustmentReasonDTO(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Object getKey() {

        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {

        StringBuffer op = new StringBuffer();
        op.append(" code[").append(code).append("] ");
        op.append("description[").append(description).append("] ");

        return op.toString();
    }

}
