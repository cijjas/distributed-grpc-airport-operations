package ar.edu.itba.pod.tpe1.client.events.actions;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.events.EventsClient;
import ar.edu.itba.pod.tpe1.client.events.EventsClientArguments;
import io.grpc.ManagedChannel;
import jdk.jfr.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RegisterAction implements Action {
    ManagedChannel channel;
    EventsClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(EventsClient.class);

    public RegisterAction(ManagedChannel channel, EventsClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            register(channel);
        } catch (Exception e) {
            logger.error("Failed to register", e);
        }
    }

    private void register(ManagedChannel channel) {
        //TODO: Implement
    }
}
