package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.exceptions.ClientMatchFinishedException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNoMoreTicketsException;
import es.udc.ws.app.client.service.exceptions.ClientMismatchedCardNumberException;
import es.udc.ws.app.client.service.exceptions.ClientTicketsAlreadyCollectedException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;

public class JsonToClientExceptionConversor {

    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InstanceNotFound")) {
                    return toInstanceNotFoundException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }


    public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("MatchFinished")) {
                    return toClientMatchFinishedException(rootNode);
                }
                if (errorType.equals("MatchNoMoreTickets")) {
                    return toClientMatchNoMoreTicketsException(rootNode);
                }
                if (errorType.equals("MismatchedCardNumber")) {
                    return toClientMismatchedCardNumberException(rootNode);
                }
                if (errorType.equals("TicketsAlreadyCollected")) {
                    return toClientTicketsAlreadyCollectedException(rootNode);
                }else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
    private static ClientMatchFinishedException toClientMatchFinishedException(JsonNode rootNode) {
        Long matchId = rootNode.get("matchId").longValue();
        return new ClientMatchFinishedException(matchId);
    }

    private static ClientMatchNoMoreTicketsException toClientMatchNoMoreTicketsException(JsonNode rootNode) {
        Long matchId = rootNode.get("matchId").longValue();
        int numTicketsSale = rootNode.get("numTicketsSale").intValue();
        int availableTickets = rootNode.get("availableTickets").intValue();
        return new ClientMatchNoMoreTicketsException(matchId, numTicketsSale, availableTickets);
    }

    private static ClientMismatchedCardNumberException toClientMismatchedCardNumberException(JsonNode rootNode) {
        Long saleId = rootNode.get("saleId").longValue();
        return new ClientMismatchedCardNumberException(saleId);
    }

    private static ClientTicketsAlreadyCollectedException toClientTicketsAlreadyCollectedException(JsonNode rootNode) {
        Long saleId = rootNode.get("saleId").longValue();
        return new ClientTicketsAlreadyCollectedException(saleId);
    }

}
