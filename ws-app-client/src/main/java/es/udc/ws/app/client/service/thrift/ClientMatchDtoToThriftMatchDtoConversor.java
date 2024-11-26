package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.thrift.ThriftMatchDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientMatchDtoToThriftMatchDtoConversor {

    public static ThriftMatchDto toThriftMatchDto(ClientMatchDto clientMatchDto) {
        Long matchId = clientMatchDto.getMatchId();

        return new ThriftMatchDto(
                matchId == null ? -1 : matchId.longValue(),
                clientMatchDto.getVisitorTeam(),
                clientMatchDto.getDateTime().toString(),
                clientMatchDto.getTicketPrice(),
                clientMatchDto.getSoldTickets(),
                clientMatchDto.getMaxTicketsAvailable()
        );
    }

    public static List<ClientMatchDto> toClientMatchDtos(List<ThriftMatchDto> matches) {
        List<ClientMatchDto> clientMatchDtos = new ArrayList<>(matches.size());
        for (ThriftMatchDto match : matches) {
            clientMatchDtos.add(toClientMatchDto(match));
        }
        return clientMatchDtos;
    }

    public static ClientMatchDto toClientMatchDto(ThriftMatchDto match) {

        return new ClientMatchDto(
                match.getMatchId(),
                match.getVisitorTeam(),
                LocalDateTime.parse(match.getDateTime()),
                Double.valueOf(match.getTicketPrice()).floatValue(),
                match.getMaxTicketsAvailable()
        );
    }
}
