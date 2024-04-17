package ar.edu.itba.pod.tpe1.client.counter.actions;

import ar.edu.itba.pod.grpc.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.FreeCountersRequest;
import ar.edu.itba.pod.grpc.FreeCountersResponse;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientArguments;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientArguments;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.util.Optional;

public class FreeCountersAction implements Action {
    ManagedChannel channel;
    CounterClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(CounterClient.class);

    public FreeCountersAction(ManagedChannel channel, CounterClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            freeCounters(channel, arguments.getCounterFrom(), arguments.getAirline());
        } catch (Exception e) {
            logger.error("Failed to free counters", e);
        }
    }

    private void freeCounters(ManagedChannel channel, Integer counterFrom, String airline) {
        CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);
        Optional<FreeCountersResponse> response = Optional.ofNullable(stub.freeCounters(
            FreeCountersRequest.newBuilder()
                    .setCounterFrom(counterFrom)
                    .setAirlineName(airline)
                    .build()));

        response.ifPresentOrElse(
            this::handleResponse,
            () -> System.out.println("Failed to free counters")
        );
    }

    private void handleResponse(FreeCountersResponse freeCountersResponse) {
        if (freeCountersResponse.getStatus().getCode() == Status.OK.getCode().value()) {
            System.out.printf("Ended check-in for flights %s on %d counters (%d-%d) in Sector %s\n",
                    String.join("|", freeCountersResponse.getFlightCodesList()),
                    freeCountersResponse.getCounterCount(),
                    freeCountersResponse.getCounterFrom(),
                    freeCountersResponse.getCounterTo(),
                    freeCountersResponse.getSectorName());
        } else {
            logger.error("Error freeing counters: {}", freeCountersResponse.getStatus().getMessage());
        }
    }

}
