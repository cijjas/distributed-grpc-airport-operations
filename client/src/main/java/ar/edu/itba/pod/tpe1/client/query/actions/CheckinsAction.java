package ar.edu.itba.pod.tpe1.client.query.actions;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClient;
import ar.edu.itba.pod.tpe1.client.passenger.PassengerClientArguments;
import ar.edu.itba.pod.tpe1.client.query.QueryClient;
import ar.edu.itba.pod.tpe1.client.query.QueryClientArguments;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckinsAction implements Action {
    ManagedChannel channel;
    QueryClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(QueryClient.class);


    public CheckinsAction(ManagedChannel channel, QueryClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            if(arguments.getSector() == null){
                System.out.println("hola sin");
                checkins(channel);
            }
            else {
                System.out.println("hola con");
                checkinsBySector(channel, arguments.getSector());
            }
        } catch (Exception e) {
            logger.error("Failed to get checkins", e);
        }
    }

    private void checkins(ManagedChannel channel) {
        // TODO: Implement
    }

    private void checkinsBySector(ManagedChannel channel, String sector) {
        // TODO:IMPLEMENT
    }
}
