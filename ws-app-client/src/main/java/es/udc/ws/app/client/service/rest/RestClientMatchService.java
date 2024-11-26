package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientSaleDto;
import es.udc.ws.app.client.service.exceptions.ClientMatchFinishedException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNoMoreTicketsException;
import es.udc.ws.app.client.service.exceptions.ClientMismatchedCardNumberException;
import es.udc.ws.app.client.service.exceptions.ClientTicketsAlreadyCollectedException;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientMatchDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientSaleDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RestClientMatchService implements ClientMatchService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientMatchService.endpointAddress";
    private String endpointAddress;

    @Override
    public Long addMatch(ClientMatchDto match) throws InputValidationException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "matches").
                    bodyStream(toInputStream(match), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientMatchDtoConversor.toClientMatchDto(response.getEntity().getContent()).getMatchId();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long buyMatch(Long matchId, String userId, int numTicketsSale,  String creditCardNumber)
            throws InstanceNotFoundException, InputValidationException, ClientMatchNoMoreTicketsException,
            ClientMatchFinishedException  {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "sales").
                    bodyForm(
                            Form.form().
                                    add("id", Long.toString(matchId)).
                                    add("userId", userId).
                                    add("numTickets", Integer.toString(numTicketsSale)).
                                    add("creditCardNumber", creditCardNumber).
                                    build()).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientSaleDtoConversor.toSingleClientSaleDto(
                    response.getEntity().getContent()).getSaleId();

        } catch (InputValidationException | InstanceNotFoundException |
                 ClientMatchNoMoreTicketsException | ClientMatchFinishedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ClientMatchDto findMatch(Long matchId) throws InstanceNotFoundException {
        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() +
                            "matches/" + matchId).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientMatchDtoConversor.toClientMatchDto(response.getEntity()
                    .getContent());
        } catch (InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientMatchDto> findMatchesByDateRange(String date) {
        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "matches?date="
                            + URLEncoder.encode(date, StandardCharsets.UTF_8)).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientMatchDtoConversor.toClientMatchDtos(response.getEntity()
                    .getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientSaleDto> findSalesByUser(String userId) {
        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "sales?userId="
                            + URLEncoder.encode(userId, StandardCharsets.UTF_8)).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientSaleDtoConversor.toClientSaleDto(response.getEntity()
                    .getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markTicketsCollected(Long saleId, String creditCardNumber)
            throws InstanceNotFoundException, ClientMismatchedCardNumberException,
            ClientTicketsAlreadyCollectedException, InputValidationException{

        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "sales/" + saleId + "?creditCardNumber="
                            + URLEncoder.encode(creditCardNumber, StandardCharsets.UTF_8)).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);
        } catch (InstanceNotFoundException | ClientMismatchedCardNumberException | ClientTicketsAlreadyCollectedException |
                InputValidationException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }



    private InputStream toInputStream(ClientMatchDto match) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientMatchDtoConversor.toObjectNode(match));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, ClassicHttpResponse response) throws Exception {

        try {

            int statusCode = response.getCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                        response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = "
                        + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
