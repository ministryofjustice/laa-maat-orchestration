package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class ContributionsDTO extends GenericDTO {
    private Long id;
    private SysGenCurrency monthlyContribs;
    private SysGenCurrency upfrontContribs;
    private SysGenDate effectiveDate;
    private SysGenDate calcDate;
    private SysGenCurrency capped;
    private boolean upliftApplied;
    private SysGenString basedOn;

    public ContributionsDTO() {
        reset();
    }

    private void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public Long getKey() {
        return getId();
    }

    public SysGenCurrency getMonthlyContribs() {
        return monthlyContribs;
    }

    public void setMonthlyContribs(SysGenCurrency monthlyContribs) {
        this.monthlyContribs = monthlyContribs;
    }

    public SysGenCurrency getUpfrontContribs() {
        return upfrontContribs;
    }

    public void setUpfrontContribs(SysGenCurrency upfrontContribs) {
        this.upfrontContribs = upfrontContribs;
    }

    public SysGenDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(SysGenDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public SysGenDate getCalcDate() {
        return calcDate;
    }

    public void setCalcDate(SysGenDate calcDate) {
        this.calcDate = calcDate;
    }

    public SysGenCurrency getCapped() {
        return capped;
    }

    public void setCapped(SysGenCurrency capped) {
        this.capped = capped;
    }

    public boolean isUpliftApplied() {
        return upliftApplied;
    }

    public void setUpliftApplied(boolean upliftApplied) {
        this.upliftApplied = upliftApplied;
    }

    public SysGenString getBasedOn() {
        return basedOn;
    }

    public void setBasedOn(SysGenString basedOn) {
        this.basedOn = basedOn;
    }

    public static ContributionsDTO example() {
        ContributionsDTO dto = new ContributionsDTO();

        dto.setMonthlyContribs(new SysGenCurrency(Double.valueOf(2345.67d)));
        dto.setUpfrontContribs(new SysGenCurrency(Double.valueOf(1234.56d)));
        dto.setCapped(new SysGenCurrency(Double.valueOf(12345.67d)));
        dto.setEffectiveDate(new SysGenDate(new Date()));
        dto.setCalcDate(new SysGenDate(new Date()));
        dto.setUpliftApplied(Boolean.TRUE);
        dto.setBasedOn(new SysGenString("Offence Type"));

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
