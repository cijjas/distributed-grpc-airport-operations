package ar.edu.itba.pod.tpe1.client.counter.actions;

import ar.edu.itba.pod.grpc.CheckinCountersRequest;
import ar.edu.itba.pod.grpc.CheckinCountersResponse;
import ar.edu.itba.pod.grpc.CounterServiceGrpc;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientArguments;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CheckinCountersAction implements Action {
    ManagedChannel channel;
    CounterClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(CounterClient.class);

    public CheckinCountersAction(ManagedChannel channel, CounterClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            checkinCounters(channel, arguments.getSector(), arguments.getCounterFrom(), arguments.getAirline());
        } catch (Exception e) {
            logger.error("Failed to checkin counters", e);
        }
    }

    private void checkinCounters(ManagedChannel channel, String sector, Integer counterFrom, String airline) {
        CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);
        Optional<CheckinCountersResponse> response = Optional.ofNullable(stub.checkinCounters(
            CheckinCountersRequest.newBuilder()
                    .setSectorName(sector)
                    .setCounterFrom(counterFrom)
                    .setAirlineName(airline)
                    .build()));

        response.ifPresentOrElse(
            this::handleResponse,
            () -> System.out.println("Failed to checkin counters")
        );
    }

    private void handleResponse(CheckinCountersResponse checkinCountersResponse) {

    }
}
