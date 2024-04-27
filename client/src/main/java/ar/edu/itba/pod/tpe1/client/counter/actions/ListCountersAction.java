package ar.edu.itba.pod.tpe1.client.counter.actions;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientArguments;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientArguments;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListCountersAction implements Action {
    ManagedChannel channel;
    CounterClientArguments arguments;
    private static final String HEADER = String.format("%-15s %-25s %-25s %-10s\n", "Counters", "Airline", "Flights", "People");
    private static final String HASHTAG_DIVIDER= "#".repeat(75);

    public ListCountersAction(ManagedChannel channel,CounterClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        if (arguments.getSector().isPresent() && arguments.getCounterFrom().isPresent() && arguments.getCounterTo().isPresent()) {
            try {
                listCounters(channel, arguments.getSector().get(), arguments.getCounterFrom().get(), arguments.getCounterTo().get());
            } catch (Exception e) {
                handleListCountersError(e);
            }
        } else {
            printListCountersUsageInstructions();
        }
    }

    private void handleListCountersError(Exception e) {
        System.out.println("Failed to list counters due to an error: " + e.getMessage());
        printListCountersUsageInstructions();
    }

    private void printListCountersUsageInstructions() {
        System.out.println("- ERROR - Invalid or missing parameters for listing counters.");
        System.out.println("- listCounters - Required parameters: -Dsector=<sectorName> -DcounterFrom=<startingCounterNumber> -DcounterTo=<endingCounterNumber>");
    }

    private void listCounters(ManagedChannel channel, String sector, Integer counterFrom, Integer counterTo) {
        CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);
        Optional<ListCountersResponse> response = Optional.ofNullable(
                stub.listCounters(
                ListCountersRequest.newBuilder()
                        .setSectorName(sector)
                        .setCounterFrom(counterFrom)
                        .setCounterTo(counterTo)
                        .build())
        );
        response.ifPresentOrElse(
                this::handleResponse,
                () -> System.out.println("Failed to list counters")
        );

    }

    private void handleResponse(ListCountersResponse response) {
        if (response.getStatus().getCode() == Status.OK.getCode().value()) {
            printCounters(response);
        } else {
            System.out.println(response.getStatus().getMessage());
        }
    }
    private void printCounters(ListCountersResponse response) {
        System.out.printf(HEADER);
        System.out.println(HASHTAG_DIVIDER);
        if(response.getCountersList().isEmpty()){
            return;
        }
        response.getCountersList().forEach(this::printCounter);
    }


    private void printCounter(Counter counter) {
        String people = counter.getPeopleInLine() == 0 ? "-" : String.valueOf(counter.getPeopleInLine());

        String flightCodes = counter.getFlightCodesList().isEmpty() ? "-" :
                String.join("|", counter.getFlightCodesList());

        System.out.printf("%-15s %-25s %-25s %-10s\n",
                String.format("(%d-%d)", counter.getCounterRange().getCounterFrom(), counter.getCounterRange().getCounterTo()),
                counter.getAirlineName(),
                flightCodes,
                people
        );
    }

}
