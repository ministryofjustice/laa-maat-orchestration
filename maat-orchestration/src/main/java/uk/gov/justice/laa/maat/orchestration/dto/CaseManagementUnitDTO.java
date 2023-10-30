package uk.gov.justice.laa.maat.orchestration.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class CaseManagementUnitDTO extends GenericDTO implements Serializable, Comparable<CaseManagementUnitDTO> {

    private static final long serialVersionUID = 2947962979790795526L;

    private Long cmuId;
    private Long areaId;
    private Boolean enabled;
    private String code;
    private Boolean libraAccess;
    private String name;
    private String description;
    private Date timeStamp;

    public CaseManagementUnitDTO() {

    }

    @Override
    public Object getKey() {
        return cmuId;
    }

    public Long getCmuId() {
        return cmuId;
    }

    public void setCmuId(Long cmuId) {
        this.cmuId = cmuId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getLibraAccess() {
        return libraAccess;
    }

    public void setLibraAccess(Boolean libraAccess) {
        this.libraAccess = libraAccess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String toString() {
        StringBuffer op = new StringBuffer();

        op.append(" AreaCode:" + this.getCode());
        op.append(", Description:" + this.getDescription());
        op.append(", Name:" + this.getName());

        return op.toString();

    }

    public int compareTo(CaseManagementUnitDTO cmu) {
        int result = description.compareTo(cmu.description);
        return result;
    }

    public static Collection<CaseManagementUnitDTO> exampleNCTCaseWorkerCMUs() {
        // Grant 'God' access (NCT case worker) - e.g. NottingHam & Liverpool access
        Collection<CaseManagementUnitDTO> cmuDTOs = new ArrayList<CaseManagementUnitDTO>();

        CaseManagementUnitDTO nottingHamCMUDTO = exampleCMU();
        //nottingHamCMUDTO.setAreaId(47);    // National Court Team
        nottingHamCMUDTO.setCmuId(new Long(253));
        nottingHamCMUDTO.setCode("4010");
        nottingHamCMUDTO.setDescription("Nottingham");
        cmuDTOs.add(nottingHamCMUDTO);

        CaseManagementUnitDTO liverPoolCMUDTO = exampleCMU();
        //liverPoolCMUDTO.setAreaId(47);	// National Court Team
        liverPoolCMUDTO.setCmuId(new Long(254));
        liverPoolCMUDTO.setCode("4020");
        liverPoolCMUDTO.setDescription("Liverpool");

        cmuDTOs.add(liverPoolCMUDTO);

        return cmuDTOs;
    }

    public static CaseManagementUnitDTO exampleCMU() {
        CaseManagementUnitDTO caseManagementUnitDTO = new CaseManagementUnitDTO();
        //caseManagementUnitDTO.setAreaId(47);	 // National Court Team
        caseManagementUnitDTO.setCmuId(new Long(253));
        caseManagementUnitDTO.setCode("4010");
        caseManagementUnitDTO.setDescription("Nottingham");
        caseManagementUnitDTO.setEnabled(Boolean.TRUE);
        caseManagementUnitDTO.setLibraAccess(Boolean.FALSE);
        return caseManagementUnitDTO;
    }

}
