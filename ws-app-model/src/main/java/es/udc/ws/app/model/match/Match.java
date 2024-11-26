package es.udc.ws.app.model.match;

import java.time.LocalDateTime;
import java.util.Objects;

public class Match {
    private Long matchId;
    private String visitorTeam;
    private LocalDateTime dateTime;
    private float ticketPrice;
    private int maxTicketsAvailable;
    private LocalDateTime creationDateTime;

    private int soldTickets;

    public Match(String visitorTeam, LocalDateTime dateTime, float ticketPrice, int maxTicketsAvailable) {
        this.visitorTeam = visitorTeam;
        this.dateTime = dateTime;
        this.ticketPrice = ticketPrice;
        this.maxTicketsAvailable = maxTicketsAvailable;
    }

    public Match(Long matchId, String visitorTeam, LocalDateTime dateTime, float ticketPrice, int maxTicketsAvailable) {
        this(visitorTeam, dateTime, ticketPrice, maxTicketsAvailable);
        this.matchId = matchId;
    }

    public Match(Long matchId, String visitorTeam, LocalDateTime dateTime, float ticketPrice, int maxTicketsAvailable,
                 LocalDateTime creationDateTime) {
        this(matchId, visitorTeam, dateTime, ticketPrice, maxTicketsAvailable);
        this.creationDateTime = (creationDateTime != null) ? creationDateTime.withNano(0) : null;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getVisitorTeam() {
        return visitorTeam;
    }

    public void setVisitorTeam(String visitorTeam) {
        this.visitorTeam = visitorTeam;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }
    public void setTicketPrice(float ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getMaxTicketsAvailable() {
        return maxTicketsAvailable;
    }

    public void setMaxTicketsAvailable(int maxTicketsAvailable) {
        this.maxTicketsAvailable = maxTicketsAvailable;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = (creationDateTime != null) ? creationDateTime.withNano(0) : null;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Double.compare(ticketPrice, match.ticketPrice) == 0 && maxTicketsAvailable == match.maxTicketsAvailable && Objects.equals(matchId, match.matchId) && Objects.equals(visitorTeam, match.visitorTeam) && Objects.equals(dateTime, match.dateTime) && Objects.equals(creationDateTime, match.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, visitorTeam, dateTime, ticketPrice, maxTicketsAvailable, creationDateTime);
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchId=" + matchId +
                ", ticketPrice=" + ticketPrice +
                ", soldTickets=" + soldTickets +
                ", maxTicketsAvailable=" + maxTicketsAvailable +
                ", visitorTeam='" + visitorTeam + '\'' +
                ", dateTime=" + dateTime +
                ", creationDateTime=" + creationDateTime +
                '}';
    }
}
