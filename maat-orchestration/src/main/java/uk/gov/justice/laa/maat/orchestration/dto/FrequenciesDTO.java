package uk.gov.justice.laa.maat.orchestration.dto;

public class FrequenciesDTO extends GenericDTO implements Comparable<FrequenciesDTO> {

    private Long assCritId;
    private String code;
    private String description;
    private Long annualWeighting;

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

    public int compareTo(FrequenciesDTO dto) {

        return code.compareTo(dto.getCode());
    }

    public Long getAnnualWeighting() {
        return annualWeighting;
    }

    public void setAnnualWeighting(Long annualWeighting) {
        this.annualWeighting = annualWeighting;
    }

    public FrequenciesDTO() {
        this.code = "";
        this.description = "";
        this.annualWeighting = Long.valueOf(0L);
    }

    public FrequenciesDTO(String code, String description, Long annualWeighting) {
        super();
        this.code = code;
        this.description = description;
        this.annualWeighting = annualWeighting;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Cd[").append(this.code).append("] ");
        sb.append("wgt[").append(this.annualWeighting).append("] ");

        return sb.toString();
    }

    public Long getAssCritId() {
        return assCritId;
    }

    public void setAssCritId(Long assCritId) {
        this.assCritId = assCritId;
    }

}
