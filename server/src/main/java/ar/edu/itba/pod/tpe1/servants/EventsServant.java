package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.EventResponse;
import ar.edu.itba.pod.grpc.EventsServiceGrpc;
import ar.edu.itba.pod.grpc.StatusResponse;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EventsServant extends EventsServiceGrpc.EventsServiceImplBase {
    private final ConcurrentMap<String, StreamObserver<EventResponse>> subscribers = new ConcurrentHashMap<>();

    public EventsServant() {
    }

    @Override
    public void register(StringValue request, StreamObserver<EventResponse> responseObserver) {
        String airlineName = request.getValue();
        subscribers.put(airlineName, responseObserver);
        responseObserver.onNext(EventResponse.newBuilder()
                .setMessage("Registered successfully for " + airlineName)
                .setStatus(
                        StatusResponse.newBuilder()
                                .setCode(Status.OK.getCode().value())
                                .setMessage("Registered successfully")
                                .build()
                )
                .build());
    }

    @Override
    public void unregister(StringValue request, StreamObserver<EventResponse> responseObserver) {
        String airlineName = request.getValue();
        StreamObserver<EventResponse> observer = subscribers.remove(airlineName);
        if (observer != null) {
            observer.onCompleted();  // Close the stream
        }
        responseObserver.onNext(EventResponse.newBuilder()
                .setMessage("Unregistered successfully for " + airlineName)
                .setStatus(StatusResponse.newBuilder()
                        .setCode(Status.OK.getCode().value())
                        .setMessage("Unregistered successfully")
                        .build())
                .build());
        responseObserver.onCompleted();
    }

    public void notify(String airlineName, String message) {
        StreamObserver<EventResponse> observer = subscribers.get(airlineName);
        if (observer != null) {
            observer.onNext(EventResponse.newBuilder()
                    .setMessage(message)
                    .setStatus(StatusResponse.newBuilder()
                            .setCode(Status.OK.getCode().value())
                            .setMessage("Notification sent")
                            .build())
                    .build());
        }
    }
}
