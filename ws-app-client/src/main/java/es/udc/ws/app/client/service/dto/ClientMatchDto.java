package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;

public class ClientMatchDto {

    private Long matchId;
    private String visitorTeam;
    private LocalDateTime dateTime;
    private float ticketPrice;
    private int maxTicketsAvailable;

    private int soldTickets;

    public ClientMatchDto(Long matchId, String visitorTeam, LocalDateTime dateTime, float ticketPrice,
                          int maxTicketsAvailable) {
        this.matchId = matchId;
        this.visitorTeam = visitorTeam;
        this.dateTime = dateTime;
        this.ticketPrice = ticketPrice;
        this.maxTicketsAvailable = maxTicketsAvailable;
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

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

    @Override
    public String toString() {
        return "MatchDto [matchId=" + matchId + ", visitorTeam=" + visitorTeam
                + ", dateTime=" + dateTime + " ticketPrice, " + ticketPrice
                + ", maxTicketsAvailable=" + maxTicketsAvailable  + ", soldTickets" + soldTickets + "]";
    }
}
