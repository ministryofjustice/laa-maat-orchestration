package uk.gov.justice.laa.maat.orchestration.dto;

public class PaymentMethodDTO extends GenericDTO {
    private String paymentMethod;
    private String description;

    public PaymentMethodDTO() {
    }

    public PaymentMethodDTO(String payment, String desc) {
        this.paymentMethod = payment;
        this.description = desc;
    }

    @Override
    public String getKey() {
        return getDescription();
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
