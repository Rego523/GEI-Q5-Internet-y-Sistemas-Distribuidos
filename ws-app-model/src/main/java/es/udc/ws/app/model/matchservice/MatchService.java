package es.udc.ws.app.model.matchservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.app.model.matchservice.exceptions.MismatchedCardNumberException;
import es.udc.ws.app.model.matchservice.exceptions.TicketsAlreadyCollectedException;
import es.udc.ws.app.model.sale.Sale;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchService {

    public Match addMatch(Match match) throws InputValidationException;

    public List<Match> findMatchesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    public Match findMatch(Long matchId) throws InstanceNotFoundException;

    public Sale buyMatch(Long matchId, String userId, String creditCardNumber, int numTicketsSale)
            throws InstanceNotFoundException, InputValidationException, MatchFinishedException, MatchNoMoreTicketsException;

    public List<Sale> findSalesByUser(String userId);

    public void markTicketsCollected(Long saleId, String creditCardNumber)
            throws InstanceNotFoundException, MismatchedCardNumberException, TicketsAlreadyCollectedException, InputValidationException;
}
