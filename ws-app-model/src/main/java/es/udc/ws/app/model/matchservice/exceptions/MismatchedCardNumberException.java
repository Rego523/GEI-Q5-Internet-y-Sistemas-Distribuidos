package es.udc.ws.app.model.matchservice.exceptions;

public class MismatchedCardNumberException extends Exception {
    private Long saleId;

    public MismatchedCardNumberException(Long saleId) {
        super("The presented bank card number does not match the one " +
                "stored in the sale with ID=" + saleId);
        this.saleId = saleId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }
}
