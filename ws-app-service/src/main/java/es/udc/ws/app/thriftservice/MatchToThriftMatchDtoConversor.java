package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.thrift.ThriftMatchDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MatchToThriftMatchDtoConversor {
    public static Match toMatch(ThriftMatchDto match) {
        LocalDateTime dateTime = LocalDateTime.parse(match.dateTime);
        return new Match(match.getMatchId(), match.getVisitorTeam(), dateTime, (float) match.getTicketPrice(),
                match.maxTicketsAvailable);
    }

    public static List<ThriftMatchDto> toThriftMatchDtos(List<Match> matches) {
        List<ThriftMatchDto> dtos = new ArrayList<>(matches.size());
        for (Match match : matches) {
            dtos.add(toThriftMatchDto(match));
        }
        return dtos;
    }

    public static ThriftMatchDto toThriftMatchDto(Match match) {
        return new ThriftMatchDto(match.getMatchId(), match.getVisitorTeam(), match.getDateTime().toString(),
                match.getTicketPrice(), match.getSoldTickets(),match.getMaxTicketsAvailable());
    }
}