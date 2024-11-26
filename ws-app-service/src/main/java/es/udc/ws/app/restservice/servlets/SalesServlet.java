package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.matchservice.MatchServiceFactory;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.app.model.matchservice.exceptions.MismatchedCardNumberException;
import es.udc.ws.app.model.matchservice.exceptions.TicketsAlreadyCollectedException;
import es.udc.ws.app.model.sale.Sale;
import es.udc.ws.app.restservice.dto.RestSaleDto;
import es.udc.ws.app.restservice.dto.SaleToRestSaleDtoConversor;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestSaleDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class SalesServlet extends RestHttpServletTemplate {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException {

        String userId = ServletUtils.getMandatoryParameter(req, "userId");
        List<Sale> saleList;
        List<RestSaleDto> saleDtoList = new ArrayList<>();

        saleList = MatchServiceFactory.getService().findSalesByUser(userId);

        if(!saleList.isEmpty()) {
            saleDtoList = SaleToRestSaleDtoConversor.toRestSaleDto(saleList);
        }

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestSaleDtoConversor.toArrayNode(saleDtoList), null);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {

        String path = ServletUtils.normalizePath(req.getPathInfo());

        if (path != null && !path.trim().isEmpty()) {

            Long saleId = ServletUtils.getIdFromPath(req, path);
            String creditCardNumber = ServletUtils.getMandatoryParameter(req, "creditCardNumber");

            try {
                MatchServiceFactory.getService().markTicketsCollected(saleId, creditCardNumber);
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK, null, null);
            } catch (TicketsAlreadyCollectedException ex) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toTicketsAlreadyCollectedException(ex), null);
            } catch (MismatchedCardNumberException ex) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toMismatchedCardNumberException(ex), null);
            }
        } else {
            ServletUtils.checkEmptyPath(req);

            Long matchId = Long.valueOf(ServletUtils.getMandatoryParameter(req, "id"));
            String userId = ServletUtils.getMandatoryParameter(req, "userId");
            String creditCardNumber = ServletUtils.getMandatoryParameter(req, "creditCardNumber");
            int numTickets = Integer.parseInt(ServletUtils.getMandatoryParameter(req, "numTickets"));

            try {
                Sale sale = MatchServiceFactory.getService().buyMatch(matchId, userId, creditCardNumber, numTickets);
                RestSaleDto saleDto = SaleToRestSaleDtoConversor.toRestSaleDto(sale);
                String saleURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + sale.getSaleId().toString();
                Map<String, String> headers = new HashMap<>(1);
                headers.put("Location", saleURL);
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                        JsonToRestSaleDtoConversor.toObjectNode(saleDto), headers);
            } catch (MatchFinishedException ex) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toMatchFinishedException(ex), null);
            } catch (MatchNoMoreTicketsException ex) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        AppExceptionToJsonConversor.toMatchNoMoreTicketsException(ex), null);
            }
        }
    }
}


