package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.match.Match;

import java.util.ArrayList;
import java.util.List;

public class MatchToRestMatchDtoConversor {

    public static List<RestMatchDto> toRestMatchDtos(List<Match> matches) {
        List<RestMatchDto> matchDtos = new ArrayList<>(matches.size());
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            matchDtos.add(toRestMatchDto(match));
        }
        return matchDtos;
    }

    public static RestMatchDto toRestMatchDto(Match match) {
        RestMatchDto restMatchDto = new RestMatchDto(match.getMatchId(), match.getVisitorTeam(), match.getDateTime(),
                match.getTicketPrice(), match.getMaxTicketsAvailable());
        restMatchDto.setSoldTickets(match.getSoldTickets());
        return restMatchDto;
    }

    public static Match toMatch(RestMatchDto matchDto) {
        Match match = new Match(matchDto.getMatchId(), matchDto.getVisitorTeam(), matchDto.getDateTime(),
                matchDto.getTicketPrice(), matchDto.getMaxTicketsAvailable());
        match.setSoldTickets(matchDto.getSoldTickets());
        return match;
    }
}