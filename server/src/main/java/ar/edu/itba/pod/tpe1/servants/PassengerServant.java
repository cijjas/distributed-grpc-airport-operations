package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Pair;
import ar.edu.itba.pod.tpe1.models.Sector;
import ar.edu.itba.pod.tpe1.models.Triple;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassengerServant extends PassengerServiceGrpc.PassengerServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(PassengerServant.class);
    private final AirportRepository airportRepository;

    public PassengerServant(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Override
    public void fetchCounter(StringValue request, StreamObserver<FetchCounterResponse> responseObserver) {
        try {
            Triple<CounterGroup, Booking, String> counterWithBooking =  airportRepository.fetchCounter(request.getValue());
            CounterGroup counters = counterWithBooking.getLeft();
            Booking booking = counterWithBooking.getMiddle();
            String sectorName = counterWithBooking.getRight();
            if(counters == null) {
                responseObserver.onNext(
                        FetchCounterResponse.newBuilder()
                                .setStatus(StatusResponse.newBuilder()
                                        .setCode(Status.OK.getCode().value())
                                        .setMessage("Requested booking flight has no counter assigned yet.")
                                        .build())
                                .setAirlineName(booking.getAirlineName())
                                .setFlightCode(booking.getFlightCode())
                                .build()
                );
            }
            else {
                responseObserver.onNext(
                        FetchCounterResponse.newBuilder()
                                .setStatus(StatusResponse.newBuilder()
                                        .setCode(Status.OK.getCode().value())
                                        .setMessage("Fetch counter successfully")
                                        .build())
                                .setCounterFrom(counters.getCounterStart())
                                .setCounterTo(counters.getCounterStart() + counters.getCounterCount() -1)
                                .setAirlineName(counters.getAirlineName())
                                .setFlightCode(booking.getFlightCode())
                                .setSectorName(sectorName)
                                .setPeopleInLine(counters.getPendingPassengers().size()) // CHEQUEAR
                                .build()
                );
            }

        } catch (IllegalArgumentException e) {
            responseObserver.onNext(
                    FetchCounterResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.INVALID_ARGUMENT.getCode().value())
                                    .setMessage(e.getMessage())
                                    .build())
                            .build()
            );
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void passengerCheckin(PassengerCheckinRequest request, StreamObserver<PassengerCheckinResponse> responseObserver) {
        try{
            Pair<Booking, CounterGroup> bookingCounterGroupPair = airportRepository.passengerCheckin(request.getBookingCode(), request.getSectorName(), request.getCounterFrom());
            Booking booking = bookingCounterGroupPair.getLeft();
            CounterGroup counterGroup = bookingCounterGroupPair.getRight();
            responseObserver.onNext(
                    PassengerCheckinResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.OK.getCode().value())
                                    .setMessage("Passenger checked in successfully")
                                    .build())
                            .setBookingCode(request.getBookingCode())
                            .setFlightCode(booking.getFlightCode())
                            .setAirlineName(counterGroup.getAirlineName())
                            .setCounterFrom(counterGroup.getCounterStart())
                            .setCounterTo(counterGroup.getCounterStart() + counterGroup.getCounterCount() - 1)
                            .setSectorName(request.getSectorName())
                            .setPeopleInLine(counterGroup.getPendingPassengers().size())
            .build());
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(
                    PassengerCheckinResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.INVALID_ARGUMENT.getCode().value())
                                    .setMessage(e.getMessage())
                                    .build())
                            .build()
            );
        } finally {
            responseObserver.onCompleted();

        }
    }

    @Override
    public void passengerStatus(StringValue request, StreamObserver<PassengerStatusResponse> responseObserver) {

    }
}
