package ar.edu.itba.pod.tpe1.client.admin;

import ar.edu.itba.pod.grpc.AirportAdminServiceGrpc;
import ar.edu.itba.pod.grpc.StatusResponse;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
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
        switch (arguments.get().getAction()){
            case ADDSECTOR:
                if (arguments.get().getSector().isPresent()) {
                    addSector(channel, arguments.get().getSector().get());
                } else {
                    logger.error("Sector name is required for 'addSector' action.");
                }
                break;
            case ADDCOUNTERS:
                if (arguments.get().getSector().isPresent() && arguments.get().getCounters().isPresent()) {
                    addCounters(channel, arguments.get().getSector().get(), arguments.get().getCounters().get());
                } else {
                    logger.error("Sector name and counter count are required for 'addCounters' action.");
                }
                break;
            case MANIFEST:
                if (arguments.get().getInPath() != null) {
                    manifest(channel, arguments.get().getInPath());
                } else {
                    logger.error("Input path is required for 'manifest' action.");
                }
                break;
            default:
                logger.error("No valid action selected.");
        }
    }



    private static void addSector(ManagedChannel channel, String sector) {

        AirportAdminServiceGrpc.AirportAdminServiceBlockingStub stub = AirportAdminServiceGrpc.newBlockingStub(channel);

        StatusResponse responseStatus = stub.addSector(StringValue.of(sector));
        if (responseStatus.getCode() == Status.OK.getCode().value()) {
            System.out.println("Sector " + sector + " added successfully");
        }
        else{
            System.out.println(responseStatus.getMessage());
        }
    }

    private static void addCounters(ManagedChannel channel, String sector, Integer counterCount) {
        AirportAdminServiceGrpc.AirportAdminServiceBlockingStub stub = AirportAdminServiceGrpc.newBlockingStub(channel);

        StatusResponse responseStatus = stub.addCounters(ar.edu.itba.pod.grpc.AddCountersRequest.newBuilder().setCounterCount(counterCount).setSectorName(sector).build());
        if (responseStatus.getCode() == Status.OK.getCode().value()) {
            System.out.println(counterCount + " new counters (" + responseStatus.getMessage() + ") in Sector " + sector + " added successfully");
        }
        else{
            System.out.println(responseStatus.getMessage());
        }
    }

    private static void manifest(ManagedChannel channel, Path manifestPath) throws IOException {
    }
}
