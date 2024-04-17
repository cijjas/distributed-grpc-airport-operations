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

import java.util.Optional;

public class ListCountersAction implements Action {
    ManagedChannel channel;
    CounterClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(CounterClient.class);
    private static final String HEADER = "Counters\t Airline\t Flights\t People\n";

    public ListCountersAction(ManagedChannel channel,CounterClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            listCounters(channel, arguments.getSector(), arguments.getCounterFrom(), arguments.getCounterTo());
        } catch (Exception e) {
            logger.error("Failed to list counters", e);
        }
    }

    private void listCounters(ManagedChannel channel, String sector, Integer counterFrom, Integer counterTo) {
        CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);
        Optional<ListCountersResponse> response = Optional.ofNullable(stub.listCounters(
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
            logger.error("Error listing counters: {}", response.getStatus().getMessage());
        }
    }

    private void printCounters(ListCountersResponse response) {
        System.out.println(HEADER);
        response.getCountersList().forEach(this::printCounter);
    }

    private void printCounter(Counter counter) {
        System.out.printf("(%d-%d)\t %s\t %s\t %d",
                counter.getCounterRange().getCounterFrom(),
                counter.getCounterRange().getCounterTo(),
                counter.getAirlineName(),
                counter.getFlightCodesList()
                        .stream()
                        .collect(StringBuilder::new,
                                (sb, s) -> sb.append(s).append("|"), StringBuilder::append)
                        .toString()
                        .trim(),
                counter.getPeopleInLine()
        );
    }
}
