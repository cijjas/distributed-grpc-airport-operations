package ar.edu.itba.pod.tpe1.client;

import ar.edu.itba.pod.grpc.AirportAdminServiceGrpc;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.itba.pod.grpc.StatusResponse;

import java.util.concurrent.TimeUnit;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("tpe1-g7 Client Starting ...");
        logger.info("grpc-com-patterns Client Starting ...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        try {
            AirportAdminServiceGrpc.AirportAdminServiceBlockingStub stub = AirportAdminServiceGrpc.newBlockingStub(channel);

            String newSector = "A";
            int countersToAdd = 10;

            StatusResponse responseStatus = stub.addSector(StringValue.of(newSector));
            if (responseStatus.getCode() == Status.OK.getCode().value()) {
                System.out.println("Sector " + newSector + " added successfully");
            }
            else{
                System.out.println(responseStatus.getMessage());
            }

            responseStatus = stub.addCounters(ar.edu.itba.pod.grpc.AddCountersRequest.newBuilder().setCounterCount(countersToAdd).setSectorName(newSector).build());
            if (responseStatus.getCode() == Status.OK.getCode().value()) {
                System.out.println(countersToAdd + " new counters (" + responseStatus.getMessage() + ") in Sector " + newSector + " added successfully");
            }
            else{
                System.out.println(responseStatus.getMessage());
            }

        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
