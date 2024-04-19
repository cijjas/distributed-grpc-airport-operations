package ar.edu.itba.pod.tpe1.client.query;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.query.actions.CheckinsAction;
import ar.edu.itba.pod.tpe1.client.query.actions.CountersAction;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class QueryClient {

    public static void main(String[] args) throws InterruptedException {
        QueryClientParser parser = new QueryClientParser();
        Optional<QueryClientArguments> arguments = parser.getQueryClientArguments(args);

        if(arguments.isEmpty()){
            System.out.println("No arguments provided.");
            return;
        }
        ManagedChannel channel = ChannelBuilder.buildChannel(arguments.get().getServerAddress());
        QueryClientAction action = arguments.get().getAction();
        if(action == null){
            System.out.println("No valid action selected.");
            return;
        }

        Action executableAction = getAction(action, channel, arguments.get());
        executableAction.execute();

    }

    private static Action getAction(QueryClientAction action, ManagedChannel channel, QueryClientArguments arguments) {
        return switch (action) {
            case CHECKINS -> new CheckinsAction(channel, arguments);
            case COUNTERS -> new CountersAction(channel, arguments);
        };
    }

}
