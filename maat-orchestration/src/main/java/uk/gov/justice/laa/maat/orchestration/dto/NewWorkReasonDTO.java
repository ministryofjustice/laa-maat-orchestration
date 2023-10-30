package uk.gov.justice.laa.maat.orchestration.dto;

public class NewWorkReasonDTO extends GenericDTO implements Comparable<NewWorkReasonDTO> {

    private String code;
    private String description;
    private String type;


    public NewWorkReasonDTO() {
    }

    public String toString() {
        StringBuffer op = new StringBuffer();
        op.append(" code = " + code);
        op.append(" description = " + description);
        op.append(" type = " + type);
        return op.toString();
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

    public int compareTo(NewWorkReasonDTO dto) {

        return code.compareTo(dto.getCode());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
