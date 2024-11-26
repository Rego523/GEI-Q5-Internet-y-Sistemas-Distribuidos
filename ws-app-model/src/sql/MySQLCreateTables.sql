-- ----------------------------------------------------------------------------
-- Model
-- -----------------------------------------------------------------------------

DROP TABLE `Match`;
DROP TABLE MatchSale;
-- ----------------------- Match ----------------------------------
CREATE TABLE `Match` ( matchId BIGINT NOT NULL AUTO_INCREMENT,
    visitorTeam VARCHAR(255) NOT NULL,
    dateTime DATETIME NOT NULL,
    ticketPrice DOUBLE NOT NULL,
    maxTicketsAvailable INT NOT NULL,
    creationDateTime DATETIME NOT NULL,
    soldTickets INT NOT NULL,
    CONSTRAINT MatchPK PRIMARY KEY (matchId),
    CONSTRAINT ValidTicketPrice CHECK (ticketPrice >= 0),
    CONSTRAINT ValidMaxTicketsAvailable CHECK (maxTicketsAvailable >= 0),
    CONSTRAINT ValidSoldTickets CHECK (soldTickets >= 0)
    //CONSTRAINT ValidDateOrder CHECK (dateTime >= creationDateTime)
);

CREATE TABLE MatchSale ( saleId BIGINT NOT NULL AUTO_INCREMENT,
      matchId BIGINT NOT NULL,
      userId VARCHAR(255) NOT NULL,
      creditCardNumber VARCHAR(16) NOT NULL,
      numTicketsSale INT NOT NULL,
      saleDate TIMESTAMP NOT NULL,
      isTicketCollected BOOLEAN NOT NULL,
    CONSTRAINT SalePK PRIMARY KEY (saleId)
);