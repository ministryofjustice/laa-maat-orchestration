package uk.gov.justice.laa.maat.orchestration.dto;

public class HRSolicitorsCostsDTO extends GenericDTO {

    private Currency solicitorRate;
    private Double solicitorHours;
    private Currency solicitorVat;
    private Currency solicitorDisb;
    private Currency solicitorEstimatedTotalCost;

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public Currency getSolicitorRate() {
        return solicitorRate;
    }

    public void setSolicitorRate(Currency solicitorRate) {
        this.solicitorRate = solicitorRate;
    }

    public Double getSolicitorHours() {
        return solicitorHours;
    }

    public void setSolicitorHours(Double solicitorHours) {
        this.solicitorHours = solicitorHours;
    }

    public Currency getSolicitorVat() {
        return solicitorVat;
    }

    public void setSolicitorVat(Currency solicitorVat) {
        this.solicitorVat = solicitorVat;
    }

    public Currency getSolicitorDisb() {
        return solicitorDisb;
    }

    public void setSolicitorDisb(Currency solicitorDisb) {
        this.solicitorDisb = solicitorDisb;
    }

    public Currency getSolicitorEstimatedTotalCost() {
        return solicitorEstimatedTotalCost;
    }

    public void setSolicitorEstimatedTotalCost(Currency solicitorEstimatedTotalCost) {
        this.solicitorEstimatedTotalCost = solicitorEstimatedTotalCost;
    }

}
