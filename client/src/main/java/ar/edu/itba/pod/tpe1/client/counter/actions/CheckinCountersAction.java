package ar.edu.itba.pod.tpe1.client.counter.actions;

import ar.edu.itba.pod.grpc.Checkin;
import ar.edu.itba.pod.grpc.CheckinCountersRequest;
import ar.edu.itba.pod.grpc.CheckinCountersResponse;
import ar.edu.itba.pod.grpc.CounterServiceGrpc;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientArguments;
import io.grpc.ManagedChannel;
import io.grpc.Status;

import java.util.List;
import java.util.Optional;

public class CheckinCountersAction implements Action {
    ManagedChannel channel;
    CounterClientArguments arguments;

    public CheckinCountersAction(ManagedChannel channel, CounterClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        if(arguments.getSector().isPresent() && arguments.getCounterFrom().isPresent() && arguments.getAirline().isPresent()){
            try{
                checkinCounters(channel, arguments.getSector().get(), arguments.getCounterFrom().get(), arguments.getAirline().get());
            }
            catch (Exception e){
                handleCheckinCountersError(e);
            }
        }
        else {
            printCheckinCountersUsageInstructions();
        }
    }

    private void handleCheckinCountersError(Exception e) {
        System.out.println("Failed to check-in counters due to an error: " + e.getMessage());
        printCheckinCountersUsageInstructions();
    }

    private void printCheckinCountersUsageInstructions() {
        System.out.println("- ERROR - Invalid or missing parameters for check-in operation.");
        System.out.println("- checkinCounters - Required parameters: -Dsector=<sectorName> -DcounterFrom=<counterStartNumber> -Dairline=<airlineName>");
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
        if (checkinCountersResponse.getStatus().getCode() == Status.OK.getCode().value()) {
            printCheckins(checkinCountersResponse.getCheckinsList());
            if(!checkinCountersResponse.getIdleCountersList().isEmpty()){
                for(Integer counter : checkinCountersResponse.getIdleCountersList()){
                    System.out.printf("Counter %d is idle\n", counter);
                }
            }
        } else {
            System.out.println(checkinCountersResponse.getStatus().getMessage());
        }
    }

    private void printCheckins(List<Checkin> checkins) {
        checkins.forEach(this::printCheckin);
    }

    private void printCheckin(Checkin checkin) {
        System.out.printf("Check-in successful of %s for flight %s at counter %d\n",
                checkin.getBookingCode(),
                checkin.getFlightCode(),
                checkin.getCounterNumber()
        );

    }

}
