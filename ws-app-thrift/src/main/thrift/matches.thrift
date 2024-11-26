
namespace java es.udc.ws.app.thrift

struct ThriftMatchDto {
    1: i64 matchId
    2: string visitorTeam
    3: string dateTime
    4: double ticketPrice
    5: i32 soldTickets
    6: i32 maxTicketsAvailable
}

struct ThriftSaleDto {
    1: i64 saleId
    2: i64 matchId
    3: string userId
    4: string lastFourDigits
    5: i64 numTicketsSale
    6: string saleDate
    7: bool isTicketCollected
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftClientMismatchedCardNumberException {
    1: i64 saleId
}

exception ThriftClientTicketsAlreadyCollectedException {
    1: i64 saleId
}

exception ThriftClientMatchFinishedException {
    1: i64 matchId
}

exception ThriftClientMatchNoMoreTicketsException {
    1: i64 matchId
    2: i32 numTicketsSale
    3: i32 availableTickets
}

service ThriftMatchService {

   ThriftMatchDto addMatch(1: ThriftMatchDto matchDto) throws (1: ThriftInputValidationException e)

   ThriftMatchDto findMatch(1: i64 matchId) throws (1: ThriftInstanceNotFoundException e)

   ThriftSaleDto buyMatch(1: i64 matchId, 2: string userId,3: i32 numTicketsSale 4: string creditCardNumber) throws (
                                                                                  1: ThriftInputValidationException e,
                                                                                  2: ThriftInstanceNotFoundException ee,
                                                                                  3: ThriftClientMatchNoMoreTicketsException eee,
                                                                                  4: ThriftClientMatchFinishedException eeee)

   list<ThriftMatchDto> findMatches(1: string date)

   list<ThriftSaleDto> findSalesByUser(1: string userId)

   void markTicketsCollected(1: i64 saleId, 2: string lastFourDigits) throws ( 1: ThriftInstanceNotFoundException e,
                                                                               2: ThriftClientMismatchedCardNumberException ee,
                                                                               3: ThriftClientTicketsAlreadyCollectedException eee,
                                                                               4: ThriftInputValidationException eeee)

}