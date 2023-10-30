package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class CorrespondenceDTO extends GenericDTO {
    private Long id;
    private Long repId;
    private Long financialAssessmentId;
    private Long passportAssessmentId;
    private Date generatedDate;
    private Date lastPrintDate;
    private CorrespondenceTypeDTO correspondenceType;
    private String templateName;
    private Date originalEmailDate; // MW - 30/03/2017 - FIP Changes

    private Collection<PrintDateDTO> printDateDTOs;

    public CorrespondenceDTO() {
        reset();
    }

    public void reset() {
        this.id = null;
        this.generatedDate = null;
        this.lastPrintDate = null;
        this.correspondenceType = new CorrespondenceTypeDTO();
        this.printDateDTOs = new ArrayList<>();
        this.templateName = null;
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

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    public Date getLastPrintDate() {
        return lastPrintDate;
    }

    public void setLastPrintDate(Date lastPrintDate) {
        this.lastPrintDate = lastPrintDate;
    }

    public static CorrespondenceDTO example(long id) {
        CorrespondenceDTO dto = new CorrespondenceDTO();

        dto.setGeneratedDate(new Date());
        dto.setId(Long.valueOf(id));

        if (id > 2) {
            dto.setLastPrintDate(null);
        } else {
            dto.setLastPrintDate(new Date());
        }

        dto.setCorrespondenceType(CorrespondenceTypeDTO.example());
        dto.setTemplateName("RM1");

        return dto;
    }

    public CorrespondenceTypeDTO getCorrespondenceType() {
        return correspondenceType;
    }

    public void setCorrespondenceType(CorrespondenceTypeDTO correspondenceType) {
        this.correspondenceType = correspondenceType;
    }

    public Long getRepId() {
        return repId;
    }

    public void setRepId(Long repId) {
        this.repId = repId;
    }

    public Long getFinancialAssessmentId() {
        return financialAssessmentId;
    }

    public void setFinancialAssessmentId(Long financialAssessmentId) {
        this.financialAssessmentId = financialAssessmentId;
    }

    public Long getPassportAssessmentId() {
        return passportAssessmentId;
    }

    public void setPassportAssessmentId(Long passportAssessmentId) {
        this.passportAssessmentId = passportAssessmentId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Collection<PrintDateDTO> getPrintDateDTOs() {
        return printDateDTOs;
    }

    public void setPrintDateDTOs(Collection<PrintDateDTO> printDateDTOs) {
        this.printDateDTOs = printDateDTOs;
    }

    public Date getOriginalEmailDate() {
        return originalEmailDate;
    }

    public void setOriginalEmailDate(Date originalEmailDate) {
        this.originalEmailDate = originalEmailDate;
    }

}
