/**
 *
 */
package uk.gov.justice.laa.maat.orchestration.dto;

public class ReviewTypeDTO extends GenericDTO {

    private static final long serialVersionUID = 5645077250308954629L;

    private String code;
    private String description;

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
