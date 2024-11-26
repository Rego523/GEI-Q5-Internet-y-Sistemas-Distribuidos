package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientSaleDto;
import es.udc.ws.app.client.service.exceptions.ClientMatchFinishedException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNoMoreTicketsException;
import es.udc.ws.app.client.service.exceptions.ClientMismatchedCardNumberException;
import es.udc.ws.app.client.service.exceptions.ClientTicketsAlreadyCollectedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.util.List;

public interface ClientMatchService {

    public Long addMatch(ClientMatchDto match)
            throws InputValidationException;

    public Long buyMatch(Long matchId, String userId, int numTicketsSale,  String creditCardNumber)
            throws InstanceNotFoundException, InputValidationException, ClientMatchNoMoreTicketsException,
            ClientMatchFinishedException, MatchFinishedException, MatchNoMoreTicketsException;

    public ClientMatchDto findMatch(Long matchId)
            throws InstanceNotFoundException;

    public List<ClientMatchDto> findMatchesByDateRange(String date);

    public List<ClientSaleDto> findSalesByUser(String userId);

    public void markTicketsCollected(Long saleId, String creditCardNumber)
            throws InstanceNotFoundException, ClientMismatchedCardNumberException,
            ClientTicketsAlreadyCollectedException, InputValidationException;
    
}
