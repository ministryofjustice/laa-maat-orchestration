package uk.gov.justice.laa.maat.orchestration.dto;

public class AppealTypeDTO extends GenericDTO {

    private String code;        // key field
    private String description;

    @Override
    public String getKey() {
        return getCode();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Code[").append(getKey()).append("]-[").append(getDescription()).append("]");
        return sb.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
