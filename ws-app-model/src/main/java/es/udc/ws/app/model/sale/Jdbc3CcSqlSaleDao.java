package es.udc.ws.app.model.sale;

import java.sql.*;


public class Jdbc3CcSqlSaleDao extends AbstractSqlSaleDao {

    @Override
    public Sale create(Connection connection, Sale sale) {

        /* Create "queryString". */
        String queryString = "INSERT INTO MatchSale"
                + " (matchId, userId, creditCardNumber,"
                + " numTicketsSale, saleDate, isTicketCollected) VALUES (?, ?, ?, ?, ?, ?)";


        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, sale.getMatchId());
            preparedStatement.setString(i++, sale.getUserId());
            preparedStatement.setString(i++, sale.getCreditCardNumber());
            preparedStatement.setFloat(i++, sale.getNumTicketsSale());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(sale.getSaleDate()));
            preparedStatement.setBoolean(i++, sale.isTicketCollected());

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long saleId = resultSet.getLong(1);

            /* Return sale. */
            return new Sale(saleId, sale.getMatchId(), sale.getUserId(),
                    sale.getCreditCardNumber(), sale.getNumTicketsSale(),
                    sale.getSaleDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}