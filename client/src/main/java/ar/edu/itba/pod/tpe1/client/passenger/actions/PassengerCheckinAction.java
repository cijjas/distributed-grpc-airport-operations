package ar.edu.itba.pod.tpe1.client.passenger.actions;

import ar.edu.itba.pod.grpc.PassengerCheckinRequest;
import ar.edu.itba.pod.grpc.PassengerCheckinResponse;
import ar.edu.itba.pod.grpc.PassengerServiceGrpc;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClient;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClientArguments;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PassengerCheckinAction implements Action {

    ManagedChannel channel;
    PassengerClientArguments arguments;


    public PassengerCheckinAction(ManagedChannel channel, PassengerClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;

    }

    @Override
    public void execute() {
        try {
            passengerCheckin(channel, arguments.getBooking(), arguments.getSector(), arguments.getCounterNumber());
        } catch (Exception e) {
            System.out.println("Failed to checkin");
        }
    }

    private void passengerCheckin(ManagedChannel channel, String booking, String sector, int counterNumber) {
        PassengerServiceGrpc.PassengerServiceBlockingStub stub = PassengerServiceGrpc.newBlockingStub(channel);
        Optional<PassengerCheckinResponse> response = Optional.ofNullable(
            stub.passengerCheckin(
                PassengerCheckinRequest
                    .newBuilder()
                        .setBookingCode(booking)
                        .setSectorName(sector)
                        .setCounterFrom(counterNumber)
                    .build()
            )
        );

        response.ifPresentOrElse(
            this::handleResponse,
            () -> System.out.println("Failed to checkin")
        );
    }

    private void handleResponse(PassengerCheckinResponse response) {
        if(response.getStatus().getCode() == Status.OK.getCode().value()){
            System.out.printf("Booking %s for flight %s from %s is now waiting to check-in on counters (%d-%d) in Sector %s with %d people in line%n",
                    response.getBookingCode(),
                    response.getFlightCode(),
                    response.getAirlineName(),
                    response.getCounterFrom(),
                    response.getCounterTo(),
                    response.getSectorName(),
                    response.getPeopleInLine()
            );
        } else {
            System.out.println(response.getStatus().getMessage());
        }
    }
}
