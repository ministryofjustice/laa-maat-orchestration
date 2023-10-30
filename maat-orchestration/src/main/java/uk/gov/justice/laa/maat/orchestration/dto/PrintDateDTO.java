package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class PrintDateDTO extends GenericDTO {

    private static final long serialVersionUID = -8968823417266234052L;

    private Long id;
    private Long corrId;
    private Date printDate;

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

    public Long getCorrId() {
        return corrId;
    }

    public void setCorrId(Long corrId) {
        this.corrId = corrId;
    }

    public Date getPrintDate() {
        return printDate;
    }

    public void setPrintDate(Date printDate) {
        this.printDate = printDate;
    }

    public String toString() {

        StringBuffer op = new StringBuffer();
        op.append(" id = " + id);
        op.append(" corrId = " + corrId);
        op.append(" printed date = " + printDate);

        return op.toString();
    }

}
