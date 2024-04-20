package ar.edu.itba.pod.tpe1.client.admin.actions;

import ar.edu.itba.pod.grpc.AddCountersRequest;
import ar.edu.itba.pod.grpc.AddCountersResponse;
import ar.edu.itba.pod.grpc.AddSectorResponse;
import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.admin.AdminClient;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientAction;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientArguments;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;

import java.util.Optional;

public class AddSectorAction implements Action {
    ManagedChannel channel;
    AdminClientArguments arguments;

    public AddSectorAction(ManagedChannel channel, AdminClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            addSector(channel, arguments.getSector());
        } catch (Exception e) {
            System.out.println("Failed to add sector");
            System.out.println("Should have arguments: -Dsector");
        }
    }

    private static void addSector(ManagedChannel channel, String sector) {

        AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

        AddSectorResponse serverResponse = stub.addSector(StringValue.of(sector));

        if (serverResponse.getStatus().getCode() == Status.OK.getCode().value()) {
            System.out.printf("Sector %s added successfully%n", serverResponse.getSectorName());
        }
        else{
            System.out.println(serverResponse.getStatus().getMessage());
        }
    }

}
