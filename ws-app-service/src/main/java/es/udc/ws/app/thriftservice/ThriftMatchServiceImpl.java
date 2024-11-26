package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchservice.MatchService;
import es.udc.ws.app.model.matchservice.MatchServiceFactory;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.app.model.matchservice.exceptions.MismatchedCardNumberException;
import es.udc.ws.app.model.matchservice.exceptions.TicketsAlreadyCollectedException;
import es.udc.ws.app.model.sale.Sale;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.TException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThriftMatchServiceImpl implements ThriftMatchService.Iface {
    @Override
    public ThriftMatchDto addMatch(ThriftMatchDto matchDto) throws ThriftInputValidationException {
        Match match = MatchToThriftMatchDtoConversor.toMatch(matchDto);
        try{
            Match addedMatch = MatchServiceFactory.getService().addMatch(match);
            return MatchToThriftMatchDtoConversor.toThriftMatchDto(addedMatch);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public ThriftMatchDto findMatch(long matchId) throws TException {
        try {
            MatchService service = MatchServiceFactory.getService();
            Match match = service.findMatch(matchId);
            return MatchToThriftMatchDtoConversor.toThriftMatchDto(match);
        } catch (InstanceNotFoundException e){
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        }
    }

    @Override
    public ThriftSaleDto buyMatch(long matchId, String userId, int numTicketsSale, String creditCardNumber) throws TException {
        try {

            Sale sale = MatchServiceFactory.getService().buyMatch(matchId, userId, creditCardNumber, numTicketsSale);
            return SaleToThriftSaleDtoConversor.toThriftSaleDto(sale);

        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (MatchFinishedException e){
            throw new ThriftClientMatchFinishedException(e.getMatchId());
        } catch (MatchNoMoreTicketsException e){
            throw new ThriftClientMatchNoMoreTicketsException(e.getMatchId(), e.getNumTicketsSale(), e.getAvailableTickets());
        }
    }



    @Override
    public List<ThriftMatchDto> findMatches(String date) {
        LocalDateTime dateTime = LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay();
        MatchService service = MatchServiceFactory.getService();
        List<Match> matches = service.findMatchesByDateRange(LocalDateTime.now(), dateTime);

        return MatchToThriftMatchDtoConversor.toThriftMatchDtos(matches);
    }


    @Override
    public List<ThriftSaleDto> findSalesByUser(String userId) {
        List<Sale> sales = MatchServiceFactory.getService().findSalesByUser(userId);

        return SaleToThriftSaleDtoConversor.toThriftSaleDto(sales);
    }

    @Override
    public void markTicketsCollected(long saleId, String lastFourDigits) throws TException {

        try {
            MatchServiceFactory.getService().markTicketsCollected(saleId, lastFourDigits);

        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));

        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());

        } catch (MismatchedCardNumberException e) {
            throw new ThriftClientMismatchedCardNumberException(e.getSaleId());

        } catch (TicketsAlreadyCollectedException e) {
            throw new ThriftClientTicketsAlreadyCollectedException(e.getSaleId());
        }
    }
}
