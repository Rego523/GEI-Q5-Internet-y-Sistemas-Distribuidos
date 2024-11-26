package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.app.model.matchservice.exceptions.MismatchedCardNumberException;
import es.udc.ws.app.model.matchservice.exceptions.TicketsAlreadyCollectedException;

public class AppExceptionToJsonConversor {
    public static ObjectNode toMatchFinishedException(MatchFinishedException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "MatchFinished");

        exceptionObject.put("matchId", (ex.getMatchId() != null) ? ex.getMatchId() : null);

        return exceptionObject;
    }

    public static ObjectNode toMatchNoMoreTicketsException(MatchNoMoreTicketsException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "MatchNoMoreTickets");

        exceptionObject.put("matchId", (ex.getMatchId() != null) ? ex.getMatchId() : null);

        exceptionObject.put("numTicketsSale", ex.getNumTicketsSale());
        exceptionObject.put("availableTickets", ex.getAvailableTickets());

        return exceptionObject;
    }

    public static ObjectNode toMismatchedCardNumberException (MismatchedCardNumberException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "MismatchedCardNumber");

        exceptionObject.put("saleId", (ex.getSaleId() != null) ? ex.getSaleId() : null);


        return exceptionObject;
    }

    public static ObjectNode toTicketsAlreadyCollectedException (TicketsAlreadyCollectedException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "TicketsAlreadyCollected");

        exceptionObject.put("saleId", (ex.getSaleId() != null) ? ex.getSaleId() : null);

        return exceptionObject;
    }
}
