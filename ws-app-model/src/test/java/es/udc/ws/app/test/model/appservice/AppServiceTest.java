package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.match.SqlMatchDao;
import es.udc.ws.app.model.match.SqlMatchDaoFactory;
import es.udc.ws.app.model.matchservice.MatchService;
import es.udc.ws.app.model.matchservice.MatchServiceFactory;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.app.model.matchservice.exceptions.MismatchedCardNumberException;
import es.udc.ws.app.model.matchservice.exceptions.TicketsAlreadyCollectedException;
import es.udc.ws.app.model.sale.Sale;
import es.udc.ws.app.model.sale.SqlSaleDao;
import es.udc.ws.app.model.sale.SqlSaleDaoFactory;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {

    private static MatchService matchService = null;
    private static SqlSaleDao saleDao = null;
    private static SqlMatchDao matchDao = null;

    private final String VALID_CREDIT_CARD_NUMBER = "1234567890123456";
    private final String INVALID_CREDIT_CARD_NUMBER = "";

    private final long NON_EXISTENT_MATCH_ID = -1;
    private final long NON_EXISTENT_SALE_ID = -1;
    private final String USER_ID = "ws-user@udc.es";
    private final String INVALID_USER_ID = "ws-user";
    private final int NUM_TICKETS_SALE = 5;


    @BeforeAll
    public static void init() {

        DataSource dataSource = new SimpleDataSource();

        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        matchService = MatchServiceFactory.getService();

        matchDao = SqlMatchDaoFactory.getDao();

        saleDao = SqlSaleDaoFactory.getDao();
    }

    LocalDateTime oneWeekDate = LocalDateTime.now().plusDays(7).withNano(0);

    private Match getValidMatch(String visitorTeam, LocalDateTime dateTime){
        return new Match(visitorTeam, dateTime, 85, 200);
    }

    private Match getValidMatch(String visitorTeam) {
        return getValidMatch(visitorTeam, oneWeekDate);
    }

    private Match getValidMatch() {
        return getValidMatch("Visitor Team");
    }


    private Match createMatch(Match match) {

        Match addedMatch;
        try {
            addedMatch = matchService.addMatch(match);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedMatch;
    }

    private void removeMatch(Long matchId) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()){

            try{
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                matchDao.remove(connection, matchId);

                /* Commit. */
                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeSale(Long saleId) {

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                saleDao.remove(connection, saleId);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
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

    public Match addMatchNoValidations(Match match) {

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

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
    private Sale findSale(Long saleId) throws InstanceNotFoundException{

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            return saleDao.find(connection, saleId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testAddMatchAndFindMatch() throws InputValidationException, InstanceNotFoundException {
        Match match = getValidMatch();
        Match addedMatch = null;

        try {
            //Create Match
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            addedMatch = matchService.addMatch(match);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            // Find Match
            Match foundMatch = matchService.findMatch(addedMatch.getMatchId());
            assertEquals(addedMatch, foundMatch);
            assertEquals(addedMatch.getTicketPrice(), foundMatch.getTicketPrice());
            assertEquals(addedMatch.getSoldTickets(), foundMatch.getSoldTickets());
            assertEquals(addedMatch.getMaxTicketsAvailable(), foundMatch.getMaxTicketsAvailable());
            assertEquals(addedMatch.getVisitorTeam(), foundMatch.getVisitorTeam());
            assertEquals(addedMatch.getDateTime(), foundMatch.getDateTime());
            assertTrue((foundMatch.getCreationDateTime().compareTo(beforeCreationDate) >= 0)
                    && (foundMatch.getCreationDateTime().compareTo(afterCreationDate) <= 0));

        } finally {
            // Clear Database
            if (addedMatch != null) {
                removeMatch(addedMatch.getMatchId());
            }
        }
    }

    @Test
    public void testAddInvalidMatch(){

        /// Visitor Team

        //Check match visitorTeam not null
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setVisitorTeam(null);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        // Check match visitorTeam not empty
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setVisitorTeam("");
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        /// Date Time

        // Check match dateTime not null
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setDateTime(null);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        // Check match dateTime not before actual date
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setDateTime(LocalDateTime.now().minusHours(1));
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        /// Ticket Price

        // Check match ticketPrice >= 0
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setTicketPrice(-1);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        /// Max Tickets Available

        // Check match maxTicketsAvailable >= 0
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setMaxTicketsAvailable(-1);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        /// Sold Tickets

        // Check match soldTickets >= 0
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setSoldTickets(-1);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        // Check match soldTickets <= MaxTicketsAvailable
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setSoldTickets(match.getMaxTicketsAvailable() + 1);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

    }

    @Test
    public void testFindNonExistentMatch() {
        assertThrows(InstanceNotFoundException.class, () -> matchService.findMatch(NON_EXISTENT_MATCH_ID));
    }


    @Test
    public void testFindMatchesByDateRange(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Add matches
        List<Match> matches = new LinkedList<>();

        Match match1 = createMatch(getValidMatch("match title 1",
                LocalDateTime.parse("20-10-2024 19:00:00", formatter)));
        matches.add(match1);

        Match match2 = createMatch(getValidMatch("match title 2",
                LocalDateTime.parse("15-11-2024 15:00:00", formatter)));
        matches.add(match2);

        Match match3 = createMatch(getValidMatch("match title 3",
                LocalDateTime.parse("17-11-2024 15:10:00", formatter)));
        matches.add(match3);

        Match match4 = createMatch(getValidMatch("match title 4",
                LocalDateTime.parse("20-12-2024 15:10:00", formatter)));
        matches.add(match4);

        try {
            // All matches
            LocalDateTime startDate = (LocalDateTime.parse("01-01-2024 00:00:00", formatter));
            LocalDateTime endDate = (LocalDateTime.parse("20-12-2024 15:10:00", formatter));
            List<Match> foundMatches = matchService.findMatchesByDateRange(startDate, endDate);

            assertEquals(matches, foundMatches);

            // Matches 2 and 3
            startDate = LocalDateTime.parse("20-10-2024 19:00:01", formatter);
            endDate = LocalDateTime.parse("17-11-2024 15:10:00", formatter);
            foundMatches = matchService.findMatchesByDateRange(startDate, endDate);

            assertEquals(Arrays.asList(match2, match3), foundMatches);

            // One match
            startDate = endDate = LocalDateTime.parse("20-12-2024 15:10:00", formatter);

            foundMatches = matchService.findMatchesByDateRange(startDate, endDate);

            assertEquals(Collections.singletonList(match4), foundMatches);
        } finally {
            //Clear Database
            for(Match match : matches){
                removeMatch(match.getMatchId());
            }
        }
    }

    @Test
    public void testBuyMatchAndFindSale() throws InstanceNotFoundException, InputValidationException,
            MatchFinishedException, MatchNoMoreTicketsException {

        Match match = createMatch(getValidMatch());
        Sale sale = null;

        try {
            LocalDateTime beforeBuyDate = LocalDateTime.now().withNano(0);

            sale = matchService.buyMatch(match.getMatchId(), USER_ID, VALID_CREDIT_CARD_NUMBER, 3);

            LocalDateTime afterBuyDate = LocalDateTime.now().withNano(0);

            //Find sale
            Sale foundSale = findSale(sale.getSaleId());

            // Check sale
            assertEquals(sale, foundSale);
            assertEquals(VALID_CREDIT_CARD_NUMBER, foundSale.getCreditCardNumber());
            assertEquals(USER_ID, foundSale.getUserId());
            assertEquals(3, foundSale.getNumTicketsSale());
            assertEquals(match.getMatchId(), foundSale.getMatchId());
            assertTrue((foundSale.getSaleDate().compareTo(beforeBuyDate) >= 0)
                    && (foundSale.getSaleDate().compareTo(afterBuyDate) <= 0));

            // Find match
            Match foundMatch = matchService.findMatch(match.getMatchId());
            System.out.println(matchService.findMatch(match.getMatchId()).getSoldTickets());

            // Check SoldTickets Sale
            assertEquals(foundSale.getNumTicketsSale(),sale.getNumTicketsSale());
            assertEquals(match.getSoldTickets() + 3, foundMatch.getSoldTickets());



        } finally {
            // Clear database: remove sale (if created) and match
            if (sale != null) {
                removeSale(sale.getSaleId());
            }
            removeMatch(match.getMatchId());
        }
    }

    @Test
    public void testBuyMatchWithInvalidCreditCard() {

        // Create match
        Match match = createMatch(getValidMatch());

        // Test invalid credit card sale
        try {
            assertThrows(InputValidationException.class, () -> {
                Sale sale = matchService.buyMatch(match.getMatchId(), USER_ID, INVALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
                removeSale(sale.getSaleId());
            });
        } finally {
            // Clear database
            removeMatch(match.getMatchId());
        }

    }

    @Test
    public void testBuyNonExistentMatch() {

        assertThrows(InstanceNotFoundException.class, () -> {
            Sale sale = matchService.buyMatch(NON_EXISTENT_MATCH_ID, USER_ID, VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
            removeSale(sale.getSaleId());
        });

    }


    @Test
    public void testBuyMatchWithInvalidMatchId() {

        // Test: buy a match with an invalid match ID
        assertThrows(InstanceNotFoundException.class, () -> {
            Sale sale = matchService.buyMatch(NON_EXISTENT_MATCH_ID, USER_ID, VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
            removeSale(sale.getSaleId());
        });
    }

    @Test
    public void testBuyMatchWithInvalidUserId() {


        // Create match
        Match match = createMatch(getValidMatch());

        // Test invalid credit card sale
        try {
            assertThrows(InputValidationException.class, () -> {
                Sale sale = matchService.buyMatch(match.getMatchId(), INVALID_USER_ID, VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
                removeSale(sale.getSaleId());
            });
        } finally {
            // Clear database
            removeMatch(match.getMatchId());
        }

    }

    @Test
    public void testBuyMatchWithInvalidNumTicketsSale() {

        // Create match
        Match match = createMatch(getValidMatch());

        // Test invalid credit card sale
        try {
            assertThrows(InputValidationException.class, () -> {
                Sale sale = matchService.buyMatch(match.getMatchId(), USER_ID, VALID_CREDIT_CARD_NUMBER, -1);
                removeSale(sale.getSaleId());
            });
        } finally {
            // Clear database
            removeMatch(match.getMatchId());
        }
    }

    @Test
    public void testMatchNoMoreTickets() {

        Match match = createMatch(getValidMatch());
        try{
            assertThrows(MatchNoMoreTicketsException.class, () -> {
                int numTickets = match.getMaxTicketsAvailable() + 1;
                Sale sale = matchService.buyMatch(match.getMatchId(), USER_ID, VALID_CREDIT_CARD_NUMBER, numTickets);
                removeSale(sale.getSaleId());
            });
        } finally {
            removeMatch(match.getMatchId());
        }
    }

    @Test
    public void testMatchFinishedException() {

        Match match = new Match("visitorTeam", LocalDateTime.now().minusDays(1), 85, 200);
        match.setCreationDateTime(LocalDateTime.now().minusDays(5));

        Match createdMatch = addMatchNoValidations(match);

        try{
            assertThrows(MatchFinishedException.class, () -> {
                matchService.buyMatch(createdMatch.getMatchId(), USER_ID, VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
            });
        } finally{
            removeMatch(createdMatch.getMatchId());
        }
    }


    @Test
    public void testFindSalesByUser() throws MatchNoMoreTicketsException, InstanceNotFoundException,
            InputValidationException, MatchFinishedException {

        // Create matches
        Match match1 = createMatch(getValidMatch("Test1"));
        Match match2 = createMatch(getValidMatch("Test2"));
        Match match3 = createMatch(getValidMatch("Test3"));
        Match match4 = createMatch(getValidMatch("Test4"));
        Match match5 = createMatch(getValidMatch("Test5"));

        // Create sales for a user
        Sale sale1 = matchService.buyMatch(match1.getMatchId(), "user1@udc.es", VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
        Sale sale2 = matchService.buyMatch(match2.getMatchId(), "user1@udc.es", VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
        Sale sale3 = matchService.buyMatch(match3.getMatchId(), "user1@udc.es", VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);

        // Create sales for b user
        Sale sale4 = matchService.buyMatch(match4.getMatchId(), "user2@udc.es", VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
        Sale sale5 = matchService.buyMatch(match5.getMatchId(), "user2@udc.es", VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);

        try{
            // Find sales by user a
            List<Sale> salesA = matchService.findSalesByUser("user1@udc.es");

            // Find sales by user b
            List<Sale> salesB = matchService.findSalesByUser("user2@udc.es");


            // Assert that the sale is found or not
            assertTrue(salesA.contains(sale1));
            assertTrue(salesA.contains(sale2));
            assertTrue(salesA.contains(sale3));
            assertFalse(salesA.contains(sale4));
            assertFalse(salesA.contains(sale5));

            assertFalse(salesB.contains(sale1));
            assertFalse(salesB.contains(sale2));
            assertFalse(salesB.contains(sale3));
            assertTrue(salesB.contains(sale4));
            assertTrue(salesB.contains(sale5));
        } finally {
            removeMatch(match1.getMatchId());
            removeMatch(match2.getMatchId());
            removeMatch(match3.getMatchId());
            removeMatch(match4.getMatchId());
            removeMatch(match5.getMatchId());

            removeSale(sale1.getSaleId());
            removeSale(sale2.getSaleId());
            removeSale(sale3.getSaleId());
            removeSale(sale4.getSaleId());
            removeSale(sale5.getSaleId());

        }

    }

    @Test
    public void testMarkTicketsCollected() throws MatchNoMoreTicketsException, InstanceNotFoundException,
            InputValidationException, MatchFinishedException, MismatchedCardNumberException, TicketsAlreadyCollectedException {

        Match match = createMatch(getValidMatch());
        Sale sale = matchService.buyMatch(match.getMatchId(), USER_ID, VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);

        try{
            matchService.markTicketsCollected(sale.getSaleId(), VALID_CREDIT_CARD_NUMBER);
            Sale foundSale = findSale(sale.getSaleId());
            assertTrue(foundSale.isTicketCollected());
        } finally {
            removeMatch(match.getMatchId());
            removeSale(sale.getSaleId());
        }

    }

    @Test
    public void testMarkTicketsCollectedNonExistentSale() {

        assertThrows(InstanceNotFoundException.class, () -> {
            matchService.markTicketsCollected(NON_EXISTENT_SALE_ID, VALID_CREDIT_CARD_NUMBER);
        });
    }

    @Test
    public void testTicketsAlreadyCollectedException() throws MatchNoMoreTicketsException, InstanceNotFoundException,
            InputValidationException, MatchFinishedException, MismatchedCardNumberException, TicketsAlreadyCollectedException {

        Match match = createMatch(getValidMatch());
        Sale sale = matchService.buyMatch(match.getMatchId(), USER_ID, VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);

        try{
            matchService.markTicketsCollected(sale.getSaleId(), VALID_CREDIT_CARD_NUMBER);

            assertThrows(TicketsAlreadyCollectedException.class, () -> {
                matchService.markTicketsCollected(sale.getSaleId(), VALID_CREDIT_CARD_NUMBER);
            });
        } finally {
            removeMatch(match.getMatchId());
            removeSale(sale.getSaleId());
        }
    }

    @Test
    public void testMismatchedCardNumberException() throws MatchNoMoreTicketsException, InstanceNotFoundException,
            InputValidationException, MatchFinishedException {

        Match match = createMatch(getValidMatch());
        Sale sale = matchService.buyMatch(match.getMatchId(), USER_ID, VALID_CREDIT_CARD_NUMBER, NUM_TICKETS_SALE);
        try{
            assertThrows(MismatchedCardNumberException.class, () -> {
                matchService.markTicketsCollected(sale.getSaleId(), "5432109876543210");
            });
        } finally {
            removeMatch(match.getMatchId());
            removeSale(sale.getSaleId());
        }
    }
}