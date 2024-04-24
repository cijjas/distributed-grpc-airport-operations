package ar.edu.itba.pod.tpe1.client.counter;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import ar.edu.itba.pod.tpe1.client.counter.actions.*;
import io.grpc.ManagedChannel;
import java.util.Optional;

public class CounterClient {



    public static void main(String[] args) throws InterruptedException {
        CounterClientParser parser = new CounterClientParser();
        Optional<CounterClientArguments> arguments = parser.getCounterClientArguments(args);

        if(arguments.isEmpty()){
            return;
        }

        ManagedChannel channel = ChannelBuilder.buildChannel(arguments.get().getServerAddress());
        CounterClientAction action = arguments.get().getAction();
        if(action == null){
            System.out.println("No valid action selected.");
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
