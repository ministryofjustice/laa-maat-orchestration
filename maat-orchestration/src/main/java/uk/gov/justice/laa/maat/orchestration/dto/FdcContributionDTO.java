package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class FdcContributionDTO extends GenericDTO {

    private Long id;
    private Long repId;

    private String status;

    private Boolean lgfsComplete;
    private Boolean agfsComplete;

    private Boolean manualAcceleration;

    private Collection<FdcItemDTO> lgfsCosts;
    private Collection<FdcItemDTO> agfsCosts;

    private DrcFileRefDTO drcFileRef;

    private Date dateCalculated;
    private Date dateCreated;
    private Date dateReplaced;

    private Double finalCost;
    private Double judicialApportionment;
    private Double lgfsCost;
    private Double agfsCost;
    private Double vat;

    private Collection<NoteDTO> notes;

    public FdcContributionDTO() {
        reset();
    }

    @Override
    public Object getKey() {

        return id;
    }

    public void reset() {

        this.id = null;
        this.repId = null;
        this.agfsCost = null;
        this.lgfsCost = null;

        this.status = null;
        this.agfsComplete = null;
        this.lgfsComplete = null;

        this.manualAcceleration = null;

        this.dateCalculated = null;
        this.dateCreated = null;
        this.dateReplaced = null;
        this.judicialApportionment = null;

        this.drcFileRef = new DrcFileRefDTO();
        this.agfsCosts = new ArrayList<FdcItemDTO>();
        this.lgfsCosts = new ArrayList<FdcItemDTO>();

        this.notes = new ArrayList<NoteDTO>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DrcFileRefDTO getDrcFileRef() {
        return drcFileRef;
    }

    public void setDrcFileRef(DrcFileRefDTO drcFileRef) {
        this.drcFileRef = drcFileRef;
    }

    public Date getDateCalculated() {
        return dateCalculated;
    }

    public void setDateCalculated(Date dateCalculated) {
        this.dateCalculated = dateCalculated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateReplaced() {
        return dateReplaced;
    }

    public void setDateReplaced(Date dateReplaced) {
        this.dateReplaced = dateReplaced;
    }

    public Double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(Double finalCost) {
        this.finalCost = finalCost;
    }

    public Long getRepId() {
        return repId;
    }

    public void setRepId(Long repId) {
        this.repId = repId;
    }

    public Collection<FdcItemDTO> getLgfsCosts() {
        return lgfsCosts;
    }

    public void setLgfsCosts(Collection<FdcItemDTO> lgfsCosts) {
        this.lgfsCosts = lgfsCosts;
    }

    public Collection<FdcItemDTO> getAgfsCosts() {
        return agfsCosts;
    }

    public void setAgfsCosts(Collection<FdcItemDTO> agfsCosts) {
        this.agfsCosts = agfsCosts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLgfsCost() {
        return lgfsCost;
    }

    public void setLgfsCost(Double lgfsCost) {
        this.lgfsCost = lgfsCost;
    }

    public Double getAgfsCost() {
        return agfsCost;
    }

    public void setAgfsCost(Double agfsCost) {
        this.agfsCost = agfsCost;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public void addAGFSCost(FdcItemDTO fdcItemDTO) {
        this.getAgfsCosts().add(fdcItemDTO);
    }

    public void addLGFSCost(FdcItemDTO fdcItemDTO) {
        this.getLgfsCosts().add(fdcItemDTO);
    }

    public boolean isActive() {
        return this.dateReplaced == null;
    }

    public Boolean getLgfsComplete() {
        return lgfsComplete;
    }

    public void setLgfsComplete(Boolean lgfsComplete) {
        this.lgfsComplete = lgfsComplete;
    }

    public Boolean getAgfsComplete() {
        return agfsComplete;
    }

    public void setAgfsComplete(Boolean agfsComplete) {
        this.agfsComplete = agfsComplete;
    }

    public Collection<NoteDTO> getNotes() {
        return notes;
    }

    public void setNotes(Collection<NoteDTO> notes) {
        this.notes = notes;
    }

    public Boolean getManualAcceleration() {
        return manualAcceleration;
    }

    public void setManualAcceleration(Boolean manualAcceleration) {
        this.manualAcceleration = manualAcceleration;
    }

    public Double getJudicialApportionment() {
        return judicialApportionment;
    }

    public void setJudicialApportionment(Double judicialApportionment) {
        this.judicialApportionment = judicialApportionment;
    }
}
