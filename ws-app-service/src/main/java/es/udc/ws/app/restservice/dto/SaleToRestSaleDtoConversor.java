package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.sale.Sale;

import java.util.ArrayList;
import java.util.List;

public class SaleToRestSaleDtoConversor {

    public static RestSaleDto toRestSaleDto(Sale sale) {
        return new RestSaleDto(sale.getSaleId(), sale.getMatchId(), sale.getUserId(),
                sale.getCreditCardNumber(), sale.getNumTicketsSale(),
                sale.getSaleDate(), sale.isTicketCollected());
    }

    public static List<RestSaleDto> toRestSaleDto(List<Sale> saleList) {
        List<RestSaleDto> saleDtos = new ArrayList<>(saleList.size());
        for (Sale sale : saleList) {
            saleDtos.add(toRestSaleDto(sale));
        }
        return saleDtos;
    }
}
