package ar.edu.itba.pod.tpe1.client.counter.actions;

import ar.edu.itba.pod.grpc.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.FreeCountersRequest;
import ar.edu.itba.pod.grpc.FreeCountersResponse;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientArguments;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import java.util.Optional;

public class FreeCountersAction implements Action {
    ManagedChannel channel;
    CounterClientArguments arguments;

    public FreeCountersAction(ManagedChannel channel, CounterClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        if(arguments.getSector().isPresent() && arguments.getCounterFrom().isPresent() && arguments.getAirline().isPresent()){
            try{
                freeCounters(channel, arguments.getSector().get(), arguments.getCounterFrom().get(), arguments.getAirline().get());
            }
            catch (Exception e){
                handleFreeCountersError(e);
            }
        }
        else {
            printFreeCountersUsageInstructions();
        }
    }

    private void handleFreeCountersError(Exception e) {
        System.out.println("Failed to free counters due to an error: " + e.getMessage());
        printFreeCountersUsageInstructions();
    }

    private void printFreeCountersUsageInstructions() {
        System.out.println("- ERROR - Invalid or missing parameters for freeing counters.");
        System.out.println("- freeCounters - Required parameters: -Dsector=<sectorName> -DcounterFrom=<counterStartNumber> -Dairline=<airlineName>");
    }

    private void freeCounters(ManagedChannel channel,String sector,  Integer counterFrom, String airline) {
        CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);
        Optional<FreeCountersResponse> response = Optional.ofNullable(stub.freeCounters(
            FreeCountersRequest.newBuilder()
                    .setCounterFrom(counterFrom)
                    .setAirlineName(airline)
                    .setSectorName(sector)
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
            System.out.println(freeCountersResponse.getStatus().getMessage());
        }
    }

}
