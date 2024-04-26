package ar.edu.itba.pod.tpe1.client.events.actions;

import ar.edu.itba.pod.grpc.EventResponse;
import ar.edu.itba.pod.grpc.EventsServiceGrpc;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.events.EventsClient;
import ar.edu.itba.pod.tpe1.client.events.EventsClientArguments;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import jdk.jfr.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class RegisterAction implements Action {
    ManagedChannel channel;
    EventsClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(EventsClient.class);

    public RegisterAction(ManagedChannel channel, EventsClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }

    @Override
    public void execute() {
        try {
            register(channel, arguments.getAirline());
        } catch (Exception e) {
            logger.error("Failed to register", e);
        }
    }

    private void register(ManagedChannel channel, String airlineName) {
        EventsServiceGrpc.EventsServiceStub asyncStub = EventsServiceGrpc.newStub(channel);
        StreamObserver<EventResponse> responseObserver = new StreamObserver<EventResponse>() {
            @Override
            public void onNext(EventResponse value) {
                logger.info("Received notification: " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Streaming error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("Stream completed");
            }
        };

        asyncStub.register(
            StringValue
                .newBuilder()
                .setValue(airlineName)
                .build(),
            responseObserver
        );
    }
}
