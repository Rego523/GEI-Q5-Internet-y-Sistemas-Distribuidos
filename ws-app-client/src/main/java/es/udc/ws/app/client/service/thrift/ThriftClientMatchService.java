package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientSaleDto;
import es.udc.ws.app.client.service.exceptions.ClientMatchFinishedException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNoMoreTicketsException;
import es.udc.ws.app.client.service.exceptions.ClientMismatchedCardNumberException;
import es.udc.ws.app.client.service.exceptions.ClientTicketsAlreadyCollectedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.List;

public class ThriftClientMatchService implements ClientMatchService {

    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientMatchService.endpointAddress";

    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);

    @Override
    public Long addMatch(ClientMatchDto match) throws InputValidationException {
        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {
            transport.open();

            return client.addMatch(ClientMatchDtoToThriftMatchDtoConversor.toThriftMatchDto(match)).getMatchId();
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long buyMatch(Long matchId, String userId, int numTicketsSale, String creditCardNumber)
            throws InstanceNotFoundException, InputValidationException, ClientMatchNoMoreTicketsException, ClientMatchFinishedException {

        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return client.buyMatch(matchId, userId, numTicketsSale, creditCardNumber).getSaleId();

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch ( ThriftClientMatchFinishedException e){
            throw new ClientMatchFinishedException(e.getMatchId());
        } catch (ThriftClientMatchNoMoreTicketsException e){
            throw new ClientMatchNoMoreTicketsException(e.getMatchId(), e.getNumTicketsSale(), e.getAvailableTickets());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ClientMatchDto findMatch(Long matchId) throws InstanceNotFoundException {
        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return ClientMatchDtoToThriftMatchDtoConversor.toClientMatchDto(client.findMatch(matchId));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientMatchDto> findMatchesByDateRange(String date) {
        ThriftMatchService.Client client = getClient();
        try (TTransport transport = client.getInputProtocol().getTransport()) {
            transport.open();
            return ClientMatchDtoToThriftMatchDtoConversor.toClientMatchDtos(client.findMatches(date));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientSaleDto> findSalesByUser(String userId) {

        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return ClientSaleDtoToThriftSaleDtoConversor.toClientSaleDtos(client.findSalesByUser(userId));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markTicketsCollected(Long saleId, String creditCardNumber)
            throws InstanceNotFoundException, ClientMismatchedCardNumberException,
            ClientTicketsAlreadyCollectedException, InputValidationException{

        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();
            client.markTicketsCollected(saleId, creditCardNumber);

        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());

        } catch (ThriftClientMismatchedCardNumberException e) {
            throw new ClientMismatchedCardNumberException(e.getSaleId());

        } catch (ThriftClientTicketsAlreadyCollectedException e) {
            throw new ClientTicketsAlreadyCollectedException(e.getSaleId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private ThriftMatchService.Client getClient(){
        try{
            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);
            return new ThriftMatchService.Client(protocol);
        } catch (TTransportException e){
            throw new RuntimeException(e);
        }
    }
}
