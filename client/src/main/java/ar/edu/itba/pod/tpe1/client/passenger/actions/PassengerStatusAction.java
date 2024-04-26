package ar.edu.itba.pod.tpe1.client.passenger.actions;

import ar.edu.itba.pod.grpc.CheckInStatus;
import ar.edu.itba.pod.grpc.PassengerServiceGrpc;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClient;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClientArguments;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PassengerStatusAction implements Action {

    ManagedChannel channel;
    PassengerClientArguments arguments;


    public PassengerStatusAction( ManagedChannel channel, PassengerClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        if (arguments.getBooking().isPresent()) {
            try {
                passengerStatus(channel, arguments.getBooking().get());
            } catch (Exception e) {
                handlePassengerStatusError(e);
            }
        } else {
            printPassengerStatusUsageInstructions();
        }
    }
    private void handlePassengerStatusError(Exception e) {
        System.out.println("Failed to get status due to an error: " + e.getMessage());
        printPassengerStatusUsageInstructions();
    }

    private void printPassengerStatusUsageInstructions() {
        System.out.println("Invalid or missing booking parameter.");
        System.out.println("Please ensure you include the parameter: -Dbooking=<bookingCode>");
    }

    private void passengerStatus(ManagedChannel channel, String booking) {
        PassengerServiceGrpc.PassengerServiceBlockingStub stub = PassengerServiceGrpc.newBlockingStub(channel);
        Optional<ar.edu.itba.pod.grpc.PassengerStatusResponse> response = Optional.ofNullable(
            stub.passengerStatus(
                    StringValue
                            .newBuilder()
                            .setValue(booking)
                            .build()
            )
        );

        response.ifPresentOrElse(
            this::handleResponse,
            () -> System.out.println("Failed to get status")
        );
    }

    private void handleResponse(ar.edu.itba.pod.grpc.PassengerStatusResponse response) {
        if (response.getStatusResponse().getCode() == Status.OK.getCode().value()) {
            printCheckInDetails(response);
        } else {
            System.out.println(response.getStatusResponse().getMessage());
        }
    }

    private void printCheckInDetails(ar.edu.itba.pod.grpc.PassengerStatusResponse response) {
        CheckInStatus status = response.getStatus();
        switch (status) {
            case CHECKED_IN:
                System.out.printf("Booking %s for flight %s from %s checked in at counter %d in Sector %s%n",
                        response.getBookingCode(),
                        response.getFlightCode(),
                        response.getAirlineName(),
                        response.getCounterChecked(),
                        response.getSectorName());
                break;
            case NOT_CHECKED_IN:
                System.out.printf("Booking %s for flight %s from %s is now waiting to check-in on counters (%d-%d) in Sector %s with %d people in line%n",
                        response.getBookingCode(),
                        response.getFlightCode(),
                        response.getAirlineName(),
                        response.getCounterFrom(),
                        response.getCounterTo(),
                        response.getSectorName(),
                        response.getPeopleInLine());
                break;
            case AWAITING:
                System.out.printf("Booking %s for flight %s from %s can check-in on counters (%d-%d) in Sector %s%n",
                        response.getBookingCode(),
                        response.getFlightCode(),
                        response.getAirlineName(),
                        response.getCounterFrom(),
                        response.getCounterTo(),
                        response.getSectorName());
                break;
            default:
                System.out.println("Unknown check-in status.");
                break;
        }
    }

}
