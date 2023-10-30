package uk.gov.justice.laa.maat.orchestration.dto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class UserDTO extends GenericDTO {

    private static final Log log = LogFactory.getLog(UserDTO.class);

    private String userName;
    private String firstName;
    private String initials;
    private String surname;
    private boolean enabled;
    private String password;
    private Date passwordExpiry;
    private Long lockedRepOrderId;
    private boolean loggedIn;
    private int loggingInAttempts;
    private AreaDTO areaDTO;
    private String userSession;
    private String sessionName;
    private String appName;
    private String appServer;
    private Collection<UserRoleDTO> userRoles;
    private CaseManagementUnitDTO selectedCMUDTO;
    private boolean locked;

    public CaseManagementUnitDTO getSelectedCMUDTO() {
        return selectedCMUDTO;
    }

    public void setSelectedCMUDTO(CaseManagementUnitDTO selectedCMUDTO) {
        this.selectedCMUDTO = selectedCMUDTO;
    }

    public UserDTO() {
        reset();
    }

    public void reset() {
        userRoles = new HashSet<>();
        setAreaDTO(new AreaDTO());                // set to an empty dto
    }

    @Override
    public Object getKey() {
        return getUserName();
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getPasswordExpiry() {
        return passwordExpiry;
    }

    public void setPasswordExpiry(Date passwordExpiry) {
        this.passwordExpiry = passwordExpiry;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public int getLoggingInAttempts() {
        return loggingInAttempts;
    }

    public void setLoggingInAttempts(int loggingInAttempts) {
        this.loggingInAttempts = loggingInAttempts;
    }

    public Long getAreaId() {
        return areaDTO.getAreaId();
    }

    public void setAreaId(Long areaId) {
        this.areaDTO.setAreaId(areaId);
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppServer() {
        return appServer;
    }

    public void setAppServer(String appServer) {
        this.appServer = appServer;
    }

    public Collection<UserRoleDTO> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Collection<UserRoleDTO> uRoles) {
        this.userRoles = uRoles;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public AreaDTO getAreaDTO() {
        return areaDTO;
    }

    public void setAreaDTO(AreaDTO areaDTO) {
        this.areaDTO = areaDTO;
    }

    @Override
    public String toString() {
        StringBuffer op = new StringBuffer();

        op.append(" FirstName -     	" + this.getFirstName());
        op.append(" Surname -       	" + this.getSurname());
        op.append(" userName -       	" + this.getUserName());
        op.append(" password -       	" + this.getPassword());
        op.append(" passwordEpiry -   	" + this.getPasswordExpiry());
        op.append(" SessionName -     	" + this.getSessionName());
        op.append(" AppName -	     	" + this.getAppName());
        op.append(" AppServer -     	" + this.getAppServer());

        if (this.getAreaDTO() != null) {
            op.append("AreaDTO -     " + this.getAreaDTO().toString());
        }
        return op.toString();
    }

    public Long getLockedRepOrderId() {
        return lockedRepOrderId;
    }

    public void setLockedRepOrderId(Long lockedRepOrderId) {
        this.lockedRepOrderId = lockedRepOrderId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}
