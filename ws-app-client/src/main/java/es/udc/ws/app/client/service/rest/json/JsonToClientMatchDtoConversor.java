package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientMatchDtoConversor {

    public static ObjectNode toObjectNode(ClientMatchDto match) throws IOException {

        ObjectNode matchObject = JsonNodeFactory.instance.objectNode();

        if (match.getMatchId() != null) {
            matchObject.put("matchId", match.getMatchId());
        }
        matchObject.put("visitorTeam", match.getVisitorTeam()).
                put("dateTime", match.getDateTime().toString()).
                put("ticketPrice", match.getTicketPrice()).
                put("maxTicketsAvailable", match.getMaxTicketsAvailable());

        return matchObject;
    }

    public static ClientMatchDto toClientMatchDto(InputStream jsonMatch) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonMatch);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                return toClientMatchDto(rootNode);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientMatchDto toClientMatchDto(JsonNode matchNode) throws ParsingException {
        if (matchNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode matchObject = (ObjectNode) matchNode;

            JsonNode matchIdNode = matchObject.get("matchId");
            Long matchId = (matchIdNode != null) ? matchIdNode.longValue() : null;

            String visitorTeam = matchObject.get("visitorTeam").textValue().trim();

            String dateTimeString = matchObject.get("dateTime").textValue().trim();
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);

            float ticketPrice = matchObject.get("ticketPrice").floatValue();
            int maxTicketsAvailable = matchObject.get("maxTicketsAvailable").intValue();
            int soldtickets = matchObject.get("soldTickets").intValue();

            ClientMatchDto clientMatchDto = new ClientMatchDto(matchId, visitorTeam, dateTime, ticketPrice,
                    maxTicketsAvailable);
            clientMatchDto.setSoldTickets(soldtickets);
            return clientMatchDto;
        }
    }

    public static List<ClientMatchDto> toClientMatchDtos(InputStream jsonMatches) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonMatches);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode matchesArray = (ArrayNode) rootNode;
                List<ClientMatchDto> matchDtos = new ArrayList<>(matchesArray.size());
                for (JsonNode matchNode : matchesArray) {
                    matchDtos.add(toClientMatchDto(matchNode));
                }

                return matchDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
