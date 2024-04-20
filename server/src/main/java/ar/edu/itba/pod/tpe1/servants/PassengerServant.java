package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Pair;
import ar.edu.itba.pod.tpe1.models.PassengerStatus.PassengerStatusInfo;
import ar.edu.itba.pod.tpe1.models.Sector;
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

    }

    @Override
    public void passengerStatus(StringValue request, StreamObserver<PassengerStatusResponse> responseObserver) {
        try{
            PassengerStatusInfo passengerStatusInfo = airportRepository.passengerStatus(request.getValue());
            PassengerStatusResponse.Builder responseBuilder = PassengerStatusResponse.newBuilder()
                    .setStatusResponse(
                            StatusResponse.newBuilder()
                                    .setCode(Status.OK.getCode().value())
                                    .setMessage("Passenger status fetched successfully")
                                    .build()
                    )
                    .setBookingCode(request.getValue())
                    .setFlightCode(passengerStatusInfo.getBooking().getFlightCode())
                    .setAirlineName(passengerStatusInfo.getCounterGroup().getAirlineName())
                    .setSectorName(passengerStatusInfo.getSectorName())
                    .setStatus(passengerStatusInfo.getCheckInStatus());

            CheckInStatus checkInStatus = passengerStatusInfo.getCheckInStatus();

            if (checkInStatus == CheckInStatus.NOT_CHECKED_IN || checkInStatus == CheckInStatus.AWAITING) {
                responseBuilder
                        .setCounterFrom(passengerStatusInfo.getCounterGroup().getCounterStart())
                        .setCounterTo(passengerStatusInfo.getCounterGroup().getCounterStart() + passengerStatusInfo.getCounterGroup().getCounterCount() - 1);
            }
            if (checkInStatus == CheckInStatus.AWAITING) {
                responseBuilder.setPeopleInLine(passengerStatusInfo.getCounterGroup().getPendingPassengers().size());
            }
            if (checkInStatus == CheckInStatus.CHECKED_IN) {
                responseBuilder.setCounterChecked(666/* TODO actual checked-in counter number here */);
            }

            responseObserver.onNext(responseBuilder.build());

        }
        catch   (IllegalArgumentException e) {
            responseObserver.onNext(
                    PassengerStatusResponse.newBuilder()
                            .setStatusResponse(
                                    StatusResponse.newBuilder()
                                            .setCode(Status.INVALID_ARGUMENT.getCode().value())
                                            .setMessage(e.getMessage())
                                            .build()
                            )
                            .build()
            );
        } finally {
            responseObserver.onCompleted();
        }

    }
}
