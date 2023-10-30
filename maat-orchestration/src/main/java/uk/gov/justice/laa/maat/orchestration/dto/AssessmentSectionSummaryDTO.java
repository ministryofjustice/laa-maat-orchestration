/**
 *
 */
package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class AssessmentSectionSummaryDTO extends GenericDTO {

    private String section;
    private Double applicantAnnualTotal;
    private Double partnerAnnualTotal;
    private Double annualTotal;
    private Collection<AssessmentDetailDTO> assessmentDetail;

    public AssessmentSectionSummaryDTO() {
        reset();
    }

    private void reset() {
        this.assessmentDetail = new ArrayList<>();
        this.applicantAnnualTotal = Double.valueOf(0.0d);
        this.partnerAnnualTotal = Double.valueOf(0.0d);
        this.annualTotal = Double.valueOf(0.0);
    }

    @Override
    public Object getKey() {
        return getSection();        // used in UI to retrieve section for update.
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Double getApplicantAnnualTotal() {
        return applicantAnnualTotal;
    }

    public void setApplicantAnnualTotal(Double applicantAnnualTotal) {
        this.applicantAnnualTotal = applicantAnnualTotal;
    }

    public Double getPartnerAnnualTotal() {
        return partnerAnnualTotal;
    }

    public void setPartnerAnnualTotal(Double partnerAnnualTotal) {
        this.partnerAnnualTotal = partnerAnnualTotal;
    }

    public Double getAnnualTotal() {
        return annualTotal;
    }

    public void setAnnualTotal(Double annualTotal) {
        this.annualTotal = annualTotal;
    }

    public Collection<AssessmentDetailDTO> getAssessmentDetail() {
        return assessmentDetail;
    }

    public void setAssessmentDetail(Collection<AssessmentDetailDTO> assessmentDetail) {
        this.assessmentDetail = assessmentDetail;
    }

    public AssessmentDetailDTO obtainDetail(long id) {
        AssessmentDetailDTO dto = null;
        if (getAssessmentDetail() != null) {
            for (Iterator<AssessmentDetailDTO> iterator = getAssessmentDetail().iterator(); iterator.hasNext(); ) {
                AssessmentDetailDTO assessmentDetailDTO = iterator.next();
                if (assessmentDetailDTO.getId().longValue() == id) {
                    dto = assessmentDetailDTO;
                    break;
                }
            }
        }
        return dto;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Section[").append(this.section).append("],  ");
        sb.append("[").append(assessmentDetail == null ? "null" : assessmentDetail.size()).append("] details.");
        return sb.toString();

    }

}
