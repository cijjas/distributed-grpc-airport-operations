package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.models.BookingHist;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.SortedMap;

public class CounterServant extends CounterServiceGrpc.CounterServiceImplBase {


    private static final Logger log = LoggerFactory.getLogger(CounterServant.class);
    private final AirportRepository airportRepository;

    public CounterServant(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Override
    public void listSectors(Empty request, StreamObserver<ListSectorsResponse> responseObserver) {
        try {
            SortedMap<String, SortedMap<Integer, Integer>> sectors = airportRepository.listSectors();
            ListSectorsResponse.Builder responseBuilder = ListSectorsResponse.newBuilder();
            sectors.forEach((sectorName, counters) -> {
                Sector.Builder sectorBuilder = Sector.newBuilder().setSectorName(sectorName);
                counters.forEach((from, count) -> sectorBuilder.addCounterRanges(
                        CounterRange.newBuilder().setCounterFrom(from).setCounterTo(from + count -1).build()
                ));
                responseBuilder.addSectors(sectorBuilder.build());
            });

            responseBuilder.setStatus(
                    StatusResponse.newBuilder()
                            .setCode(Status.OK.getCode().value())
                            .setMessage("Sectors listed successfully")
                            .build()
            );
            responseObserver.onNext(responseBuilder.build());
        } catch (IllegalStateException e) {
            responseObserver.onNext(
                    ListSectorsResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.NOT_FOUND.getCode().value())
                                    .setMessage(e.getMessage())
                                    .build())
                            .build()
            );
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listCounters(ListCountersRequest request, StreamObserver<ListCountersResponse> responseObserver) {
        try {
            SortedMap<Integer, CounterGroup> counters = airportRepository.listCounters(request.getSectorName());
            ListCountersResponse.Builder responseBuilder = ListCountersResponse.newBuilder();
            counters.forEach((from, counterGroup) -> responseBuilder.addCounters(
                    Counter.newBuilder()
                            .setAirlineName(counterGroup.getAirlineName())
                            .setCounterRange(
                                    CounterRange.newBuilder()
                                    .setCounterFrom(from)
                                    .setCounterTo(from + counterGroup.getCounterCount() - 1)
                                    .build()
                            )
                            .addAllFlightCodes(counterGroup.getFlightCodes())
                            .setPeopleInLine(counterGroup.getPendingPassengers().size())
                            .build()));

            responseBuilder.setStatus(
                    StatusResponse.newBuilder()
                            .setCode(io.grpc.Status.OK.getCode().value())
                            .setMessage("Counters listed successfully")
                            .build()
            );
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void assignCounters(AssignCountersRequest request, StreamObserver<AssignCountersResponse> responseObserver) {
        try {
            airportRepository.assignCounters(request.getSectorName(), request.getAirlineName(), request.getFlightCodesList(), request.getCounterCount());

            responseObserver.onNext(
                    AssignCountersResponse.newBuilder()
                        .setStatus(StatusResponse.newBuilder()
                            .setCode(io.grpc.Status.OK.getCode().value())
                            .setMessage("Counters assigned successfully")
                            .build()
                        )
                    .setSectorName(request.getSectorName())
                    .setAirlineName(request.getAirlineName())
                    .addAllFlightCodes(request.getFlightCodesList())
                    .setCounterCount(request.getCounterCount())
                    .setCounterFrom(666)
                    .setCounterTo(666)
                    .build()
            );

            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void freeCounters(FreeCountersRequest request, StreamObserver<FreeCountersResponse> responseObserver) {
        try {

            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void checkinCounters(CheckinCountersRequest request, StreamObserver<CheckinCountersResponse> responseObserver) {
        try {
            List<BookingHist> checkins = airportRepository.checkInCounters(request.getSectorName(), request.getCounterFrom(), request.getAirlineName());
            CheckinCountersResponse.Builder responseBuilder = CheckinCountersResponse.newBuilder();
            checkins.forEach(bookingHist ->
                    responseBuilder.addCheckins(
                        Checkin.newBuilder()
                            .setBookingCode(bookingHist.getBookingCode())
                            .setFlightCode(bookingHist.getFlightCode())
                            .setCounterNumber(bookingHist.getCheckinCounter())
                            .build()
                    )

            );
            responseBuilder.setStatus(
                    StatusResponse.newBuilder()
                            .setCode(io.grpc.Status.OK.getCode().value())
                            .setMessage("Passengers checked in successfully")
                            .build()
            );
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    // TODO: revisar fuerte, no entiendno poruqe hay assignment.counterCount() y no un getter, el
    @Override
    public void listPendingAssignments(StringValue request, StreamObserver<ListPendingAssignmentsResponse> responseObserver) {
        try {
            List<CheckinAssignment> assignments = airportRepository.listPendingAssignments(request.getValue());
            ListPendingAssignmentsResponse.Builder responseBuilder = ListPendingAssignmentsResponse.newBuilder();
            assignments.forEach(assignment ->
                    responseBuilder.addPendingAssignments(
                        PendingAssignment.newBuilder()
                            .setCounterCount(assignment.counterCount())
                            .setAirlineName(assignment.airlineName())
                            .addAllFlightCodes(assignment.flightCodes())
                            .build()
                    )
            );
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
