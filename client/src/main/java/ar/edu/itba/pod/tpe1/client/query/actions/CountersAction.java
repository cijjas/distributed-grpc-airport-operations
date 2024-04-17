package ar.edu.itba.pod.tpe1.client.query.actions;

import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.query.QueryClient;
import ar.edu.itba.pod.tpe1.client.query.QueryClientArguments;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountersAction implements Action {
    ManagedChannel channel;
    QueryClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(QueryClient.class);


    public CountersAction(ManagedChannel channel, QueryClientArguments arguments) {
        this.arguments = arguments;
        this.channel = channel;
    }

    @Override
    public void execute() {
        try {
            counters(channel);
        } catch (Exception e) {
            logger.error("Failed to get counters", e);
        }

    }

    private void counters(ManagedChannel channel) {
        // TODO: Implement
    }
}
