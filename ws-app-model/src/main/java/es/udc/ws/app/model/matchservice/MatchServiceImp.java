package es.udc.ws.app.model.matchservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.match.SqlMatchDao;
import es.udc.ws.app.model.match.SqlMatchDaoFactory;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.app.model.matchservice.exceptions.MismatchedCardNumberException;
import es.udc.ws.app.model.matchservice.exceptions.TicketsAlreadyCollectedException;
import es.udc.ws.app.model.matchservice.validations.CustomPropertyValidator;
import es.udc.ws.app.model.sale.Sale;
import es.udc.ws.app.model.sale.SqlSaleDao;
import es.udc.ws.app.model.sale.SqlSaleDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

public class MatchServiceImp implements MatchService{

    private final DataSource dataSource;
    private SqlMatchDao matchDao = null;
    private SqlSaleDao saleDao = null;

    public MatchServiceImp(){
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        matchDao = SqlMatchDaoFactory.getDao();
        saleDao = SqlSaleDaoFactory.getDao();
    }

    private void validateMatch(Match match) throws InputValidationException{
        PropertyValidator.validateMandatoryString("visitorTeam", match.getVisitorTeam());
        CustomPropertyValidator.validateLocalDateTime("dateTime", match.getDateTime());
        CustomPropertyValidator.validateFloat("ticketPrice", match.getTicketPrice(), 0);
        CustomPropertyValidator.validateInt("maxTicketsAvailable", match.getMaxTicketsAvailable(), 1);
        PropertyValidator.validateLong("soldTickets", match.getSoldTickets(), 0, match.getMaxTicketsAvailable());
    }

    @Override
    public Match addMatch(Match match) throws InputValidationException {
        validateMatch(match);
        match.setCreationDateTime(LocalDateTime.now());
        match.setSoldTickets(0);

        try(Connection connection = dataSource.getConnection()){

            try{
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Match createdMatch = matchDao.create(connection, match);

                /* Commit. */
                connection.commit();

                return createdMatch;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Match> findMatchesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try(Connection connection = dataSource.getConnection()){
            return matchDao.findMatchesByDateRange(connection,
                    startDate.withNano(0), endDate.withNano(0));
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Match findMatch(Long matchId) throws InstanceNotFoundException {
        try (Connection connection = dataSource.getConnection()) {
            return matchDao.find(connection, matchId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Sale buyMatch(Long matchId, String userId, String creditCardNumber, int numTicketsSale)
            throws InstanceNotFoundException, InputValidationException, MatchFinishedException, MatchNoMoreTicketsException {
        PropertyValidator.validateCreditCard(creditCardNumber);
        CustomPropertyValidator.validateEmail(userId);

        if(numTicketsSale <= 0){
            throw new InputValidationException("Invalid number of tickets sale value (it must be grater than 0 ");
        }


        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Match match = matchDao.find(connection, matchId);

                LocalDateTime currentDateTime = LocalDateTime.now();
                if (match.getDateTime().isBefore(currentDateTime)) {
                    throw new MatchFinishedException(matchId);
                }

                int availableTickets = match.getMaxTicketsAvailable() - match.getSoldTickets();

                if (numTicketsSale > availableTickets) {
                    throw new MatchNoMoreTicketsException(matchId, numTicketsSale, availableTickets);
                }

                Sale sale = saleDao.create(connection, new Sale(matchId, userId, creditCardNumber, numTicketsSale, LocalDateTime.now()));

                match.setSoldTickets(match.getSoldTickets() + numTicketsSale);
                matchDao.update(connection, match);

                /* Commit. */
                connection.commit();

                return sale;

            } catch (MatchFinishedException | MatchNoMoreTicketsException | InstanceNotFoundException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch ( RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Sale> findSalesByUser(String userId) {
        try (Connection connection = dataSource.getConnection()) {
            return saleDao.findSalesByUser(connection, userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markTicketsCollected(Long saleId, String creditCardNumber) throws InstanceNotFoundException,
            MismatchedCardNumberException, TicketsAlreadyCollectedException, InputValidationException {

        PropertyValidator.validateCreditCard(creditCardNumber);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Sale sale = saleDao.find(connection, saleId);

                if (!creditCardNumber.equals(sale.getCreditCardNumber())) {
                    throw new MismatchedCardNumberException(saleId);
                }

                if (sale.isTicketCollected()) {
                    throw new TicketsAlreadyCollectedException(saleId);
                }

                sale.setTicketCollected(true);
                saleDao.update(connection, sale);

                connection.commit();


            } catch (MismatchedCardNumberException | TicketsAlreadyCollectedException | InstanceNotFoundException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
