package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestMatchDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonToRestMatchDtoConversor {
    public static ObjectNode toObjectNode(RestMatchDto match){
        ObjectNode matchObject = JsonNodeFactory.instance.objectNode();
        matchObject.put("matchId", match.getMatchId()).
                put("visitorTeam", match.getVisitorTeam()).
                put("dateTime", match.getDateTime().toString()).
                put("ticketPrice", match.getTicketPrice()).
                put("maxTicketsAvailable", match.getMaxTicketsAvailable()).
                put("soldTickets", match.getSoldTickets());
        return matchObject;
    }
    public static ArrayNode toArrayNode(List<RestMatchDto> matches){
        ArrayNode matchesNode = JsonNodeFactory.instance.arrayNode();
        for (RestMatchDto matchDto : matches) {
            ObjectNode matchObject = toObjectNode(matchDto);
            matchesNode.add(matchObject);
        }
        return matchesNode;
    }

    public static RestMatchDto toRestMatchDto(InputStream jsonMatch) throws ParsingException{
        try{
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonMatch);

            if(rootNode.getNodeType() != JsonNodeType.OBJECT){
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode matchObject = (ObjectNode) rootNode;

                JsonNode matchIdNode = matchObject.get("matchId");
                Long matchId = (matchIdNode != null) ? matchIdNode.longValue() : null;

                String visitorTeam = matchObject.get("visitorTeam").textValue().trim();
                String stringDateTime = matchObject.get("dateTime").textValue().trim();
                float ticketPrice = matchObject.get("ticketPrice").floatValue();
                int maxTicketsAvailable = matchObject.get("maxTicketsAvailable").intValue();

                LocalDateTime date = LocalDateTime.parse(stringDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

                return new RestMatchDto(matchId, visitorTeam, date, ticketPrice, maxTicketsAvailable);
            }
        } catch (ParsingException ex){
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

}
