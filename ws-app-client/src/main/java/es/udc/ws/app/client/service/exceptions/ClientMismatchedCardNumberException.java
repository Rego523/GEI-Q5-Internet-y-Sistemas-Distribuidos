package es.udc.ws.app.client.service.exceptions;

public class ClientMismatchedCardNumberException extends Exception {
    private Long saleId;

    public ClientMismatchedCardNumberException(Long saleId) {
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
