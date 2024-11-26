package es.udc.ws.app.client.service.exceptions;

public class ClientMatchFinishedException extends Exception{

    private Long matchId;

    public ClientMatchFinishedException(Long matchId) {
        super("The match with ID " + matchId + " has already finished.");
        this.matchId = matchId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

}
