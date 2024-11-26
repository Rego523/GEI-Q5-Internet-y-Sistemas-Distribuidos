package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestSaleDto;

import java.util.List;

public class JsonToRestSaleDtoConversor {

    public static ObjectNode toObjectNode(RestSaleDto sale) {

        ObjectNode saleNode = JsonNodeFactory.instance.objectNode();

        if (sale.getSaleId() != null) {
            saleNode.put("saleId", sale.getSaleId());
        }
        saleNode.put("matchId", sale.getMatchId()).
                put("userId", sale.getUserId()).
                put("lastFourDigits", sale.getLastFourDigits()).
                put("numTicketsSale", sale.getNumTicketsSale()).
                put("isTicketColected", sale.isTicketCollected()).
                put("saleDate", sale.getSaleDate().toString());

        return saleNode;
    }

    public static ArrayNode toArrayNode(List<RestSaleDto> saleDtoList) {

        ArrayNode salesNode = JsonNodeFactory.instance.arrayNode();
        for (RestSaleDto saleDto : saleDtoList) {
            ObjectNode saleObject = toObjectNode(saleDto);
            salesNode.add(saleObject);
        }

        return salesNode;
    }
}
