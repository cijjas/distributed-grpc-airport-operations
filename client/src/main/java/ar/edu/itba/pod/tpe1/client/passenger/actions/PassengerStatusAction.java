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
    private static final Logger logger = LoggerFactory.getLogger(PassengerClient.class);


    public PassengerStatusAction( ManagedChannel channel, PassengerClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            passengerStatus(channel);
        } catch (Exception e) {
            logger.error("Failed to get status", e);
        }
    }

    private void passengerStatus(ManagedChannel channel) {
        // TODO: Implement
    }

}
