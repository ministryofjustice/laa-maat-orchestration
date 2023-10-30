package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class CrownCourtSummaryDTO extends GenericDTO {
    public static final String REP_ORDER_DECISION_GRANTED = "Granted";
    public static final String REP_TYPE_CC_ONLY = "Crown Court Only";

    private Long ccRepId;
    private SysGenString ccRepType;
    private Date ccRepOrderDate;
    private Date sentenceOrderDate;
    private Date ccWithDrawalDate;
    private SysGenString repOrderDecision;
    private Boolean inPrisoned;
    private OutcomeDTO ccOutcome;
    private OutcomeDTO ccAppealOutcome;
    private Boolean benchWarrantyIssued;
    private EvidenceFeeDTO evidenceProvisionFee;
    private Collection<OutcomeDTO> outcomeDTOs;

    public CrownCourtSummaryDTO() {
        reset();
    }

    public void reset() {
        this.ccRepId = null;
        this.ccRepType = null;
        this.ccRepOrderDate = null;
        this.sentenceOrderDate = null;
        this.ccWithDrawalDate = null;
        this.repOrderDecision = null;
        this.evidenceProvisionFee = new EvidenceFeeDTO();
        this.inPrisoned = false;
        this.benchWarrantyIssued = false;
        this.ccOutcome = new OutcomeDTO();
        this.ccAppealOutcome = new OutcomeDTO();
        this.outcomeDTOs = new ArrayList<>();

    }

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getCcRepId() {
        return ccRepId;
    }

    public void setCcRepId(Long ccRepId) {
        this.ccRepId = ccRepId;
    }

    public SysGenString getCcRepType() {
        return ccRepType;
    }

    public void setCcRepType(SysGenString ccRepType) {
        this.ccRepType = ccRepType;
    }

    public Date getCcRepOrderDate() {
        return ccRepOrderDate;
    }

    public void setCcRepOrderDate(Date ccRepOrderDate) {
        this.ccRepOrderDate = ccRepOrderDate;
    }

    public Date getCcWithDrawalDate() {
        return ccWithDrawalDate;
    }

    public void setCcWithDrawalDate(Date ccWithDrawalDate) {
        this.ccWithDrawalDate = ccWithDrawalDate;
    }

    public EvidenceFeeDTO getEvidenceProvisionFee() {
        return evidenceProvisionFee;
    }

    public void setEvidenceProvisionFee(EvidenceFeeDTO evidenceProvisionFee) {
        this.evidenceProvisionFee = evidenceProvisionFee;
    }

    public Boolean getInPrisoned() {
        return inPrisoned;
    }

    public void setInPrisoned(Boolean inPrisoned) {
        this.inPrisoned = inPrisoned;
    }

    public Boolean getBenchWarrantyIssued() {
        return benchWarrantyIssued;
    }

    public void setBenchWarrantyIssued(Boolean benchWarrantyIssued) {
        this.benchWarrantyIssued = benchWarrantyIssued;
    }

    public OutcomeDTO getCcOutcome() {
        return ccOutcome;
    }

    public void setCcOutcome(OutcomeDTO ccOutcome) {
        this.ccOutcome = ccOutcome;
    }

    public OutcomeDTO getCcAppealOutcome() {
        return ccAppealOutcome;
    }

    public void setCcAppealOutcome(OutcomeDTO ccAppealOutcome) {
        this.ccAppealOutcome = ccAppealOutcome;
    }

    public SysGenString getRepOrderDecision() {
        return repOrderDecision;
    }

    public void setRepOrderDecision(SysGenString repOrderDecision) {
        this.repOrderDecision = repOrderDecision;
    }

    public Collection<OutcomeDTO> getOutcomeDTOs() {
        return outcomeDTOs;
    }

    public void setOutcomeDTOs(Collection<OutcomeDTO> outcomeDTOs) {
        this.outcomeDTOs = outcomeDTOs;
    }

    public Date getSentenceOrderDate() {
        return sentenceOrderDate;
    }

    public void setSentenceOrderDate(Date sentenceOrderDate) {
        this.sentenceOrderDate = sentenceOrderDate;
    }

}
