package ar.edu.itba.pod.tpe1.client.events.actions;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.events.EventsClient;
import ar.edu.itba.pod.tpe1.client.events.EventsClientArguments;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnregisterAction implements Action {
    ManagedChannel channel;
    EventsClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(EventsClient.class);


    public UnregisterAction(ManagedChannel channel, EventsClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    // TODO: Implement

    @Override
    public void execute() {
        try {
            unregister(channel);
        } catch (Exception e) {
            logger.error("Failed to unregister", e);
        }
    }

    private void unregister(ManagedChannel channel) {
        //TODO: Implement
    }
}
