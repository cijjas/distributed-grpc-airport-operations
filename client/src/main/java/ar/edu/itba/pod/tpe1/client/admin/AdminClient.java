package ar.edu.itba.pod.tpe1.client.admin;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.client.ChannelBuilder;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

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
                Optional<String> sector = arguments.get().getSector();
                if (sector.isPresent()) {
                    addSector(channel, sector.get());
                } else {
                    logger.error("Sector name is required for 'addSector' action.");
                }
                break;
            case ADDCOUNTERS:
                Optional<String> sectorName = arguments.get().getSector();
                Optional<Integer> counterCount = arguments.get().getCounters();
                if (sectorName.isPresent() && counterCount.isPresent()) {
                    addCounters(channel, sectorName.get(), counterCount.get());
                } else {
                    logger.error("Sector name and counter count are required for 'addCounters' action.");
                }
                break;
            case MANIFEST:
                Optional<Path> manifestPath = arguments.get().getInPath();
                if (manifestPath.isPresent()) {
                    manifest(channel, manifestPath.get());
                } else {
                    logger.error("Input path is required for 'manifest' action.");
                }
                break;
            default:
                logger.error("No valid action selected.");
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
        }
        else{
            System.out.println(serverResponse.getStatus().getMessage());
        }
    }

    private static void manifest(ManagedChannel channel, Path manifestPath) throws IOException {
        AdminServiceGrpc.AdminServiceStub asyncStub = AdminServiceGrpc.newStub(channel);
        StreamObserver<ManifestResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(ManifestResponse serverResponse) {
                String message = String.format("Booking %s for %s %s added successfully",
                        serverResponse.getPassenger().getBookingCode(),
                        serverResponse.getPassenger().getAirlineName(),
                        serverResponse.getPassenger().getFlightCode());
                System.out.println(message);
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Error in manifest response", t);
            }

            @Override
            public void onCompleted() {
                logger.info("Manifest response completed");
            }
        };

        StreamObserver<ManifestRequest> requestObserver = asyncStub.manifest(responseObserver);
        try (Stream<String> lines = Files.lines(manifestPath)) {
            lines.skip(1)
                    .map(line -> line.split(";"))
                    .forEach(parts -> {
                        BookingData bookingData = BookingData.newBuilder()
                                .setBookingCode(parts[0])
                                .setFlightCode(parts[1])
                                .setAirlineName(parts[2])
                                .build();
                        requestObserver.onNext(ManifestRequest.newBuilder().setPassenger(bookingData).build());
                    });
            requestObserver.onCompleted();
        } catch (Exception e) {
            requestObserver.onError(e);
        }
    }
}
