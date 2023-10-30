package uk.gov.justice.laa.maat.orchestration.dto;

public class UserRoleDTO extends GenericDTO {

    public static final String NCT_CASEWORKER = "NCT CASEWORKER";

    private String roleName;
    private boolean enabled;

    public UserRoleDTO() {

    }

    @Override
    public Object getKey() {

        return getRoleName();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" role:[").append(this.roleName).append("]");
        sb.append(" enabled:[").append(this.enabled).append("]");
        return sb.toString();
    }

}
