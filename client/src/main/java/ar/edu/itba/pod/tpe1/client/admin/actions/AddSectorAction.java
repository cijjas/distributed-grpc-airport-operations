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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AddSectorAction implements Action {
    ManagedChannel channel;
    AdminClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(AdminClient.class);

    public AddSectorAction(ManagedChannel channel, AdminClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        Optional<String> sectorName = arguments.getSector();
        if (sectorName.isPresent()) {
            addSector(channel, sectorName.get());
        } else {
            logger.error("Sector name is required for 'addSector' action.");
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
