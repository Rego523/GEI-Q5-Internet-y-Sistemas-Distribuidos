package es.udc.ws.app.client.service.exceptions;

public class ClientMatchNoMoreTicketsException extends Exception {
    private Long matchId;
    private int numTicketsSale;
    private int availableTickets;

    public ClientMatchNoMoreTicketsException(Long matchId, int numTicketsSale, int availableTickets) {
        super("Trying to buy " + numTicketsSale + " tickets for the match with ID " + matchId +
                " while only " + availableTickets + " tickets are available.");
        this.matchId = matchId;
        this.numTicketsSale = numTicketsSale;
        this.availableTickets = availableTickets;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public int getNumTicketsSale() {
        return numTicketsSale;
    }

    public void setNumTicketsSale(int numTicketsSale) {
        this.numTicketsSale = numTicketsSale;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }
}

