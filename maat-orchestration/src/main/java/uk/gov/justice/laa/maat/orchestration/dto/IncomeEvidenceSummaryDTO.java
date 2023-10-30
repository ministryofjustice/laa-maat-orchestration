package uk.gov.justice.laa.maat.orchestration.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class IncomeEvidenceSummaryDTO extends GenericDTO {
    private static final long serialVersionUID = 1L;

    private Date evidenceDueDate;
    private Date evidenceReceivedDate;
    private Date upliftAppliedDate;
    private Date upliftRemovedDate;
    private Date firstReminderDate;
    private Date secondReminderDate;
    private String incomeEvidenceNotes;
    private Collection<EvidenceDTO> applicantIncomeEvidenceList;
    private Collection<EvidenceDTO> partnerIncomeEvidenceList;
    private Collection<ExtraEvidenceDTO> extraEvidenceList;
    private Boolean enabled;
    private Boolean upliftsAvailable;


    public IncomeEvidenceSummaryDTO() {
        reset();
    }

    public boolean isEmpty() {
        return evidenceDueDate == null &&
                evidenceReceivedDate == null &&
                upliftAppliedDate == null &&
                upliftRemovedDate == null &&
                firstReminderDate == null &&
                secondReminderDate == null &&
                StringUtils.isBlank(incomeEvidenceNotes) &&
                applicantIncomeEvidenceList.isEmpty() &&
                partnerIncomeEvidenceList.isEmpty() &&
                enabled.booleanValue() == false;

    }

    public void reset() {

        applicantIncomeEvidenceList = new ArrayList<EvidenceDTO>();
        partnerIncomeEvidenceList = new ArrayList<EvidenceDTO>();
        extraEvidenceList = new ArrayList<ExtraEvidenceDTO>();
        this.incomeEvidenceNotes = "";
        this.enabled = Boolean.FALSE;

    }

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasUpliftApplied() {
        return this.upliftAppliedDate != null;
    }

    public boolean hasUpliftRemoved() {
        return this.upliftRemovedDate != null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("IncomeEvidenceSummaryDTO: ");
        sb.append("Evid.dueDt[").append(this.evidenceDueDate).append("] ");
        sb.append("UpliftApplied [").append(this.upliftAppliedDate).append("] ");
        sb.append("UpliftRemoved [").append(this.upliftRemovedDate).append("] ");
        sb.append("enabled[").append(this.enabled).append("] ");
        sb.append("Income Evid.[");
        sb.append("ApplicantIncome Evid.[");
        for (EvidenceDTO evidenceDTO : getApplicantIncomeEvidenceList()) {
            sb.append(evidenceDTO.toString()).append(" ");
        }
        sb.append("]");
        sb.append("PartnerIncome Evid.[");
        for (EvidenceDTO evidenceDTO : getPartnerIncomeEvidenceList()) {
            sb.append(evidenceDTO.toString()).append(" ");
        }
        sb.append("]");


        return sb.toString();
    }

    public boolean hasEvidenceRequests() {
        return !(applicantIncomeEvidenceList.isEmpty() && partnerIncomeEvidenceList.isEmpty());
    }

    public Date getEvidenceDueDate() {
        return evidenceDueDate;
    }

    public void setEvidenceDueDate(Date evidenceDueDate) {
        this.evidenceDueDate = evidenceDueDate;
    }

    public Date getUpliftAppliedDate() {
        return upliftAppliedDate;
    }

    public void setUpliftAppliedDate(Date upliftAppliedDate) {
        this.upliftAppliedDate = upliftAppliedDate;
    }

    public Date getUpliftRemovedDate() {
        return upliftRemovedDate;
    }

    public void setUpliftRemovedDate(Date upliftRemovedDate) {
        this.upliftRemovedDate = upliftRemovedDate;
    }

    public Date getFirstReminderDate() {
        return firstReminderDate;
    }

    public void setFirstReminderDate(Date firstReminderDate) {
        this.firstReminderDate = firstReminderDate;
    }

    public Date getSecondReminderDate() {
        return secondReminderDate;
    }

    public void setSecondReminderDate(Date secondReminderDate) {
        this.secondReminderDate = secondReminderDate;
    }

    public Date getEvidenceReceivedDate() {
        return evidenceReceivedDate;
    }

    public void setEvidenceReceivedDate(Date evidenceReceivedDate) {
        this.evidenceReceivedDate = evidenceReceivedDate;
    }

    public EvidenceDTO obtainIncomeEvidence(long id) {

        // combine the two collections for the purpose of the search
        Collection<EvidenceDTO> combinedEvidence = getApplicantIncomeEvidenceList();
        combinedEvidence.addAll(getPartnerIncomeEvidenceList());

        EvidenceDTO dto = null;
        if (combinedEvidence != null) {
            for (Iterator<EvidenceDTO> iterator = combinedEvidence.iterator(); iterator.hasNext(); ) {
                EvidenceDTO incomeEvidenceDTO = iterator.next();
                if (incomeEvidenceDTO.getId().longValue() == id) {
                    dto = incomeEvidenceDTO;
                    break;
                }
            }
        }
        return dto;

    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getIncomeEvidenceNotes() {
        return incomeEvidenceNotes;
    }

    public void setIncomeEvidenceNotes(String incomeEvidenceNotes) {
        this.incomeEvidenceNotes = incomeEvidenceNotes;
    }

    public Collection<ExtraEvidenceDTO> getExtraEvidenceList() {
        return extraEvidenceList;
    }

    public void setExtraEvidenceList(Collection<ExtraEvidenceDTO> extraEvidenceList) {
        this.extraEvidenceList = extraEvidenceList;
    }

    public Collection<EvidenceDTO> getApplicantIncomeEvidenceList() {
        return applicantIncomeEvidenceList;
    }

    public void setApplicantIncomeEvidenceList(
            Collection<EvidenceDTO> applicantIncomeEvidenceList) {
        this.applicantIncomeEvidenceList = applicantIncomeEvidenceList;
    }

    public Collection<EvidenceDTO> getPartnerIncomeEvidenceList() {
        return partnerIncomeEvidenceList;
    }

    public void setPartnerIncomeEvidenceList(
            Collection<EvidenceDTO> partnerIncomeEvidenceList) {
        this.partnerIncomeEvidenceList = partnerIncomeEvidenceList;
    }

    public Boolean getUpliftsAvailable() {
        return upliftsAvailable;
    }

    public void setUpliftsAvailable(Boolean upliftsAvailable) {
        this.upliftsAvailable = upliftsAvailable;
    }
}

