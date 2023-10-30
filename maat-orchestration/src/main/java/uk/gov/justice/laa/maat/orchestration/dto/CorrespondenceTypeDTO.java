package uk.gov.justice.laa.maat.orchestration.dto;

public class CorrespondenceTypeDTO extends GenericDTO {

    private String correspondenceType;
    private String description;

    public CorrespondenceTypeDTO() {
    }

    public CorrespondenceTypeDTO(String code, String description) {
        this.correspondenceType = code;
        this.description = description;
    }

    public Object getKey() {
        return null;
    }

    public String toString() {
        StringBuffer op = new StringBuffer();
        op.append("type = " + correspondenceType);
        op.append(" description = " + description);
        return op.toString();
    }

    public String getCorrespondenceType() {
        return correspondenceType;
    }

    public void setCorrespondenceType(String correspondenceType) {
        this.correspondenceType = correspondenceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static CorrespondenceTypeDTO example() {
        return new CorrespondenceTypeDTO("aaa", "AAAAAAA");

    }

}
