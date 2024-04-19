package ar.edu.itba.pod.tpe1.client.admin.actions;

import ar.edu.itba.pod.grpc.AddCountersRequest;
import ar.edu.itba.pod.grpc.AddCountersResponse;
import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.admin.AdminClient;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientArguments;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import java.util.Optional;

public class AddCountersAction implements Action {
    ManagedChannel channel;
    AdminClientArguments arguments;

    public AddCountersAction(ManagedChannel channel, AdminClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        Optional<String> sectorName = arguments.getSector();
        Optional<Integer> counterCount = arguments.getCounters();
        if (sectorName.isPresent() && counterCount.isPresent()) {
            addCounters(channel, sectorName.get(), counterCount.get());
        } else {
            System.out.println("Sector name and counter count are required for 'addCounters' action.");
        }
    }

    private static void addCounters(ManagedChannel channel, String sector, Integer counterCount) {
        AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

        AddCountersResponse serverResponse = stub.addCounters(
                AddCountersRequest.newBuilder()
                        .setSectorName(sector)
                        .setCounterCount(counterCount)
                        .build());

        if (serverResponse.getStatus().getCode() == Status.OK.getCode().value()) {
            System.out.printf("%d new counters (%d-%d) in Sector %s added successfully%n",
                    serverResponse.getCounterCount(),
                    serverResponse.getFirstCounterNumber(),
                    serverResponse.getLastCounterNumber(),
                    serverResponse.getSectorName());
        } else {
            System.out.println(serverResponse.getStatus().getMessage());
        }
    }
}
