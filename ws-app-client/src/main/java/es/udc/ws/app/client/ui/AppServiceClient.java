package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.ClientMatchServiceFactory;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientSaleDto;
import es.udc.ws.app.client.service.exceptions.ClientMatchFinishedException;
import es.udc.ws.app.client.service.exceptions.ClientMatchNoMoreTicketsException;
import es.udc.ws.app.model.matchservice.exceptions.MatchFinishedException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNoMoreTicketsException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AppServiceClient {

    public static void main(String[] args) {

        if(args.length == 0) {
            printUsageAndExit();
        }
        ClientMatchService clientMatchService =
                ClientMatchServiceFactory.getService();

        if ("-addMatch".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, new int[] {3, 4});

            // [addMatch] <visitor> <celebrationdate> <price> <maxTickets>
            try {
                String visitor = args[1].trim();
                LocalDateTime celebrationDate = LocalDateTime.parse(args[2]);
                float price = Float.parseFloat(args[3]);
                int maxTickets = Integer.parseInt(args[4]);

                Long matchId = clientMatchService.addMatch(new ClientMatchDto(null,
                        visitor, celebrationDate, price, maxTickets));

                System.out.println("MatchId=" + matchId + " creado");

            } catch (DateTimeParseException ex) {
                System.err.println("Error: La fecha está mal formateada");
                ex.printStackTrace(System.err);
            } catch (IllegalArgumentException ex) {
                System.err.println("Error: " + ex.getMessage());
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if("-buy".equalsIgnoreCase(args[0])) {
            // [buy] MatchServiceClient -buy <matchId> <userEmail> <numTickets> <cardNumber>

            validateArgs(args, 5, new int[] {1, 3});

            Long saleId;

            try {
                saleId = clientMatchService.buyMatch(Long.parseLong(args[1]),
                        args[2], Integer.parseInt(args[3]), args[4]);

                System.out.println("Match " + args[1] +
                        " purchased sucessfully with sale number " +
                        saleId);

            } catch (InstanceNotFoundException | InputValidationException | ClientMatchNoMoreTicketsException |
                     ClientMatchFinishedException | MatchFinishedException | MatchNoMoreTicketsException ex) {
                System.err.println("Error: " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        } else if("-findMatch".equalsIgnoreCase(args[0])){

            // [findById] MatchServiceClient -findMatch <matchId>

            validateArgs(args, 2, new int[] {1});

            try{
                ClientMatchDto matchDto = clientMatchService.findMatch(Long.parseLong(args[1]));
                System.out.println("Datos del partido con id " + matchDto.getMatchId() + ":");
                System.out.println(
                        "Fecha celebración: " + matchDto.getDateTime().toString() +
                        ", Entradas disponibles: " + (matchDto.getMaxTicketsAvailable() - matchDto.getSoldTickets()) +
                        ", Entradas totales: " + matchDto.getMaxTicketsAvailable() +
                        ", Equipo visitante: " + matchDto.getVisitorTeam() +
                        ", Precio de la entrada: " + matchDto.getTicketPrice()
                );

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if("-findMatches".equalsIgnoreCase(args[0])){

            // [findByDate] MatchServiceClient -findMatches <untilDay>

            validateArgs(args, 2, new int[] {});


            try{
                List<ClientMatchDto> matches = clientMatchService.findMatchesByDateRange(args[1]);

                System.out.println("Found matches until " + args[1]);

                for (ClientMatchDto matchDto : matches) {
                    System.out.println(
                            "Datos del partido con id : " + matchDto.getMatchId() + ":\n" +
                            "Fecha celebración: " + matchDto.getDateTime().toString() +
                            ", Entradas disponibles: " + (matchDto.getMaxTicketsAvailable() - matchDto.getSoldTickets()) +
                            ", Entradas totales: " + matchDto.getMaxTicketsAvailable() +
                            ", Equipo visitante: " + matchDto.getVisitorTeam() +
                            ", Precio de la entrada: " + matchDto.getTicketPrice()
                    );
                }

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if("-findPurchases".equalsIgnoreCase(args[0])) {

            // [findPurchases] MatchServiceClient -findPurchases <userEmail>

            validateArgs(args, 2, new int[] {});

            try {

                List<ClientSaleDto> sales = clientMatchService.findSalesByUser(args[1]);

                System.out.println("Found " + sales.size() +
                        " sale(s) with keywords '" + args[1] + "'");

                for (ClientSaleDto saleDto : sales) {
                    System.out.println("Datos de la compra con id " + saleDto.getSaleId() + ":\n" +
                            "matchId: " + saleDto.getMatchId() +
                            ", Entradas: " + saleDto.getNumTicketsSale() +
                            ", Tarjeta acabada en: " + saleDto.getLastFourDigits() +
                            ", Recogidas: " + (saleDto.isTicketCollected() ? "Sí" : "No") +
                            ", Usuario: " + saleDto.getUserId() +
                            ", Fecha de la compra: " + saleDto.getSaleDate());
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if("-collect".equalsIgnoreCase(args[0])) {

            // [collect] MatchServiceClient -collect <purchaseId> <cardNumber>

            validateArgs(args, 3, new int[] {1});

            try {
                clientMatchService.markTicketsCollected(Long.parseLong(args[1]), args[2]);
                System.out.println("Entradas recogidas exitosamente");
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

    }

    public static void validateArgs(String[] args, int expectedArgs,
                                    int[] numericArguments) {
        if(expectedArgs != args.length) {
            printUsageAndExit();
        }
        for(int i = 0 ; i< numericArguments.length ; i++) {
            int position = numericArguments[i];
            try {
                Double.parseDouble(args[position]);
            } catch(NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [add]              MatchServiceClient -addMatch <visitor> <celebrationdate> <price> <maxTickets>\n" +
                "    [buy]              MatchServiceClient -buy <matchId> <userEmail> <numTickets> <cardNumber>\n" +
                "    [findByDate]       MatchServiceClient -findMatches <untilDay>\n" +
                "    [findById]         MatchServiceClient -findMatch <matchId>\n" +
                "    [findPurchases]    MatchServiceClient -findPurchases <userEmail>\n" +
                "    [collect]          MatchServiceClient -collect <purchaseId> <cardNumber>\n"
        );
    }
}