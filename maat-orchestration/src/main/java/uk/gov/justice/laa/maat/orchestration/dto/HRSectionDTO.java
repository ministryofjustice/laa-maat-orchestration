package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;

public class HRSectionDTO extends GenericDTO {

    private HRDetailTypeDTO detailType;
    private Collection<HRDetailDTO> detail;
    private Currency applicantAnnualTotal;
    private Currency partnerAnnualTotal;
    private Currency annualTotal;

    public HRSectionDTO() {
        reset();
    }

    public void reset() {
        this.detailType = new HRDetailTypeDTO();
        this.detail = new ArrayList<>();
    }

    @Override
    public Object getKey() {
        return getDetailType().getKey();
    }

    public HRDetailTypeDTO getDetailType() {
        return detailType;
    }

    public void setDetailType(HRDetailTypeDTO detailType) {
        this.detailType = detailType;
    }

    public Collection<HRDetailDTO> getDetail() {
        return detail;
    }

    public void setDetail(Collection<HRDetailDTO> detail) {
        this.detail = detail;
    }

    public Currency getApplicantAnnualTotal() {
        return applicantAnnualTotal;
    }

    public void setApplicantAnnualTotal(Currency applicantAnnualTotal) {
        this.applicantAnnualTotal = applicantAnnualTotal;
    }

    public Currency getPartnerAnnualTotal() {
        return partnerAnnualTotal;
    }

    public void setPartnerAnnualTotal(Currency partnerAnnualTotal) {
        this.partnerAnnualTotal = partnerAnnualTotal;
    }

    public Currency getAnnualTotal() {
        return annualTotal;
    }

    public void setAnnualTotal(Currency annualTotal) {
        this.annualTotal = annualTotal;
    }

}
