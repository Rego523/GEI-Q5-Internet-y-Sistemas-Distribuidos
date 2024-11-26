package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientSaleDto;
import es.udc.ws.app.thrift.ThriftSaleDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientSaleDtoToThriftSaleDtoConversor {

    public static List<ClientSaleDto> toClientSaleDtos(List<ThriftSaleDto> sales) {
        List<ClientSaleDto> clientSalesDtos = new ArrayList<>(sales.size());
        for (ThriftSaleDto sale : sales) {
            clientSalesDtos.add(toClientSaleDto(sale));
        }
        return clientSalesDtos;
    }

    private static ClientSaleDto toClientSaleDto(ThriftSaleDto sale) {

        return new ClientSaleDto(
                sale.getSaleId(),
                sale.getMatchId(),
                sale.getUserId(),
                sale.getLastFourDigits(),
                (int) sale.getNumTicketsSale(),
                LocalDateTime.parse(sale.getSaleDate()),
                sale.isTicketCollected
        );
    }
}