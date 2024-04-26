package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.EventResponse;
import ar.edu.itba.pod.grpc.EventsServiceGrpc;
import ar.edu.itba.pod.grpc.StatusResponse;
import ar.edu.itba.pod.tpe1.repositories.NotificationRepository;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Queue;

public class EventsServant extends EventsServiceGrpc.EventsServiceImplBase {
    private final NotificationRepository notificationRepository;

    public EventsServant(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void register(StringValue request, StreamObserver<EventResponse> responseObserver) {
        String airlineName = request.getValue();
        notificationRepository.putSubscriber(airlineName, responseObserver);

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
        StreamObserver<EventResponse> observer = notificationRepository.removeSubscriber(airlineName);
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
}
