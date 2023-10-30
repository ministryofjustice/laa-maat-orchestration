package uk.gov.justice.laa.maat.orchestration.dto;

public class RepStatusDTO extends GenericDTO implements Comparable<RepStatusDTO> {

    private String status;
    private String description;
    private Boolean updateAllowed;
    private Boolean removeContribs;

    @Override
    public String getKey() {
        return getStatus();
    }

    public Boolean getUpdateAllowed() {
        return updateAllowed;
    }

    public void setUpdateAllowed(Boolean updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

    public Boolean getRemoveContribs() {
        return removeContribs;
    }

    public void setRemoveContribs(Boolean removeContribs) {
        this.removeContribs = removeContribs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int compareTo(RepStatusDTO dto) {
        return status.compareTo(dto.getStatus());
    }

}
