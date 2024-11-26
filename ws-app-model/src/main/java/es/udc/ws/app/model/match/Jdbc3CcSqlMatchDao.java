package es.udc.ws.app.model.match;

import java.sql.*;

public class Jdbc3CcSqlMatchDao extends AbstractSqlMatchDao {
    @Override
    public Match create(Connection connection, Match match) {

        /* Create "queryString" */
        // "MATCH" is a reserved word in SQL, we need to use ``

        String queryString = "INSERT INTO `Match`"
                + " (visitorTeam, dateTime, ticketPrice, maxTicketsAvailable, creationDateTime, soldTickets)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)){
            /* Fill "preparatedStatement" */
            int i = 1;
            preparedStatement.setString(i++, match.getVisitorTeam());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getDateTime()));
            preparedStatement.setFloat(i++, match.getTicketPrice());
            preparedStatement.setInt(i++, match.getMaxTicketsAvailable());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getCreationDateTime()));
            preparedStatement.setInt(i++, match.getSoldTickets());

            /* Execute query */
            preparedStatement.executeUpdate();

            /* Get generated identifier */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()){
                throw new SQLException(
                    "JDBC driver did not return generated key.");
                }
            Long matchId = resultSet.getLong(1);

            /* Return match */

            return new Match(matchId, match.getVisitorTeam(), match.getDateTime(),
                    match.getTicketPrice(), match.getMaxTicketsAvailable(), match.getCreationDateTime());

            } catch (SQLException e){
                throw new RuntimeException(e);
        }
    }
}
