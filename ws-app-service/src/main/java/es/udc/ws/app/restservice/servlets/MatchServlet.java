package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchservice.MatchServiceFactory;
import es.udc.ws.app.restservice.dto.MatchToRestMatchDtoConversor;
import es.udc.ws.app.restservice.dto.RestMatchDto;
import es.udc.ws.app.restservice.json.JsonToRestMatchDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class MatchServlet extends RestHttpServletTemplate {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException, InstanceNotFoundException{

        String path = ServletUtils.normalizePath(req.getPathInfo());

        if (path != null && !path.trim().isEmpty()){

            Long matchId = ServletUtils.getIdFromPath(req, path);

            Match match = MatchServiceFactory.getService().findMatch(matchId);

            RestMatchDto matchDto = MatchToRestMatchDtoConversor.toRestMatchDto(match);

            ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_OK,
                    JsonToRestMatchDtoConversor.toObjectNode(matchDto), null);

        } else {
            ServletUtils.checkEmptyPath(req);
            String stringDate = req.getParameter("date");

            stringDate = stringDate.replaceAll("'", "");

            LocalDateTime date = LocalDate.parse(stringDate, DateTimeFormatter.ISO_DATE).atStartOfDay();

            List<Match> matches = MatchServiceFactory.getService().findMatchesByDateRange(LocalDateTime.now(),date);

            List<RestMatchDto> matchesDtos = MatchToRestMatchDtoConversor.toRestMatchDtos(matches);
            ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_OK,
                    JsonToRestMatchDtoConversor.toArrayNode(matchesDtos), null);
        }
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {
        ServletUtils.checkEmptyPath(req);

        RestMatchDto matchDto = JsonToRestMatchDtoConversor.toRestMatchDto(req.getInputStream());

        Match match = MatchToRestMatchDtoConversor.toMatch(matchDto);
        match = MatchServiceFactory.getService().addMatch(match);

        matchDto = MatchToRestMatchDtoConversor.toRestMatchDto(match);

        String matchURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + match.getMatchId();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", matchURL);

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestMatchDtoConversor.toObjectNode(matchDto), headers);
    }
}
