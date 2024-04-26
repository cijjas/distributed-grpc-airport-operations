package ar.edu.itba.pod.tpe1.client.query.actions;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.query.QueryClient;
import ar.edu.itba.pod.tpe1.client.query.QueryClientArguments;
import com.google.protobuf.Option;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class CountersAction implements Action {
    ManagedChannel channel;
    QueryClientArguments arguments;
    private static final String HEADER = String.format("%-8s%-10s%-17s%-20s%-8s", "Sector", "Counters", "Airline", "Flights", "People");
    private static final String HASHTAG_DIVIDER= "#".repeat(63);


    public CountersAction(ManagedChannel channel, QueryClientArguments arguments) {
        this.arguments = arguments;
        this.channel = channel;
    }

    @Override
    public void execute() {
        if(arguments.getOutPath().isPresent()) {
            try {
                String sectorArgument = arguments.getSector().isPresent() ? arguments.getSector().get() : "";

                counters(channel, sectorArgument, arguments.getOutPath().get());
            } catch (Exception e) {
                System.out.println("An error occurred getting the counters data");
            }
        }
        else{
            printCountersUsageInstructions();
        }


    }

    private void handleCountersError(Exception e) {
        System.out.println("An error occurred fetching the checkins: " + e.getMessage());
        printCountersUsageInstructions();
    }

    private void printCountersUsageInstructions() {
        System.out.println("Invalid or missing output path parameter.");
        System.out.println("Required parameter: -DoutPath=<outputPath>");
        System.out.println("Optional parameters: -Dsector=<sectorName> (default: all sectors), -Dairline=<airlineName> (default: all airlines)");
    }

    private void counters(ManagedChannel channel, String sectorName, Path outPath) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        Optional<CountersResponse> response = Optional.ofNullable(
                stub.counters(
                        CountersRequest.newBuilder()
                                .setSectorName(sectorName)
                                .build())
        );
        response.ifPresentOrElse(
                presentResponse -> handleResponse(presentResponse, outPath),
                () -> System.out.println("Failed to list counters")
        );
    }

    private void handleResponse(CountersResponse response, Path outPath) {
        if (response.getStatus().getCode() == Status.OK.getCode().value()) {
            printCountersToFile(response, outPath);
        } else {
            System.out.println(response.getStatus().getMessage());
        }
    }

    private void printCountersToFile(CountersResponse response, Path filePath) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writer.println(HEADER);
            writer.println(HASHTAG_DIVIDER);
            if(response.getCountersList().isEmpty()){
                return;
            }
            response.getCountersList().forEach(counterGroup -> printCounterToFile(counterGroup, writer));
        } catch (IOException e) {
            System.out.println("An error occurred creating the file");
        }
    }

    private void printCounterToFile(CountersResponse.CounterGroup counterGroup, PrintWriter writer) {
        String people = counterGroup.getPeopleInLine() == -1 ? "-" : String.valueOf(counterGroup.getPeopleInLine());

        String flightCodes = counterGroup.getFlightCodesList().isEmpty() ? "-" :
                String.join("|", counterGroup.getFlightCodesList());

        String airlineName = counterGroup.getAirlineName().isEmpty() ? "-" : counterGroup.getAirlineName();

        writer.printf("%-8s%-10s%-17s%-20s%-8s\n",
                counterGroup.getSectorName(),
                String.format("(%d-%d)", counterGroup.getCounterFrom(), counterGroup.getCounterTo()),
                airlineName,
                flightCodes,
                people
        );
    }

}
