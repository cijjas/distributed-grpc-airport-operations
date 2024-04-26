package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.grpc.EventResponse;
import ar.edu.itba.pod.grpc.StatusResponse;
import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import ar.edu.itba.pod.tpe1.models.CounterGroup.AssignedCounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.Sector;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class NotificationRepository {
    private final ConcurrentMap<String, StreamObserver<EventResponse>> subscribers = new ConcurrentHashMap<>();

    private final PassengerRepository passengerRepository;

    public NotificationRepository(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public void putSubscriber(String airlineName, StreamObserver<EventResponse> responseObserver) {
        if (subscribers.containsKey(airlineName))
            throw new IllegalArgumentException("Airline already registered");
        if (!passengerRepository.passengersExpectedForAirline(airlineName))
            throw new IllegalArgumentException("No expected passengers found for airline");

        subscribers.put(airlineName, responseObserver);
    }

    public StreamObserver<EventResponse> removeSubscriber(String airlineName) {
        if (!subscribers.containsKey(airlineName))
            throw new IllegalArgumentException("Airline Not Registered");

        return subscribers.remove(airlineName);
    }

    public void publishEvent(String airlineName, String message) {
        EventResponse event = EventResponse.newBuilder()
                .setMessage(message)
                .setStatus(StatusResponse.newBuilder()
                        .setCode(Status.OK.getCode().value())
                        .setMessage("Event published successfully")
                        .build())
                .build();
        StreamObserver<EventResponse> observer = subscribers.get(airlineName);
        if (observer != null) {
            observer.onNext(event);
        }
    }



}