package es.udc.ws.app.model.sale;

import es.udc.ws.app.model.matchservice.exceptions.MismatchedCardNumberException;
import es.udc.ws.app.model.matchservice.exceptions.TicketsAlreadyCollectedException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlSaleDao implements SqlSaleDao {

    protected AbstractSqlSaleDao() {
    }

    @Override
    public Sale find(Connection connection, Long saleId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "SELECT matchId, userId, creditCardNumber,"
                + " numTicketsSale, saleDate, isTicketCollected FROM MatchSale WHERE saleId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, saleId.longValue());

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(saleId,
                        Sale.class.getName());
            }

            /* Get results. */
            i = 1;
            Long matchId = resultSet.getLong(i++);
            String userId = resultSet.getString(i++);
            String creditCardNumber = resultSet.getString(i++);
            int numTicketsSale = resultSet.getInt(i++);
            Timestamp saleDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime saleDate = saleDateAsTimestamp.toLocalDateTime();
            boolean isTicketCollected = resultSet.getBoolean(i++);

            /* Return sale. */
            Sale sale = new Sale(saleId, matchId, userId, creditCardNumber,
                    numTicketsSale, saleDate);
            sale.setTicketCollected(isTicketCollected);
            return sale;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Connection connection, Sale sale)
            throws InstanceNotFoundException{

        /* Create "queryString". */
        String queryString = "UPDATE MatchSale"
                + " SET matchId = ?, userId = ?, CreditCardNumber = ?, "
                + " NumTicketsSale = ?, saleDate = ?, isTicketCollected = ? WHERE saleId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, sale.getMatchId());
            preparedStatement.setString(i++, sale.getUserId());
            preparedStatement.setString(i++, sale.getCreditCardNumber());
            preparedStatement.setFloat(i++, sale.getNumTicketsSale());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(sale.getSaleDate()));
            preparedStatement.setBoolean(i++, sale.isTicketCollected());
            preparedStatement.setLong(i++, sale.getSaleId());

            /* Execute query. */
            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(sale.getMatchId(),
                        Sale.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(Connection connection, Long saleId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM MatchSale WHERE" + " saleId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, saleId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(saleId,
                        Sale.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Sale> findSalesByUser(Connection connection, String userId) {
        List<Sale> sales = new ArrayList<>();

        /* Create "queryString". */
        String queryString = "SELECT saleId, matchId, creditCardNumber, numTicketsSale, saleDate, isTicketCollected"
                + " FROM MatchSale WHERE userId = ? ORDER BY saleDate";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            /* Fill "preparedStatement". */
            preparedStatement.setString(1, userId);

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Long saleId = resultSet.getLong("saleId");
                Long matchId = resultSet.getLong("matchId");
                String creditCardNumber = resultSet.getString("creditCardNumber");
                int numTicketsSale = resultSet.getInt("numTicketsSale");
                Timestamp saleDateAsTimestamp = resultSet.getTimestamp("saleDate");
                LocalDateTime saleDate = saleDateAsTimestamp.toLocalDateTime();
                boolean isTicketCollected = resultSet.getBoolean("isTicketCollected");

                Sale sale = new Sale(saleId, matchId, userId, creditCardNumber, numTicketsSale, saleDate);
                sale.setTicketCollected(isTicketCollected);

                sales.add(sale);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return sales;
    }
}