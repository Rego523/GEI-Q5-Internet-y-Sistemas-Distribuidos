package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.sale.Sale;
import es.udc.ws.app.thrift.ThriftSaleDto;

import java.util.ArrayList;
import java.util.List;

public class SaleToThriftSaleDtoConversor {

    public static ThriftSaleDto toThriftSaleDto(Sale sale) {

        return new ThriftSaleDto(sale.getSaleId(), sale.getMatchId(), sale.getUserId(), sale.getCreditCardNumber(),
                sale.getNumTicketsSale(), sale.getSaleDate().toString(), sale.isTicketCollected());

    }

    public static List<ThriftSaleDto> toThriftSaleDto(List<Sale> sales) {

        List<ThriftSaleDto> ThriftSaleDtolists = new ArrayList<>(sales.size());

        for (Sale sale : sales) {
            ThriftSaleDtolists.add(toThriftSaleDto(sale));
        }
        return ThriftSaleDtolists;

    }

}