package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class PassportedDTO extends GenericDTO {
    private static final long serialVersionUID = 1L;

    private Long passportedId;
    private Long cmuId;
    private Long usn;

    private Date date;

    private AssessmentStatusDTO assessementStatusDTO;
    private PassportConfirmationDTO passportConfirmationDTO;
    private NewWorkReasonDTO newWorkReason;
    private ReviewTypeDTO reviewType;

    private String dwpResult;

    private Boolean benefitIncomeSupport;
    private JobSeekerDTO benefitJobSeeker;
    private Boolean benefitGaurenteedStatePension;
    private Boolean benefitClaimedByPartner;
    private Boolean benefitEmploymentSupport;
    private PartnerDTO partnerDetails;
    private String notes;
    private String result;
    private Boolean under18HeardYouthCourt;
    private Boolean under18HeardMagsCourt;
    private Boolean under18FullEducation;
    private Boolean under16;
    private Boolean between1617;

    private IncomeEvidenceSummaryDTO passportSummaryEvidenceDTO;

    private String whoDwpChecked;

    public PassportedDTO() {
        reset();
    }

    public void reset() {
        this.passportedId = null;
        this.usn = null;
        this.date = null;
        this.assessementStatusDTO = new AssessmentStatusDTO();
        this.passportConfirmationDTO = new PassportConfirmationDTO();
        this.newWorkReason = new NewWorkReasonDTO();
        this.reviewType = new ReviewTypeDTO();
        this.benefitJobSeeker = new JobSeekerDTO();
        this.partnerDetails = new PartnerDTO();
        this.notes = "";
        this.benefitClaimedByPartner = false;
        this.benefitEmploymentSupport = false;
        this.benefitGaurenteedStatePension = false;
        this.benefitIncomeSupport = false;
        this.under18HeardMagsCourt = false;
        this.under18HeardYouthCourt = false;
        this.passportSummaryEvidenceDTO = new IncomeEvidenceSummaryDTO();

    }

    @Override
    public Object getKey() {
        return getPassportedId();
    }

    public Boolean isEmptyEvidence() {
        return this.passportSummaryEvidenceDTO.isEmpty();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getBenefitIncomeSupport() {
        return benefitIncomeSupport;
    }

    public void setBenefitIncomeSupport(Boolean benefitIncomeSupport) {
        this.benefitIncomeSupport = benefitIncomeSupport;
    }

    public JobSeekerDTO getBenefitJobSeeker() {
        return benefitJobSeeker;
    }

    public void setBenefitJobSeeker(JobSeekerDTO benefitJobSeeker) {
        this.benefitJobSeeker = benefitJobSeeker;
    }

    public Boolean getBenefitGaurenteedStatePension() {
        return benefitGaurenteedStatePension;
    }

    public void setBenefitGaurenteedStatePension(
            Boolean benefitGaurenteedStatePension) {
        this.benefitGaurenteedStatePension = benefitGaurenteedStatePension;
    }

    public Boolean getBenefitClaimedByPartner() {
        return benefitClaimedByPartner;
    }

    public void setBenefitClaimedByPartner(Boolean benefitClaimedByPartner) {
        this.benefitClaimedByPartner = benefitClaimedByPartner;
    }

    public PartnerDTO getPartnerDetails() {
        return partnerDetails;
    }

    public void setPartnerDetails(PartnerDTO partnerDetails) {
        this.partnerDetails = partnerDetails;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getUnder18HeardYouthCourt() {
        return under18HeardYouthCourt;
    }

    public void setUnder18HeardYouthCourt(Boolean under18HeardYouthCourt) {
        this.under18HeardYouthCourt = under18HeardYouthCourt;
    }

    public Boolean getUnder18HeardMagsCourt() {
        return under18HeardMagsCourt;
    }

    public void setUnder18HeardMagsCourt(Boolean under18HeardMagsCourt) {
        this.under18HeardMagsCourt = under18HeardMagsCourt;
    }

    public Boolean getUnder18FullEducation() {
        return under18FullEducation;
    }

    public void setUnder18FullEducation(Boolean under18FullEducation) {
        this.under18FullEducation = under18FullEducation;
    }

    public Boolean getUnder16() {
        return under16;
    }

    public void setUnder16(Boolean under16) {
        this.under16 = under16;
    }

    public Boolean getBetween1617() {
        return between1617;
    }

    public void setBetween1617(Boolean between1617) {
        this.between1617 = between1617;
    }

    public Long getPassportedId() {
        return passportedId;
    }

    public void setPassportedId(Long passportedId) {
        this.passportedId = passportedId;
    }

    public Boolean getBenefitEmploymentSupport() {
        return benefitEmploymentSupport;
    }

    public void setBenefitEmploymentSupport(Boolean benefitEmploymentSupport) {
        this.benefitEmploymentSupport = benefitEmploymentSupport;
    }

    public AssessmentStatusDTO getAssessementStatusDTO() {
        return assessementStatusDTO;
    }

    public void setAssessementStatusDTO(AssessmentStatusDTO assessementStatusDTO) {
        this.assessementStatusDTO = assessementStatusDTO;
    }

    public PassportConfirmationDTO getPassportConfirmationDTO() {
        return passportConfirmationDTO;
    }

    public void setPassportConfirmationDTO(
            PassportConfirmationDTO passportConfirmationDTO) {
        this.passportConfirmationDTO = passportConfirmationDTO;
    }

    public NewWorkReasonDTO getNewWorkReason() {
        return newWorkReason;
    }

    public void setNewWorkReason(NewWorkReasonDTO newWorkReason) {
        this.newWorkReason = newWorkReason;
    }

    public String getDwpResult() {
        return dwpResult;
    }

    public void setDwpResult(String dwpResult) {
        this.dwpResult = dwpResult;
    }

    public Long getCmuId() {
        return cmuId;
    }

    public void setCmuId(Long cmuId) {
        this.cmuId = cmuId;
    }

    public String toString() {
        StringBuffer op = new StringBuffer(" PassportedDTO - ");
        op.append("passportId [").append(this.passportedId).append("] ");
        op.append("confirmation[").append(this.passportConfirmationDTO.getConfirmation()).append("] ");
        op.append("status[").append(this.assessementStatusDTO).append("] ");
        op.append("date[").append(this.date).append("] ");
        op.append("result[").append(this.result).append("] ");

        op.append("applicatEvidenceList[");
        if (this.passportSummaryEvidenceDTO.getApplicantIncomeEvidenceList() == null) {
            op.append("null");
        } else {
            for (EvidenceDTO evidenceDTO : this.passportSummaryEvidenceDTO.getApplicantIncomeEvidenceList()) {
                op.append(evidenceDTO).append(", ");
            }
        }
        op.append("] ");

        op.append("partnerEvidenceList[");
        if (this.passportSummaryEvidenceDTO.getPartnerIncomeEvidenceList() == null) {
            op.append("null");
        } else {
            for (EvidenceDTO evidenceDTO : this.passportSummaryEvidenceDTO.getPartnerIncomeEvidenceList()) {
                op.append(evidenceDTO).append(", ");
            }
        }
        op.append("] ");


        return op.toString();
    }

    public IncomeEvidenceSummaryDTO getPassportSummaryEvidenceDTO() {
        return passportSummaryEvidenceDTO;
    }

    public void setPassportSummaryEvidenceDTO(
            IncomeEvidenceSummaryDTO passportSummaryEvidenceDTO) {
        this.passportSummaryEvidenceDTO = passportSummaryEvidenceDTO;
    }

    public Long getUsn() {
        return usn;
    }

    public void setUsn(Long usn) {
        this.usn = usn;
    }

    public String getWhoDwpChecked() {
        return whoDwpChecked;
    }

    public void setWhoDwpChecked(String whoDwpChecked) {
        this.whoDwpChecked = whoDwpChecked;
    }

    public ReviewTypeDTO getReviewType() {
        return reviewType;
    }

    public void setReviewType(ReviewTypeDTO reviewType) {
        this.reviewType = reviewType;
    }

}