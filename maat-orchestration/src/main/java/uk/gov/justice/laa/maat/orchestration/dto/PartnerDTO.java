package uk.gov.justice.laa.maat.orchestration.dto;


import java.util.Date;

public class PartnerDTO extends GenericDTO {
    private String firstName;
    private String surname;
    private String nationaInsuranceNumber;
    private Date dateOfBirth;

    public PartnerDTO() {

        reset();
    }

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNationaInsuranceNumber() {
        return nationaInsuranceNumber;
    }

    public void setNationaInsuranceNumber(String nationaInsuranceNumber) {
        this.nationaInsuranceNumber = nationaInsuranceNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void reset() {

        this.dateOfBirth = null;
        this.firstName = null;
        this.surname = null;
        this.nationaInsuranceNumber = null;
    }
}