package ar.edu.itba.pod.tpe1.client.admin;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import ar.edu.itba.pod.tpe1.client.admin.actions.AddCountersAction;
import ar.edu.itba.pod.tpe1.client.admin.actions.AddSectorAction;
import ar.edu.itba.pod.tpe1.client.admin.actions.ManifestAction;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class AdminClient {
    private static final Logger logger = LoggerFactory.getLogger(AdminClient.class);

    public static void main(String[] args) throws IOException {

        logger.info("tpe1-g7 AdminClient Starting ...");
        AdminClientParser parser = new AdminClientParser();
        Optional<AdminClientArguments> arguments = parser.getAdminClientArguments(args);
        if(arguments.isEmpty()){
            logger.error("No arguments provided");
            return;
        }

        ManagedChannel channel = ChannelBuilder.buildChannel(arguments.get().getServerAddress());
        AdminClientAction action = arguments.get().getAction();
        if(action == null){
            logger.error("No valid action selected.");
            return;
        }

        Action executableAction = getAction(action, channel, arguments.get());

        executableAction.execute();

    }


    private static Action getAction(AdminClientAction action, ManagedChannel channel, AdminClientArguments arguments) {
        return switch (action) {
            case ADDSECTOR -> new AddSectorAction(channel, arguments);
            case ADDCOUNTERS -> new AddCountersAction(channel, arguments);
            case MANIFEST -> new ManifestAction(channel, arguments);
        };
    }





}
