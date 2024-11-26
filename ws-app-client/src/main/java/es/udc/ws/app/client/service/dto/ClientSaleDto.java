package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;

public class ClientSaleDto {
    private Long saleId;
    private Long matchId;
    private String userId;
    private int numTicketsSale;

    private String lastFourDigits; // Solo los últimos 4 dígitos de la tarjeta
    private LocalDateTime saleDate;
    private boolean isTicketCollected;

    public ClientSaleDto() {
    }

    public ClientSaleDto(Long saleId, Long matchId, String userId, String lastFourDigits, int numTicketsSale,
                         LocalDateTime saleDate, boolean isTicketCollected) {
        this.saleId = saleId;
        this.matchId = matchId;
        this.userId = userId;
        this.lastFourDigits = lastFourDigits;
        this.numTicketsSale = numTicketsSale;
        this.saleDate = saleDate;
        this.isTicketCollected = isTicketCollected;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public int getNumTicketsSale() {
        return numTicketsSale;
    }

    public void setNumTicketsSale(int numTicketsSale) {
        this.numTicketsSale = numTicketsSale;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public boolean isTicketCollected() {
        return isTicketCollected;
    }

    public void setTicketCollected(boolean ticketCollected) {
        isTicketCollected = ticketCollected;
    }
}
