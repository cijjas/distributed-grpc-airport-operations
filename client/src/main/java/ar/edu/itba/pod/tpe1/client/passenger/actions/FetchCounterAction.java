package ar.edu.itba.pod.tpe1.client.passenger.actions;

import ar.edu.itba.pod.grpc.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.FetchCounterResponse;
import ar.edu.itba.pod.grpc.FlightStatus;
import ar.edu.itba.pod.grpc.PassengerServiceGrpc;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.events.EventsClient;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClient;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClientArguments;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class FetchCounterAction implements Action {
    ManagedChannel channel;
    PassengerClientArguments arguments;



    public FetchCounterAction(ManagedChannel channel, PassengerClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;

    }

    @Override
    public void execute() {
        try {
            fetchCounter(channel, arguments.getBooking());
        } catch (Exception e) {
            System.out.println("Failed to fetch counter");
            System.out.println("Should have parameters: -Dbooking");
        }
    }

    private void fetchCounter(ManagedChannel channel, String booking) {
        PassengerServiceGrpc.PassengerServiceBlockingStub stub = PassengerServiceGrpc.newBlockingStub(channel);
        Optional<FetchCounterResponse> response = Optional.ofNullable(
            stub.fetchCounter(
                StringValue
                    .newBuilder()
                    .setValue(booking)
                    .build()
            )
        );
        response.ifPresentOrElse(
            this::handleResponse,
            () -> System.out.println("Failed to fetch counter")
        );

    }

    private void handleResponse(FetchCounterResponse response) {
        if (response.getStatus().getCode() == Status.OK.getCode().value()) {
            printFlightCheckInDetails(response);
        } else {
            System.out.println(response.getStatus().getMessage());
        }
    }

    private void printFlightCheckInDetails(FetchCounterResponse response) {
        FlightStatus flightStatus = response.getFlightStatus();
        switch (flightStatus) {
            case CHECKING_IN:
                System.out.printf("Flight %s from %s is now checking in at counters (%d-%d) in Sector %s with %d people in line%n",
                        response.getFlightCode(),
                        response.getAirlineName(),
                        response.getCounterFrom(),
                        response.getCounterTo(),
                        response.getSectorName(),
                        response.getPeopleInLine());
                break;
            case PENDING:
                System.out.printf("Flight %s from %s has no counters assigned yet%n",
                        response.getFlightCode(),
                        response.getAirlineName());
                break;
            case EXPIRED:
                System.out.printf("Flight %s from %s has already expired%n",
                        response.getFlightCode(),
                        response.getAirlineName());
                break;
            default:
                System.out.println("Unknown flight status.");
                break;
        }
    }

}
