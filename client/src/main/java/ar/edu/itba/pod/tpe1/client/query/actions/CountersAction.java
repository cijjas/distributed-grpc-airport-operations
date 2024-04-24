package ar.edu.itba.pod.tpe1.client.query.actions;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.query.QueryClient;
import ar.edu.itba.pod.tpe1.client.query.QueryClientArguments;
import com.google.protobuf.Option;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

public class CountersAction implements Action {
    ManagedChannel channel;
    QueryClientArguments arguments;


    public CountersAction(ManagedChannel channel, QueryClientArguments arguments) {
        this.arguments = arguments;
        this.channel = channel;
    }

    @Override
    public void execute() {
        try {
            if(arguments.getSector() == null){
                counters(channel, arguments.getOutPath());
            }
            else {
                countersBySector(channel, arguments.getSector(), arguments.getOutPath());
            }
        } catch (Exception e) {
            System.out.println("Failed to get counters");
        }

    }

    private void counters(ManagedChannel channel, Path outPath) {
        countersBySector(channel, null, outPath);
    }

    private void countersBySector(ManagedChannel channel, String sector, Path outPath) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        CountersRequest.Builder requestBuilder = CountersRequest.newBuilder();
        if (sector != null) {
            requestBuilder.setSectorName(sector);
        }
        CountersRequest request = requestBuilder.build();

        Optional<CountersResponse> response = Optional.ofNullable(stub.counters(request));
        response.ifPresentOrElse(
                this::handleResponse,
                () -> System.out.printf("Failed to get status for sector: %s%n", sector)
        );
    }

    private void handleResponse(CountersResponse response) {
        // Print the response
        System.out.println("Counters:");
    }

}
