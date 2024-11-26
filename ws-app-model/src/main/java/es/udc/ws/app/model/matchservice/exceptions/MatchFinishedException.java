package es.udc.ws.app.model.matchservice.exceptions;

public class MatchFinishedException extends Exception{
    private Long matchId;

    public MatchFinishedException(Long matchId) {
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
