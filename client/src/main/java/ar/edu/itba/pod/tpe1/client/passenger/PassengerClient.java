package ar.edu.itba.pod.tpe1.client.passenger;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.itba.pod.tpe1.client.passenger.actions.*;

import java.util.Optional;

public class PassengerClient {


    public static void main(String[] args) throws InterruptedException {
        PassengerClientParser parser = new PassengerClientParser();
        Optional<PassengerClientArguments> arguments = parser.getPassengerClientArguments(args);

        if(arguments.isEmpty()){
            System.out.println("No arguments provided.");
            return;
        }

        ManagedChannel channel =  ChannelBuilder.buildChannel(arguments.get().getServerAddress());
        PassengerClientAction action = arguments.get().getAction();
        if(action == null){
            System.out.println("No valid action selected.");
            return;
        }

        Action executableAction = getAction(action, channel, arguments.get());

        executableAction.execute();
    }

    private static Action getAction(PassengerClientAction action, ManagedChannel channel, PassengerClientArguments arguments) {
        return switch (action) {
            case FETCHCOUNTER -> new FetchCounterAction(channel, arguments);
            case PASSENGERCHECKIN -> new PassengerCheckinAction(channel, arguments);
            case PASSENGERSTATUS -> new PassengerStatusAction(channel, arguments);
        };
    }


}
