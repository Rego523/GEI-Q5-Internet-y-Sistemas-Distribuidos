package es.udc.ws.app.model.sale;

import java.time.LocalDateTime;
import java.util.Objects;

public class Sale {
    private Long saleId;
    private Long matchId;
    private String userId;
    private String creditCardNumber;
    private int numTicketsSale;
    private LocalDateTime saleDate;
    private boolean isTicketCollected = false;

    public Sale(Long matchId, String userId, String creditCardNumber, int numTicketsSale,
                LocalDateTime saleDate) {
        this.matchId = matchId;
        this.userId = userId;
        this.creditCardNumber = creditCardNumber;
        this.numTicketsSale = numTicketsSale;
        this.saleDate = (saleDate != null) ? saleDate.withNano(0) : null;
    }

    public Sale(Long saleId, Long matchId, String userId, String creditCardNumber, int numTicketsSale,
                LocalDateTime saleDate) {
        this(matchId, userId, creditCardNumber, numTicketsSale, saleDate);
        this.saleId = saleId;
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

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
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
        this.saleDate = (saleDate != null) ? saleDate.withNano(0) : null;
    }

    public boolean isTicketCollected() {
        return isTicketCollected;
    }

    public void setTicketCollected(boolean ticketCollected) {
        isTicketCollected = ticketCollected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return numTicketsSale == sale.numTicketsSale && isTicketCollected == sale.isTicketCollected && Objects.equals(saleId, sale.saleId) && Objects.equals(matchId, sale.matchId) && Objects.equals(userId, sale.userId) && Objects.equals(creditCardNumber, sale.creditCardNumber) && Objects.equals(saleDate, sale.saleDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId, matchId, userId, creditCardNumber, numTicketsSale, saleDate, isTicketCollected);
    }
}
