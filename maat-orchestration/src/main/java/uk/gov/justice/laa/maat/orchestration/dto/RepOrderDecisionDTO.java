package uk.gov.justice.laa.maat.orchestration.dto;

public class RepOrderDecisionDTO extends GenericDTO implements Comparable<RepOrderDecisionDTO> {

    private String code;
    private SysGenString description;

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

    public SysGenString getDescription() {
        return description;
    }

    public void setDescription(SysGenString description) {
        this.description = description;
    }

    public int compareTo(RepOrderDecisionDTO dto) {

        return code.compareTo(dto.getCode());
    }

}
