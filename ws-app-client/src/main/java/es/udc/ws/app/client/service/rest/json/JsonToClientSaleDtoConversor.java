package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientSaleDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientSaleDtoConversor {

    public static List<ClientSaleDto> toClientSaleDto(InputStream jsonSales) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonSales);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode salesArray = (ArrayNode) rootNode;
                List<ClientSaleDto> saleDtos = new ArrayList<>(salesArray.size());
                for (JsonNode saleNode : salesArray) {
                    saleDtos.add(toClientSaleDto(saleNode));
                }

                return saleDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientSaleDto toClientSaleDto(JsonNode matchNode) throws ParsingException {
        if (matchNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode matchObject = (ObjectNode) matchNode;

            JsonNode saleIdNode = matchObject.get("saleId");
            Long saleId = (saleIdNode != null) ? saleIdNode.longValue() : null;

            JsonNode matchIdNode = matchObject.get("matchId");
            Long matchId = (matchIdNode != null) ? matchIdNode.longValue() : null;

            String userId = matchObject.get("userId").textValue().trim();
            String lastFourDigits = matchObject.get("lastFourDigits").textValue().trim();

            int numTicketsSale = matchObject.get("numTicketsSale").intValue();

            boolean isTicketColected = matchObject.get("isTicketColected").booleanValue();

            String saleDateString = matchObject.get("saleDate").textValue().trim();
            LocalDateTime saleDate = LocalDateTime.parse(saleDateString);

            return new ClientSaleDto(saleId, matchId, userId, lastFourDigits,
                    numTicketsSale, saleDate, isTicketColected);
        }
    }

    public static ClientSaleDto toSingleClientSaleDto(InputStream jsonSale) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonSale);
            return toClientSaleDto(rootNode);
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

}