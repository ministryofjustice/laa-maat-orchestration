package uk.gov.justice.laa.maat.orchestration.dto;

public class SupplierDTO extends GenericDTO {
    private String accountNumber;
    private String name;
    private AddressDTO address;

    public SupplierDTO() {
        reset();
    }

    public void reset() {
        this.accountNumber = null;
        this.name = null;
        this.address = new AddressDTO();
    }

    @Override
    public String getKey() {
        return getAccountNumber();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}