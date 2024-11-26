package es.udc.ws.app.client.service.exceptions;

public class ClientTicketsAlreadyCollectedException  extends Exception {
    private Long saleId;

    public ClientTicketsAlreadyCollectedException(Long saleId) {
        super("Tickets with saleId=\"" + saleId + "\n have already been collected.");
        this.saleId = saleId;
    }
    public Long getSaleId(){
        return saleId;
    }
    public void setSaleId(Long saleId){
        this.saleId = saleId;
    }
}
