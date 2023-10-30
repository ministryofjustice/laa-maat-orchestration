package uk.gov.justice.laa.maat.orchestration.dto;

public class IOJDecisionReasonDTO extends GenericDTO implements Comparable<IOJDecisionReasonDTO> {
    private String code;
    private String description;

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

    public int compareTo(IOJDecisionReasonDTO dto) {

        return code.compareTo(dto.getCode());
    }

}
