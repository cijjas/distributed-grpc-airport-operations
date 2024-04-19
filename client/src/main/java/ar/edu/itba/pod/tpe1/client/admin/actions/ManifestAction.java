package ar.edu.itba.pod.tpe1.client.admin.actions;

import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.BookingData;
import ar.edu.itba.pod.grpc.ManifestRequest;
import ar.edu.itba.pod.grpc.ManifestResponse;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.admin.AdminClient;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientArguments;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ManifestAction implements Action {
    ManagedChannel channel;
    AdminClientArguments arguments;

    public ManifestAction(ManagedChannel channel, AdminClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        Optional<Path> manifestPath = arguments.getInPath();
        if (manifestPath.isPresent()) {
            manifest(channel, manifestPath.get());
        } else {
            System.out.println("Input path is required for 'manifest' action.");
        }
    }

    private static void manifest(ManagedChannel channel, Path manifestPath)  {
        AdminServiceGrpc.AdminServiceStub asyncStub = AdminServiceGrpc.newStub(channel);
        final CountDownLatch finishLatch = new CountDownLatch(1); // Latch for waiting to finish all responses

        StreamObserver<ManifestResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(ManifestResponse serverResponse) {
                if (serverResponse.getStatus().getCode() == Status.OK.getCode().value()) {
                    String message = String.format("Booking %s for %s %s added successfully",
                            serverResponse.getPassenger().getBookingCode(),
                            serverResponse.getPassenger().getAirlineName(),
                            serverResponse.getPassenger().getFlightCode());
                    System.out.println(message);
                } else {
                    System.out.println(serverResponse.getStatus().getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        };

        StreamObserver<ManifestRequest> requestObserver = asyncStub.manifest(responseObserver);
        try (Stream<String> lines = Files.lines(manifestPath)) {
            lines.skip(1) // Assuming the first line is a header
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

        try {
            finishLatch.await();
            channel.shutdownNow();
            if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Channel did not terminate within the allowed time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted while waiting for channel to terminate");
        } finally {
            if (!channel.isShutdown()) {
                channel.shutdownNow();
            }
        }

    }
}
