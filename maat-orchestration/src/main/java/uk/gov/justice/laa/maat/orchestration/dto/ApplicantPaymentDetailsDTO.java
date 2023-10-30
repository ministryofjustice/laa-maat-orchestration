package uk.gov.justice.laa.maat.orchestration.dto;

public class ApplicantPaymentDetailsDTO extends GenericDTO {

    private PaymentMethodDTO paymentMethod;
    private Integer paymentDay;
    private String accountNumber;
    private String sortCode;
    private String accountName;

    public ApplicantPaymentDetailsDTO() {
        reset();
    }

    public void reset() {
        this.paymentMethod = new PaymentMethodDTO();
    }

    @Override
    public String getKey() {
        return getAccountNumber();
    }

    public PaymentMethodDTO getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodDTO paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(Integer paymentDay) {
        this.paymentDay = paymentDay;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

}
