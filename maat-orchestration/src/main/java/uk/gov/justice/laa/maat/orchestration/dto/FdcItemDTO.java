package uk.gov.justice.laa.maat.orchestration.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class FdcItemDTO extends GenericDTO {

    private Long id;
    private String caseId;
    private String itemType;
    private String courtCode;
    private String supplierCode;
    private Boolean apportioned;
    private Boolean paidAsClaimed;
    private String latest;
    private Double cost;
    private Double vat;
    private Date costDate;

    private FdcAdjustmentReasonDTO adjustmentReason;

    public static final String LGFS_COST_TYPE = "LGFS";
    public static final String AGFS_COST_TYPE = "AGFS";


    public FdcItemDTO() {
        reset();
    }

    private void reset() {

        // initialise complex types.
        this.adjustmentReason = new FdcAdjustmentReasonDTO();
    }

    @Override
    public Object getKey() {

        return id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public Boolean getApportioned() {
        return apportioned;
    }

    public void setApportioned(Boolean apportioned) {
        this.apportioned = apportioned;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Date getCostDate() {
        return costDate;
    }

    public void setCostDate(Date costDate) {
        this.costDate = costDate;
    }

    public FdcAdjustmentReasonDTO getAdjustmentReason() {
        return adjustmentReason;
    }

    public void setAdjustmentReason(FdcAdjustmentReasonDTO adjustmentReason) {
        this.adjustmentReason = adjustmentReason;
    }

    public String toString() {

        StringBuffer op = new StringBuffer();
        op.append("FdcItemDTO-id[").append(this.id).append("] ");
        op.append("caseId-Court-Supplier[").append(this.caseId).append("/").append(this.courtCode).append("/").append(this.supplierCode).append("] ");
        op.append(" itemType[").append(this.itemType).append("] ");
        //op.append( " apportioned[").append(this.apportioned).append("] ");
        op.append(" cost[").append(this.cost).append("] ");
        op.append(" VAT[").append(this.vat).append("] ");
        op.append(" costDate[").append(this.costDate).append("] ");
        op.append(" adjustmentReason[").append(this.adjustmentReason).append("] ");
        return op.toString();
    }

    public static FdcItemDTO exampleAdjusment(Double cost) {
        FdcItemDTO fdcItemDTO = new FdcItemDTO();
        fdcItemDTO.caseId = "";
        fdcItemDTO.costDate = new Date();
        fdcItemDTO.adjustmentReason = new FdcAdjustmentReasonDTO(null, null);
        //fdcItemDTO.apportioned = false;
        fdcItemDTO.cost = cost;
        fdcItemDTO.vat = cost * 1.2;

        return fdcItemDTO;
    }

    public static FdcItemDTO example(String caseRef, double cost) {
        FdcItemDTO fdcItemDTO = new FdcItemDTO();

        fdcItemDTO.caseId = caseRef;
        fdcItemDTO.costDate = new Date("08/07/2011");
        String reasonCode = "Pre AGFS Transfer";
        String reasonDesc = "Pre AGFS Transfer";
        fdcItemDTO.adjustmentReason = new FdcAdjustmentReasonDTO(reasonCode, reasonDesc);
        //fdcItemDTO.apportioned = false;
        fdcItemDTO.cost = cost;
        fdcItemDTO.vat = cost * 1.2;

        return fdcItemDTO;
    }

    public boolean isEditable() {
        return getId() == null || getId() == 0;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public static FdcItemDTO createBlankCost(String costTypeId) {
        FdcItemDTO fdcItemDTO = new FdcItemDTO();
        //fdcItemDTO.setId(0L);
        fdcItemDTO.setItemType(costTypeId);
        fdcItemDTO.setCostDate(new Date());
        fdcItemDTO.setCost(0.0D);
        fdcItemDTO.setVat(0.0D);

        return fdcItemDTO;
    }

    public String getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public Boolean getPaidAsClaimed() {
        return paidAsClaimed;
    }

    public void setPaidAsClaimed(Boolean paidAsClaimed) {
        this.paidAsClaimed = paidAsClaimed;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public String getCostRef() {
        String ref = null;
        if (StringUtils.isBlank(this.caseId)) {
            // A Manual adjustment. No Case id exists...
            ref = "";

        } else {
            // A system generated item (produced from CCR / CCLF feed.
            StringBuffer op = new StringBuffer();
            op.append(this.caseId).append(" \\ ");
            if (StringUtils.isBlank(this.courtCode)) {
                op.append("---");
            } else {
                op.append(this.courtCode);
            }
            op.append(" \\ ");
            if (StringUtils.isBlank(this.courtCode)) {
                op.append("------");
            } else {
                op.append(this.supplierCode);
            }
            ref = op.toString();
        }

        return ref;
    }

    public Double getTotal() {
        double vat;
        double cost;

        if (this.getVat() == null) {
            vat = 0.0D;
        } else {
            vat = getVat().doubleValue();
        }

        if (this.getCost() == null) {
            cost = 0.0D;
        } else {
            cost = getCost().doubleValue();
        }

        return Double.valueOf(vat + cost);
    }

    public boolean isReasonCodePopulated() {
        return StringUtils.isNotBlank(getAdjustmentReason().getCode());
    }

}
