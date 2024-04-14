package ar.edu.itba.pod.tpe1.client.admin;

import ar.edu.itba.pod.grpc.AirportAdminServiceGrpc;
import ar.edu.itba.pod.grpc.StatusResponse;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class AdminClient {
    private static final Logger logger = LoggerFactory.getLogger(AdminClient.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("tpe1-g7 AdminClient Starting ...");
        if(args.length < 2){
            System.out.println("Error");
        }

        String serverAddress = args[0];
        String action = args[1];

        ManagedChannel channel = ChannelBuilder.buildChannel(serverAddress);

        try {
            switch (action) {
                case "addSector":
                    if (args.length != 3) {
                        System.out.println("Insufficient arguments for 'addSector' action");
                        return;
                    }
                    addSector(channel, args[2]);
                    break;
                case "addCounters":
                    if (args.length != 4) {
                        System.out.println("Insufficient arguments for 'addCounters' action");
                        return;
                    }
                    Integer counterCount = Integer.parseInt(args[3]);
                    addCounters(channel, args[2], counterCount);
                    break;
                case "manifest":
                    if (args.length != 3) {
                        System.out.println("Insufficient arguments for 'manifest' action");
                        return;
                    }
                    Path csvPath = Paths.get(args[2]);
                    manifest(channel, csvPath);
                    break;
                default:
                    System.out.println("Unknown action: " + action);
                    return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid counter count provided for 'addCounters' action");
        } catch (InvalidPathException e) {
            System.out.println("Invalid CSV file path provided for 'manifest' action");
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
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

    private static void manifest(ManagedChannel channel, Path manifestPath) {
        // Logic for the "manifest" action
    }
}
