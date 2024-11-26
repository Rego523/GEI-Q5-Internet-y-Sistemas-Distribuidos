package es.udc.ws.app.model.match;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

abstract public class AbstractSqlMatchDao implements SqlMatchDao{

    protected AbstractSqlMatchDao() {
    }

    @Override
    public Match find(Connection connection, Long matchId) throws InstanceNotFoundException{
        String queryString = "SELECT visitorTeam, dateTime, ticketPrice, maxTicketsAvailable, creationDateTime, soldTickets "
                + "FROM `Match` WHERE matchId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i=1;
            preparedStatement.setLong(i++, matchId.longValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()){
                throw new InstanceNotFoundException(matchId, Match.class.getName());
            }

            i=1;
            String visitorTeam = resultSet.getString(i++);
            LocalDateTime dateTime = resultSet.getTimestamp(i++).toLocalDateTime();
            float ticketPrice = resultSet.getFloat(i++);
            int maxTicketsAvailable = resultSet.getInt(i++);
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDateTime = creationDateAsTimestamp.toLocalDateTime();
            int soldTickets = resultSet.getInt(i++);

            Match match = new Match(matchId, visitorTeam, dateTime, ticketPrice, maxTicketsAvailable, creationDateTime);
            match.setSoldTickets(soldTickets);

            return match;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Match> findMatchesByDateRange(Connection connection, LocalDateTime startDate, LocalDateTime endDate) {

        /* Create "queryString" */

        String queryString = "SELECT matchId, visitorTeam, dateTime, ticketPrice, maxTicketsAvailable, creationDateTime, soldTickets "
                + "FROM `Match`";

        if (startDate != null && endDate != null) {
            queryString += " WHERE dateTime BETWEEN ? AND ?";
        }
        queryString += " ORDER BY dateTime";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            /* Fill "preparedStatement". */
            if (startDate != null && endDate != null) {
                int i = 1;
                preparedStatement.setTimestamp(i++, Timestamp.valueOf(startDate));
                preparedStatement.setTimestamp(i++, Timestamp.valueOf(endDate));
            }

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read matches. */
            List<Match> matches = new ArrayList<>();

            while(resultSet.next()){

                int i = 1;
                Long matchId = Long.valueOf(resultSet.getLong(i++));
                String visitorTeam = resultSet.getString(i++);
                LocalDateTime dateTime = resultSet.getTimestamp(i++).toLocalDateTime();
                float ticketPrice = resultSet.getFloat(i++);
                int maxTicketsAvailable = resultSet.getInt(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDateTime = creationDateAsTimestamp.toLocalDateTime();
                int soldTickets = resultSet.getInt(i++);

                Match match = new Match(matchId, visitorTeam, dateTime, ticketPrice, maxTicketsAvailable,
                        creationDateTime);

                match.setSoldTickets(soldTickets);

                matches.add(match);
            }

            /* Return matches */
            return matches;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Match match) throws InstanceNotFoundException {
        /* Create "queryString */
        String queryString = "UPDATE `Match` SET visitorTeam = ?, dateTime = ?, ticketPrice = ?, " +
                "maxTicketsAvailable = ?, soldTickets = ? WHERE matchId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparatedStatement" */

            int i = 1;
            preparedStatement.setString(i++, match.getVisitorTeam());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getDateTime()));
            preparedStatement.setFloat(i++, match.getTicketPrice());
            preparedStatement.setInt(i++, match.getMaxTicketsAvailable());
            preparedStatement.setInt(i++, match.getSoldTickets());
            preparedStatement.setLong(i++, match.getMatchId());

            /* Execute query. */

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(match.getMatchId(),
                        Match.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long matchId)
        throws InstanceNotFoundException{

        /* Create "quearyString". */
        String quearyString = "DELETE FROM `Match` WHERE" + " matchId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(quearyString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, matchId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();
            if (removedRows == 0) {
                throw new InstanceNotFoundException(matchId, Match.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
