package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;

public class RestSaleDto {

    private Long saleId;
    private Long matchId;
    private String userId;
    private String lastFourDigits; // Solo los últimos 4 dígitos de la tarjeta
    private int numTicketsSale;
    private LocalDateTime saleDate;
    private boolean isTicketCollected;

    public RestSaleDto() {
    }

    public RestSaleDto(Long saleId, Long matchId, String userId, String creditCardNumber,
                       int numTicketsSale, LocalDateTime saleDate, boolean isTicketCollected) {
        this.saleId = saleId;
        this.matchId = matchId;
        this.userId = userId;
        this.lastFourDigits = extractLastFourDigits(creditCardNumber);
        this.numTicketsSale = numTicketsSale;
        this.saleDate = saleDate;
        this.isTicketCollected = isTicketCollected;
    }

    // Getters and setters

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

    // Método privado para extraer los últimos 4 dígitos de la tarjeta
    public static String extractLastFourDigits(String creditCardNumber) {
        if (creditCardNumber != null && creditCardNumber.length() >= 4) {
            return creditCardNumber.substring(creditCardNumber.length() - 4);
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return "RestSaleDto{" +
                "saleId=" + saleId +
                ", matchId=" + matchId +
                ", userId='" + userId + '\'' +
                ", lastFourDigits='" + lastFourDigits + '\'' +
                ", numTicketsSale=" + numTicketsSale +
                ", saleDate=" + saleDate +
                ", isTicketCollected=" + isTicketCollected +
                '}';
    }
}