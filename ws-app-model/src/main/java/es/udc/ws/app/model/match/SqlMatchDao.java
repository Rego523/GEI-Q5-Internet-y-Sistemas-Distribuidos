package es.udc.ws.app.model.match;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlMatchDao {
    public Match create(Connection connection, Match match);

    public Match find(Connection connection, Long matchId) throws InstanceNotFoundException;

    public List<Match> findMatchesByDateRange(Connection connection,
                                              LocalDateTime startDate,
                                              LocalDateTime endDate);

    public void update(Connection connection, Match match)
        throws InstanceNotFoundException;
    public void remove(Connection connection, Long matchId)
        throws InstanceNotFoundException;
}
