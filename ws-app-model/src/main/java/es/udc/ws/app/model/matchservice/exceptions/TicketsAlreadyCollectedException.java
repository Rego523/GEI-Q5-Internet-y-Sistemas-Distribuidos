package es.udc.ws.app.model.matchservice.exceptions;

public class TicketsAlreadyCollectedException extends Exception {
    private Long saleId;

    public TicketsAlreadyCollectedException(Long saleId) {
        super("Tickets with saleId= " + saleId + "have already been collected.");
        this.saleId = saleId;
    }
    public Long getSaleId(){
        return saleId;
    }
    public void setSaleId(Long saleId){
        this.saleId = saleId;
    }
}
