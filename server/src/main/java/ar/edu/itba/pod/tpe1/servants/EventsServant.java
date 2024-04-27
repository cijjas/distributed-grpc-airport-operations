package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.EventResponse;
import ar.edu.itba.pod.grpc.EventsServiceGrpc;
import ar.edu.itba.pod.grpc.StatusResponse;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

public class EventsServant extends EventsServiceGrpc.EventsServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(EventsServant.class);
    private final AirportRepository airportRepository;
    private final ConcurrentMap<String, StreamObserver<EventResponse>> subscribers;

    public EventsServant(AirportRepository airportRepository, ConcurrentMap<String, StreamObserver<EventResponse>> subscribers) {
        this.airportRepository = airportRepository;
        this.subscribers = subscribers;
    }


    @Override
    public void register(StringValue request, StreamObserver<EventResponse> responseObserver) {
        logger.info("Registering for airline: {}", request.getValue());
        String airlineName = request.getValue();


        if(!airportRepository.getAirlineRepository().airlineExists(airlineName)){
            responseObserver.onNext(EventResponse.newBuilder()
                    .setStatus(
                            StatusResponse.newBuilder()
                                    .setCode(Status.INVALID_ARGUMENT.getCode().value())
                                    .setMessage("No passengers expected for airline or airline does not exist")
                                    .build()
                    )
                    .build());
            responseObserver.onCompleted();
            return;
        }
        if(subscribers.containsKey(airlineName.toUpperCase())){
            responseObserver.onNext(EventResponse.newBuilder()
                    .setStatus(
                            StatusResponse.newBuilder()
                                    .setCode(Status.INVALID_ARGUMENT.getCode().value())
                                    .setMessage("Airline is registered for notifications")
                                    .build()
                    )
                    .build());
            responseObserver.onCompleted();
            return;
        }

        subscribers.put(airlineName.toUpperCase(), responseObserver);
        responseObserver.onNext(EventResponse.newBuilder()
                .setMessage(airlineName + " registered successfully for events")
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

        if(!subscribers.containsKey(airlineName.toUpperCase())){
            responseObserver.onNext(EventResponse.newBuilder()
                    .setStatus(
                            StatusResponse.newBuilder()
                                    .setCode(Status.INVALID_ARGUMENT.getCode().value())
                                    .setMessage("Airline was not registered for notifications")
                                    .build()
                    )
                    .build());
            responseObserver.onCompleted();
            return;
        }

        StreamObserver<EventResponse> observer = subscribers.remove(airlineName.toUpperCase());
        if (observer != null) {
            observer.onCompleted();  // Close the stream
        }
        responseObserver.onNext(EventResponse.newBuilder()
                .setMessage(airlineName + " unregistered successfully for events")
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
