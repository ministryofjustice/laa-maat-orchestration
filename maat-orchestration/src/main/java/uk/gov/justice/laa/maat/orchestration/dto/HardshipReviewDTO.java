package uk.gov.justice.laa.maat.orchestration.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class HardshipReviewDTO extends GenericDTO {

    private Long id;
    private SupplierDTO supplier;
    private NewWorkReasonDTO newWorkReason;
    private Long cmuId;
    private String reviewResult;
    private Date reviewDate;
    private String notes;
    private String decisionNotes;
    private HRSolicitorsCostsDTO solictorsCosts;
    private SysGenCurrency disposableIncome;
    private SysGenCurrency disposableIncomeAfterHardship;
    private Collection<HRSectionDTO> section;
    private Collection<HRProgressDTO> progress;
    private AssessmentStatusDTO asessmentStatus;
    private Timestamp timestamp;

    public HardshipReviewDTO() {
        reset();
    }

    public void reset() {
        this.id = null;
        this.supplier = new SupplierDTO();
        this.newWorkReason = new NewWorkReasonDTO();
        this.solictorsCosts = new HRSolicitorsCostsDTO();
        this.section = new ArrayList<>();
        this.progress = new ArrayList<>();
        this.asessmentStatus = new AssessmentStatusDTO();

    }

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SupplierDTO getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
    }

    public NewWorkReasonDTO getNewWorkReason() {
        return newWorkReason;
    }

    public void setNewWorkReason(NewWorkReasonDTO newWorkReason) {
        this.newWorkReason = newWorkReason;
    }

    public Long getCmuId() {
        return cmuId;
    }

    public void setCmuId(Long cmuId) {
        this.cmuId = cmuId;
    }

    public String getReviewResult() {
        return reviewResult;
    }

    public void setReviewResult(String reviewResult) {
        this.reviewResult = reviewResult;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDecisionNotes() {
        return decisionNotes;
    }

    public void setDecisionNotes(String decisionNotes) {
        this.decisionNotes = decisionNotes;
    }

    public HRSolicitorsCostsDTO getSolictorsCosts() {
        return solictorsCosts;
    }

    public void setSolictorsCosts(HRSolicitorsCostsDTO solictorsCosts) {
        this.solictorsCosts = solictorsCosts;
    }

    public SysGenCurrency getDisposableIncome() {
        return disposableIncome;
    }

    public void setDisposableIncome(SysGenCurrency disposableIncome) {
        this.disposableIncome = disposableIncome;
    }

    public SysGenCurrency getDisposableIncomeAfterHardship() {
        return disposableIncomeAfterHardship;
    }

    public void setDisposableIncomeAfterHardship(SysGenCurrency disposableIncomeAfterHardship) {
        this.disposableIncomeAfterHardship = disposableIncomeAfterHardship;
    }

    public Collection<HRSectionDTO> getSection() {
        return section;
    }

    public void setSection(Collection<HRSectionDTO> section) {
        this.section = section;
    }

    public Collection<HRProgressDTO> getProgress() {
        return progress;
    }

    public void setProgress(Collection<HRProgressDTO> progress) {
        this.progress = progress;
    }

    public AssessmentStatusDTO getAsessmentStatus() {
        return asessmentStatus;
    }

    public void setAsessmentStatus(AssessmentStatusDTO asessmentStatus) {
        this.asessmentStatus = asessmentStatus;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
