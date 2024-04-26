package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.EventResponse;
import ar.edu.itba.pod.grpc.EventsServiceGrpc;
import ar.edu.itba.pod.grpc.StatusResponse;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EventsServant extends EventsServiceGrpc.EventsServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(EventsServant.class);
    private final ConcurrentMap<String, StreamObserver<EventResponse>> subscribers;

    public EventsServant(ConcurrentMap<String, StreamObserver<EventResponse>> subscribers) {
        this.subscribers = subscribers;
    }


    @Override
    public void register(StringValue request, StreamObserver<EventResponse> responseObserver) {
        logger.info("Registering for airline: {}", request.getValue());
        String airlineName = request.getValue();
        subscribers.put(airlineName.toUpperCase(), responseObserver);
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
        StreamObserver<EventResponse> observer = subscribers.remove(airlineName.toUpperCase());
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

    public boolean isRegistered(String airlineName){
        return subscribers.containsKey(airlineName.toUpperCase());
    }

    public void notify(String airlineName, String message) {
        StreamObserver<EventResponse> observer = subscribers.get(airlineName.toUpperCase());
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
