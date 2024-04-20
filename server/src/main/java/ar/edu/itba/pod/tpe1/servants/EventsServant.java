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
        try{
            notificationRepository.registerAirline(airlineName);
            responseObserver.onNext(EventResponse.newBuilder().setMessage(airlineName + " registered successfully for check-in events").build());
            while (notificationRepository.isAirlineRegistered(airlineName)){
                if(notificationRepository.hasNewNotifications(airlineName)){
                    List<String> notifications = notificationRepository.getLatestNotifications(airlineName);
                    for(String notification : notifications){
                        responseObserver.onNext(EventResponse.newBuilder().setMessage(notification).setStatus(StatusResponse.newBuilder().setCode(Status.OK.getCode().value()).build()).build());
                    }
                }
            }
        } catch (IllegalArgumentException e){
            responseObserver.onNext(EventResponse.newBuilder().setStatus(StatusResponse.newBuilder().setCode(Status.INVALID_ARGUMENT.getCode().value()).setMessage(e.getMessage()).build()).build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void unregister(StringValue request, StreamObserver<EventResponse> responseObserver) {
        try{
            notificationRepository.unregisterAirline(request.getValue());
            responseObserver.onNext(EventResponse.newBuilder().setMessage(request.getValue() + " unregistered successfully for events").build());
        } catch (IllegalArgumentException e){
            responseObserver.onNext(EventResponse.newBuilder().setStatus(StatusResponse.newBuilder().setCode(Status.INVALID_ARGUMENT.getCode().value()).setMessage(e.getMessage()).build()).build());
        } finally {
            responseObserver.onCompleted();
        }
    }
}
