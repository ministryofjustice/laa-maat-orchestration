package uk.gov.justice.laa.maat.orchestration.dto;

public class EvidenceFeeDTO extends GenericDTO {
    private String feeLevel;
    private SysGenString description;

    public EvidenceFeeDTO() {
    }

    public EvidenceFeeDTO(String code, SysGenString description) {
        this.feeLevel = code;
        this.description = description;
    }

    @Override
    public String getKey() {
        return getFeeLevel();
    }

    public String getFeeLevel() {
        return feeLevel;
    }

    public void setFeeLevel(String feeLevel) {
        this.feeLevel = feeLevel;
    }

    public SysGenString getDescription() {
        return description;
    }

    public void setDescription(SysGenString description) {
        this.description = description;
    }

    public static EvidenceFeeDTO example() {
        return new EvidenceFeeDTO("XXXX", new SysGenString("** FEe descr. **"));
    }

}
