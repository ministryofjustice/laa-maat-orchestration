package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.*;

public class AreaDTO extends GenericDTO implements Comparable<AreaDTO> {

    private static final long serialVersionUID = 178738522671686929L;

    private Long areaId;
    private String code;
    private String description;
    private boolean enabled = false;
    private Date timeStamp;

    private Collection<CaseManagementUnitDTO> caseManagementUnits;


    public AreaDTO() {
        super();
        reset();
    }

    public void reset() {
        setCaseManagementUnits(new HashSet<>());
    }

    @Override
    public Long getKey() {

        return areaId;
    }

    public Collection<CaseManagementUnitDTO> getCmuDTO() {
        return caseManagementUnits;
    }

    public void setCmuDTO(Collection<CaseManagementUnitDTO> cmuDTO) {
        this.caseManagementUnits = cmuDTO;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<CaseManagementUnitDTO> getCaseManagementUnits() {
        ArrayList<CaseManagementUnitDTO> cmus = new ArrayList<>(this.caseManagementUnits);
        return cmus;
    }

    public void setCaseManagementUnits(
            Collection<CaseManagementUnitDTO> caseManagementUnits) {
        this.caseManagementUnits = caseManagementUnits;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        StringBuffer op = new StringBuffer();

        op.append(" AreaCode -     " + this.getCode());
        op.append(" AreaName -     " + this.getDescription());

        if (this.getCaseManagementUnits() != null) {
            op.append("    No. of CMUs -  " + this.getCaseManagementUnits().size());
            Iterator<CaseManagementUnitDTO> it = this.getCaseManagementUnits().iterator();

            while (it.hasNext()) {
                CaseManagementUnitDTO cmuDTO = it.next();
                op.append(cmuDTO.toString());
            }
        }

        return op.toString();
    }

    public int compareTo(AreaDTO area) {
        int result = description.compareTo(area.description);
        return result;

    }

}
