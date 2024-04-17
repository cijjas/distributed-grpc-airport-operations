package ar.edu.itba.pod.tpe1.client.events;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientAction;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientArguments;
import ar.edu.itba.pod.tpe1.client.counter.actions.*;
import ar.edu.itba.pod.tpe1.client.events.actions.RegisterAction;
import ar.edu.itba.pod.tpe1.client.events.actions.UnregisterAction;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class EventsClient {
    private static final Logger logger = LoggerFactory.getLogger(EventsClient.class);


    public static void main(String[] args) throws InterruptedException {
        logger.info("tpe1-g7 EventsClient Starting ...");
        EventsClientParser parser = new EventsClientParser();
        Optional<EventsClientArguments> arguments = parser.getEventsClientArguments(args);

        if(arguments.isEmpty()){
            logger.error("No arguments provided");
            return;
        }

        ManagedChannel channel = ChannelBuilder.buildChannel(arguments.get().getServerAddress());
        EventsClientAction action = arguments.get().getAction();
        if(action == null){
            logger.error("No valid action selected.");
            return;
        }

        Action executableAction = getAction(action, channel, arguments.get());

        executableAction.execute();
    }


    private static Action getAction(EventsClientAction action, ManagedChannel channel, EventsClientArguments arguments) {
        return switch (action) {
            case REGISTER -> new RegisterAction(channel, arguments);
            case UNREGISTER -> new UnregisterAction(channel, arguments);
        };
    }
}
