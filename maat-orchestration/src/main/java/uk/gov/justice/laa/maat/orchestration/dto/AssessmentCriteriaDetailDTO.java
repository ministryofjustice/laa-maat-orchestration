package uk.gov.justice.laa.maat.orchestration.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class AssessmentCriteriaDetailDTO extends GenericDTO implements Comparable<AssessmentCriteriaDetailDTO> {

    private Long id;
    private Long assessmentCriteriaId;
    private String description;
    private String section;
    private Integer sequence;
    private String asdeDetailCode;

    private Collection<CaseTypeCriteriaDetailDTO> caseTypeCriteriaDetail;
    private Collection<FrequenciesDTO> freqDetails;


    public AssessmentCriteriaDetailDTO() {
        reset();
    }

    private void reset() {
        caseTypeCriteriaDetail = new ArrayList<>();
    }


    @Override
    public Object getKey() {
        return getId();
    }

    public int compareTo(AssessmentCriteriaDetailDTO o) {

        int result = -1;

        if (id > o.getId()) {
            result = section.compareTo(o.getSection());
        } else {
            result = o.getSection().compareTo(section);
        }
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssessmentCriteriaId() {
        return assessmentCriteriaId;
    }

    public void setAssessmentCriteriaId(Long assessmentCriteriaId) {
        this.assessmentCriteriaId = assessmentCriteriaId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Collection<CaseTypeCriteriaDetailDTO> getCaseTypeCriteriaDetail() {
        return caseTypeCriteriaDetail;
    }

    public void setCaseTypeCriteriaDetail(
            Collection<CaseTypeCriteriaDetailDTO> caseTypeCriteriaDetail) {
        this.caseTypeCriteriaDetail = caseTypeCriteriaDetail;
    }

    public String toString() {
        StringBuilder op = new StringBuilder(128);
        op.append("AssmntCritDetDTO:- ");
        op.append("id[").append(this.id).append("], ");
        op.append("AssCritId[").append(this.assessmentCriteriaId).append("], ");
        op.append("desc[").append(this.description).append("], ");
        op.append("Sect[").append(this.section).append("], ");
        op.append("Seq[").append(this.sequence).append("], ");
        op.append("asdeDetailCode[").append(this.asdeDetailCode).append("], ");
        op.append("caseType(s)[");
        Collection<CaseTypeCriteriaDetailDTO> caseTypeCriteriaDetail = this.getCaseTypeCriteriaDetail();
        for (Iterator<CaseTypeCriteriaDetailDTO> iterator = caseTypeCriteriaDetail.iterator(); iterator.hasNext(); ) {
            op.append(iterator.next()).append(iterator.hasNext() ? ", " : "");
        }
        op.append("] ");

        return op.toString();
    }

    public CaseTypeCriteriaDetailDTO getCaseTypeCriteriaDetail(String caseTypeCode) {
        CaseTypeCriteriaDetailDTO selectedCaseType = null;

        for (Iterator<CaseTypeCriteriaDetailDTO> iterator = getCaseTypeCriteriaDetail().iterator(); iterator.hasNext(); ) {
            CaseTypeCriteriaDetailDTO caseTypeCriteriaDetailDTO = iterator.next();
            if (StringUtils.equals(caseTypeCode, caseTypeCriteriaDetailDTO.getCatyCaseType())) {
                selectedCaseType = caseTypeCriteriaDetailDTO;
                break;
            }
        }

        return selectedCaseType;
    }

    public Collection<FrequenciesDTO> getFreqDetails() {
        return freqDetails;
    }

    public void setFreqDetails(Collection<FrequenciesDTO> freqDetails) {
        this.freqDetails = freqDetails;
    }

    public String getAsdeDetailCode() {
        return asdeDetailCode;
    }

    public void setAsdeDetailCode(String asdeDetailCode) {
        this.asdeDetailCode = asdeDetailCode;
    }

}
