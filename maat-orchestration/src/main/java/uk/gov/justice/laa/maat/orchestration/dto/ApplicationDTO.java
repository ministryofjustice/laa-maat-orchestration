package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class ApplicationDTO extends GenericDTO {
    private Long repId;
    private Long areaId;
    private String caseId;
    private String arrestSummonsNo;
    private String statusReason;
    private Date dateCreated;
    private Date dateReceived;
    private Date dateOfSignature;
    private Date committalDate;
    private SysGenDate magsCourtOutcomeDate;
    private Date magsWithdrawalDate;
    private SysGenDate dateStatusSet;
    private Date dateStatusDue;
    private Date decisionDate;
    private Date dateStamp;
    private Date hearingDate;
    private AssessmentDTO assessmentDTO;
    private CaseManagementUnitDTO caseManagementUnitDTO;
    private CrownCourtOverviewDTO crownCourtOverviewDTO;
    private MagsCourtDTO magsCourtDTO;
    private OutcomeDTO magsOutcomeDTO;
    private OffenceDTO offenceDTO;
    private PassportedDTO passportedDTO;
    private RepOrderDecisionDTO repOrderDecision;
    private RepStatusDTO statusDTO;
    private SupplierDTO supplierDTO;
    private String transactionId;
    private Boolean applicantHasPartner;
    private boolean welshCorrepondence;
    private String iojResult;
    private String iojResultNote;
    private String solicitorName;
    private String solicitorEmail;
    private String solicitorAdminEmail;
    private Collection<AssessmentSummaryDTO> assessmentSummary;
    private Collection<FdcContributionDTO> fdcContributions;
    private boolean courtCustody;
    private boolean retrial;
    private boolean messageDisplayed;
    private String alertMessage;
    private Long usn;

    public ApplicationDTO() {
        reset();
    }

    public void reset() {
        this.repId = null;
        this.caseId = null;
        this.usn = null;
        this.arrestSummonsNo = null;
        this.statusReason = null;
        this.iojResultNote = null;
        this.solicitorName = null;
        this.solicitorEmail = null;
        this.solicitorAdminEmail = null;
        this.areaId = null;
        this.dateReceived = null;
        this.dateOfSignature = null;
        this.dateStatusSet = null;
        this.committalDate = null;
        this.dateStamp = null;
        this.magsCourtOutcomeDate = null;
        this.magsWithdrawalDate = null;
        this.courtCustody = false;
        this.retrial = false;
        this.messageDisplayed = false;
        this.statusDTO = new RepStatusDTO();
        this.offenceDTO = new OffenceDTO();
        this.repOrderDecision = new RepOrderDecisionDTO();
        this.magsCourtDTO = new MagsCourtDTO();
        this.magsOutcomeDTO = new OutcomeDTO();
        this.supplierDTO = new SupplierDTO();
        this.assessmentDTO = new AssessmentDTO();
        this.assessmentSummary = new ArrayList<>();
        this.crownCourtOverviewDTO = new CrownCourtOverviewDTO();
        this.passportedDTO = new PassportedDTO();
        this.fdcContributions = new ArrayList<>();
        this.transactionId = null;
    }

    @Override
    public Long getKey() {
        return getRepId();
    }

    public String getSolicitorOfficeId() {
        return this.supplierDTO.getAccountNumber();
    }

    public String toString() {
        StringBuffer op = new StringBuffer("ApplicationDTO - ");
        op.append(" repId = " + getRepId());
        op.append(" caseId " + getCaseId());
        op.append(" status = " + getStatusReason());

        return op.toString();
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getArrestSummonsNo() {
        return arrestSummonsNo;
    }

    public void setArrestSummonsNo(String arrestSummonsNo) {
        this.arrestSummonsNo = arrestSummonsNo;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public SysGenDate getDateStatusSet() {
        return dateStatusSet;
    }

    public void setDateStatusSet(SysGenDate dateStatusSet) {
        this.dateStatusSet = dateStatusSet;
    }

    public Date getCommittalDate() {
        return committalDate;
    }

    public void setCommittalDate(Date committalDate) {
        this.committalDate = committalDate;
    }

    public SysGenDate getMagsCourtOutcomeDate() {
        return magsCourtOutcomeDate;
    }

    public void setMagsCourtOutcomeDate(SysGenDate magsCourtOutcomeDate) {
        this.magsCourtOutcomeDate = magsCourtOutcomeDate;
    }

    public OffenceDTO getOffenceDTO() {
        return offenceDTO;
    }

    public void setOffenceDTO(OffenceDTO offenceDTO) {
        this.offenceDTO = offenceDTO;
    }

    public MagsCourtDTO getMagsCourtDTO() {
        return magsCourtDTO;
    }

    public void setMagsCourtDTO(MagsCourtDTO magsCourtDTO) {
        this.magsCourtDTO = magsCourtDTO;
    }

    public SupplierDTO getSupplierDTO() {
        return supplierDTO;
    }

    public void setSupplierDTO(SupplierDTO supplierDTO) {
        this.supplierDTO = supplierDTO;
    }

    public AssessmentDTO getAssessmentDTO() {
        return assessmentDTO;
    }

    public void setAssessmentDTO(AssessmentDTO assessmentDTO) {
        this.assessmentDTO = assessmentDTO;
    }

    public CrownCourtOverviewDTO getCrownCourtOverviewDTO() {
        return crownCourtOverviewDTO;
    }

    public void setCrownCourtOverviewDTO(CrownCourtOverviewDTO crownCourtOverviewDTO) {
        this.crownCourtOverviewDTO = crownCourtOverviewDTO;
    }

    public Collection<AssessmentSummaryDTO> getAssessmentSummary() {
        return assessmentSummary;
    }

    public void setAssessmentSummary(
            Collection<AssessmentSummaryDTO> assessmentSummary) {
        this.assessmentSummary = assessmentSummary;
    }

    public Long getRepId() {
        return repId;
    }

    public void setRepId(Long repId) {
        this.repId = repId;
    }

    public RepStatusDTO getStatusDTO() {
        return statusDTO;
    }

    public void setStatusDTO(RepStatusDTO repStatus) {
        this.statusDTO = repStatus;
    }

    public PassportedDTO getPassportedDTO() {
        return passportedDTO;
    }

    public void setPassportedDTO(PassportedDTO passportedDTO) {
        this.passportedDTO = passportedDTO;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public boolean isCourtCustody() {
        return courtCustody;
    }

    public void setCourtCustody(boolean courtCustody) {
        this.courtCustody = courtCustody;
    }

    public boolean isRetrial() {
        return retrial;
    }

    public void setRetrial(boolean retrial) {
        this.retrial = retrial;
    }

    public Date getMagsWithdrawalDate() {
        return magsWithdrawalDate;
    }

    public void setMagsWithdrawalDate(Date magsWithdrawalDate) {
        this.magsWithdrawalDate = magsWithdrawalDate;
    }

    public CaseManagementUnitDTO getCaseManagementUnitDTO() {
        return caseManagementUnitDTO;
    }

    public void setCaseManagementUnitDTO(CaseManagementUnitDTO caseManagementUnitDTO) {
        this.caseManagementUnitDTO = caseManagementUnitDTO;
    }

    public Date getDateStatusDue() {
        return dateStatusDue;
    }

    public void setDateStatusDue(Date dateStatusDue) {
        this.dateStatusDue = dateStatusDue;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public RepOrderDecisionDTO getRepOrderDecision() {
        return repOrderDecision;
    }

    public void setRepOrderDecision(RepOrderDecisionDTO repOrderDecision) {
        this.repOrderDecision = repOrderDecision;
    }

    public boolean getWelshCorrepondence() {
        return welshCorrepondence;
    }

    public void setWelshCorrepondence(boolean welshCorrepondence) {
        this.welshCorrepondence = welshCorrepondence;
    }

    public String getIojResult() {
        return iojResult;
    }

    public void setIojResult(String iojResult) {
        this.iojResult = iojResult;
    }

    public static ApplicationDTO example() {
        // TODO Auto-generated method stub
        return null;
    }

    public OutcomeDTO getMagsOutcomeDTO() {
        return magsOutcomeDTO;
    }

    public void setMagsOutcomeDTO(OutcomeDTO magsOutcomeDTO) {
        this.magsOutcomeDTO = magsOutcomeDTO;
    }

    public boolean hasPartner() {
        return getApplicantHasPartner() != null ? getApplicantHasPartner() : false;
    }

    public Boolean getApplicantHasPartner() {
        return applicantHasPartner;
    }

    public void setApplicantHasPartner(Boolean applicantHasPartner) {
        this.applicantHasPartner = applicantHasPartner;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public Collection<FdcContributionDTO> getFdcContributions() {
        return fdcContributions;
    }

    public void setFdcContributions(Collection<FdcContributionDTO> fdcContributions) {
        this.fdcContributions = fdcContributions;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateOfSignature() {
        return dateOfSignature;
    }

    public void setDateOfSignature(Date dateOfSignature) {
        this.dateOfSignature = dateOfSignature;
    }

    public Long getUsn() {
        return usn;
    }

    public void setUsn(Long usn) {
        this.usn = usn;
    }

    public Date getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(Date dateStamp) {
        this.dateStamp = dateStamp;
    }

    public Date getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(Date hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getIojResultNote() {
        return iojResultNote;
    }

    public void setIojResultNote(String iojResultNote) {
        this.iojResultNote = iojResultNote;
    }

    public String getSolicitorName() {
        return solicitorName;
    }

    public void setSolicitorName(String solicitorName) {
        this.solicitorName = solicitorName;
    }

    public boolean isMessageDisplayed() {
        return messageDisplayed;
    }

    public void setMessageDisplayed(boolean messageDisplayed) {
        this.messageDisplayed = messageDisplayed;
    }

    public String getSolicitorEmail() {
        return solicitorEmail;
    }

    public void setSolicitorEmail(String solicitorEmail) {
        this.solicitorEmail = solicitorEmail;
    }

    public String getSolicitorAdminEmail() {
        return solicitorAdminEmail;
    }

    public void setSolicitorAdminEmail(String solicitorAdminEmail) {
        this.solicitorAdminEmail = solicitorAdminEmail;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
