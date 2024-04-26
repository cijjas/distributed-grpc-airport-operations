package ar.edu.itba.pod.tpe1.client.query.actions;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.query.QueryClientArguments;
import io.grpc.ManagedChannel;
import io.grpc.Status;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class CheckinsAction implements Action {
    ManagedChannel channel;
    QueryClientArguments arguments;
    private static final String HEADER = String.format("%-8s%-10s%-17s%-11s%-16s", "Sector", "Counter", "Airline", "Flight", "Booking");
    private static final String HASHTAG_DIVIDER = "#".repeat(63);


    public CheckinsAction(ManagedChannel channel, QueryClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        if(arguments.getOutPath().isPresent()){
            try {
                String sectorName = arguments.getSector().isPresent() ? arguments.getSector().get() : "";
                String airlineName = arguments.getAirline().isPresent() ? arguments.getAirline().get() : "";

                checkins(channel, sectorName, airlineName, arguments.getOutPath().get());
            } catch (Exception e) {
                handleCheckinsError(e);
            }
        }
        else{
            printCheckinsUsageInstructions();
        }
    }

    private void handleCheckinsError(Exception e) {
        System.out.println("An error occurred fetching the checkins: " + e.getMessage());
        printCheckinsUsageInstructions();
    }

    private void printCheckinsUsageInstructions() {
        System.out.println("Invalid or missing output path parameter.");
        System.out.println("Required parameter: -DoutPath=<outputPath>");
        System.out.println("Optional parameters: -Dsector=<sectorName> (default: all sectors), -Dairline=<airlineName> (default: all airlines)");
    }

    private void checkins(ManagedChannel channel, String sectorName, String airlineName, Path outPath) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        Optional<CheckinsResponse> response = Optional.ofNullable(
                stub.checkins(
                        CheckinsRequest.newBuilder()
                                .setSectorName(sectorName)
                                .setAirlineName(airlineName)
                                .build())
        );
        response.ifPresentOrElse(
                presentResponse -> handleResponse(presentResponse, outPath),
                () -> System.out.println("Failed to list checkins")
        );
    }

    private void handleResponse(CheckinsResponse response, Path outPath) {
        if (response.getStatus().getCode() == Status.OK.getCode().value()) {
            printCheckinsToFile(response, outPath);
        } else {
            System.out.println(response.getStatus().getMessage());
        }
    }

    private void printCheckinsToFile(CheckinsResponse response, Path filePath) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writer.println(HEADER);
            writer.println(HASHTAG_DIVIDER);
            if (response.getCheckinsList().isEmpty()) {
                return;
            }
            response.getCheckinsList().forEach(checkin -> printCheckinToFile(checkin, writer));
        } catch (IOException e) {
            System.out.println("An error occurred creating the file");
        }
    }

    private void printCheckinToFile(CheckinsResponse.Checkin checkin, PrintWriter writer) {
        writer.printf("%-8s%-10s%-17s%-11s%-16s\n",
                checkin.getSectorName(),
                checkin.getCounterNumber(),
                checkin.getAirlineName(),
                checkin.getFlightCode(),
                checkin.getBookingCode()
        );
    }

}
