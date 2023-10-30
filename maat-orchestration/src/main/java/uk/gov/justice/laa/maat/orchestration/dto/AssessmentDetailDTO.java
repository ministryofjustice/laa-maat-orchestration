package uk.gov.justice.laa.maat.orchestration.dto;

import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;

public class AssessmentDetailDTO extends GenericDTO {

    private Long id;
    private Long criteriaDetailsId;
    private Double applicantAmount;
    private Double partnerAmount;
    private FrequenciesDTO applicantFrequency;
    private FrequenciesDTO partnerFrequency;
    private String description;
    private String detailCode;


    public AssessmentDetailDTO() {
        reset();
    }

    public double obtainApplicantAnnualAmount() {
        double amount = 0D;
        if (StringUtils.isNotBlank(getApplicantFrequency().getCode()) && getApplicantFrequency() != null && getApplicantAmount() != null) {
            BigDecimal annualWeighting = new BigDecimal("1");
            BigDecimal annualAmount = new BigDecimal("0.00");
            // note using String constructor prevents 'noise' inherent with doubles.
            annualWeighting = new BigDecimal(getApplicantFrequency().getAnnualWeighting().toString());
            annualAmount = new BigDecimal(getApplicantAmount().toString());


            amount = (annualAmount.multiply(annualWeighting)).doubleValue();

        }
        return amount;
    }

    public double obtainPartnerAnnualAmount() {
        double amount = 0D;

        if (StringUtils.isNotBlank(getPartnerFrequency().getCode()) && getPartnerFrequency() != null && getPartnerAmount() != null) {
            BigDecimal annualWeighting = new BigDecimal("1");
            BigDecimal annualAmount = new BigDecimal("0.00");

            annualWeighting = new BigDecimal(getPartnerFrequency().getAnnualWeighting().toString());
            annualAmount = new BigDecimal(getPartnerAmount().toString());

            amount = (annualAmount.multiply(annualWeighting)).doubleValue();

        }

        return amount;

    }

    public AssessmentDetailDTO(AssessmentCriteriaDetailDTO criteria) {
        setCriteriaDetailsId(criteria.getId());
        setDescription(criteria.getDescription());

    }

    public void reset() {
        partnerFrequency = new FrequenciesDTO();
        applicantFrequency = new FrequenciesDTO();
        applicantAmount = Double.valueOf(0.0d);
        partnerAmount = Double.valueOf(0.0d);
    }

    public void clearApplicant() {
        getApplicantFrequency().setAnnualWeighting(null);
        getApplicantFrequency().setCode(null);
        setApplicantAmount(Double.valueOf(0.0d));
    }

    public void clearPartner() {
        getPartnerFrequency().setAnnualWeighting(null);
        getPartnerFrequency().setCode(null);
        setPartnerAmount(Double.valueOf(0.0d));
    }

    @Override
    public String toString() {
        StringBuilder op = new StringBuilder(128);
        op.append("AssessmentDetailDTO- ");
        op.append("id[").append(this.id).append("] ");
        op.append("crit.Det.Id[").append(this.criteriaDetailsId).append("] ");
        op.append("app.Amt.[").append(this.applicantAmount).append("] ");
        op.append("app.Freq.[").append(this.applicantFrequency).append("] ");
        op.append("part.Amt.[").append(this.partnerAmount).append("] ");
        op.append("part.Freq.[").append(this.partnerFrequency).append("] ");
        op.append("descr.[").append(this.description).append("] ");
        op.append("descCd[").append(this.detailCode).append("]");

        return op.toString();
    }

    @Override
    public Object getKey() {
        return getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriteriaDetailsId() {
        return criteriaDetailsId;
    }

    public void setCriteriaDetailsId(Long criteriaDetailsId) {
        this.criteriaDetailsId = criteriaDetailsId;
    }

    public Double getApplicantAmount() {
        return applicantAmount;
    }

    public void setApplicantAmount(Double applicantAmount) {
        this.applicantAmount = applicantAmount;
    }

    public Double getPartnerAmount() {
        return partnerAmount;
    }

    public void setPartnerAmount(Double partnerAmount) {
        this.partnerAmount = partnerAmount;
    }

    public FrequenciesDTO getApplicantFrequency() {
        return applicantFrequency;
    }

    public void setApplicantFrequency(FrequenciesDTO applicantFrequency) {
        this.applicantFrequency = applicantFrequency;
    }

    public FrequenciesDTO getPartnerFrequency() {
        return partnerFrequency;
    }

    public void setPartnerFrequency(FrequenciesDTO partnerFrequency) {
        this.partnerFrequency = partnerFrequency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetailCode() {
        return detailCode;
    }

    public void setDetailCode(String detailCode) {
        this.detailCode = detailCode;
    }


}
