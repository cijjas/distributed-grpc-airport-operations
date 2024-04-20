package ar.edu.itba.pod.tpe1.client.passenger.actions;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClient;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClientArguments;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassengerStatusAction implements Action {

    ManagedChannel channel;
    PassengerClientArguments arguments;


    public PassengerStatusAction( ManagedChannel channel, PassengerClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            passengerStatus(channel);
        } catch (Exception e) {
            System.out.println("Failed to get status");
        }
    }

    private void passengerStatus(ManagedChannel channel) {
        // TODO: Implement
    }

}
