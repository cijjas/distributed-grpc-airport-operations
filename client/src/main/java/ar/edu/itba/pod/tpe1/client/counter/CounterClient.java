package ar.edu.itba.pod.tpe1.client.counter;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import ar.edu.itba.pod.tpe1.client.admin.AdminClient;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientAction;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientArguments;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientParser;
import ar.edu.itba.pod.tpe1.client.admin.actions.AddCountersAction;
import ar.edu.itba.pod.tpe1.client.admin.actions.AddSectorAction;
import ar.edu.itba.pod.tpe1.client.admin.actions.ManifestAction;
import ar.edu.itba.pod.tpe1.client.counter.actions.*;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientAction;
import java.nio.file.Path;
import java.util.Optional;

public class CounterClient {

    private static final Logger logger = LoggerFactory.getLogger(CounterClient.class);


    public static void main(String[] args) throws InterruptedException {
        logger.info("tpe1-g7 CounterClient Starting ...");
        CounterClientParser parser = new CounterClientParser();
        Optional<CounterClientArguments> arguments = parser.getCounterClientArguments(args);

        if(arguments.isEmpty()){
            logger.error("No arguments provided");
            return;
        }

        ManagedChannel channel = ChannelBuilder.buildChannel(arguments.get().getServerAddress());
        CounterClientAction action = arguments.get().getAction();
        if(action == null){
            logger.error("No valid action selected.");
            return;
        }

        Action executableAction = getAction(action, channel, arguments.get());

        executableAction.execute();


    }


    private static Action getAction(CounterClientAction action, ManagedChannel channel, CounterClientArguments arguments) {
        return switch (action) {
            case LISTSECTORS -> new ListSectorsAction(channel, arguments);
            case LISTCOUNTERS -> new ListCountersAction(channel, arguments);
            case ASSIGNCOUNTERS -> new AssignCountersAction(channel, arguments);
            case FREECOUNTERS -> new FreeCountersAction(channel, arguments);
            case CHECKINCOUNTERS -> new CheckinCountersAction(channel, arguments);
            case LISTPENDINGASSIGNMENTS -> new ListPendingAssignmentsAction(channel, arguments);
        };
    }
}
