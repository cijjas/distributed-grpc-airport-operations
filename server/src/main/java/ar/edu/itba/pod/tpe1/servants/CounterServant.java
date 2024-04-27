package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Pair;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.IntStream;

public class CounterServant extends CounterServiceGrpc.CounterServiceImplBase {


    private static final Logger log = LoggerFactory.getLogger(CounterServant.class);
    private final AirportRepository airportRepository;
    private final EventsServant eventsServant;

    public CounterServant(AirportRepository airportRepository,EventsServant eventsServant) {
        this.airportRepository = airportRepository;
        this.eventsServant = eventsServant;
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
        } catch (Exception e) {
            responseObserver.onNext(
                    ListSectorsResponse.newBuilder()
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
    public void listCounters(ListCountersRequest request, StreamObserver<ListCountersResponse> responseObserver) {
        try {
            SortedMap<Integer, CounterGroup> counters = airportRepository.listCounters(request.getSectorName(), request.getCounterFrom(), request.getCounterTo());
            ListCountersResponse.Builder responseBuilder = ListCountersResponse.newBuilder();
            counters.forEach((counterIndex, counterGroup) -> {
                        String airlineName;
                        List<String> flightCodes;
                        int peopleInLine;
                        if(counterGroup.isActive()) {
                            airlineName = counterGroup.getAirlineName();
                            flightCodes = counterGroup.getFlightCodes();
                            peopleInLine = counterGroup.getPendingPassengers().size();
                        } else {
                            airlineName = "-";
                            flightCodes = new ArrayList<>();
                            peopleInLine = 0;
                        }
                        responseBuilder.addCounters(
                                Counter.newBuilder()
                                        .setAirlineName(airlineName)
                                        .setCounterRange(
                                                CounterRange.newBuilder()
                                                        .setCounterFrom(counterIndex)
                                                        .setCounterTo(counterIndex + counterGroup.getCounterCount() - 1)
                                                        .build()
                                        )
                                        .addAllFlightCodes(flightCodes)
                                        .setPeopleInLine(peopleInLine)
                                        .build());
                    }
                  );

            responseBuilder.setStatus(
                    StatusResponse.newBuilder()
                            .setCode(Status.OK.getCode().value())
                            .setMessage("Counters listed successfully")
                            .build()
            );
            responseObserver.onNext(responseBuilder.build());
        } catch (Exception e) {
            responseObserver.onNext(
                    ListCountersResponse.newBuilder()
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
    public void assignCounters(AssignCountersRequest request, StreamObserver<AssignCountersResponse> responseObserver) {
        try {
            // left side available, right side from or # pending
            Pair<Boolean, Integer> assignedCounters = airportRepository.assignCounters(request.getSectorName(), request.getAirlineName(), request.getFlightCodesList(), request.getCounterCount());
            if(assignedCounters.getLeft()){
                // counter was assigned
                responseObserver.onNext(
                        AssignCountersResponse.newBuilder()
                                .setStatus(StatusResponse.newBuilder()
                                        .setCode(Status.OK.getCode().value())
                                        .setMessage("Counters assigned successfully")
                                        .build()
                                )
                                .setSectorName(request.getSectorName())
                                .setAirlineName(request.getAirlineName())
                                .addAllFlightCodes(request.getFlightCodesList())
                                .setCounterCount(request.getCounterCount())
                                .setCounterFrom(assignedCounters.getRight())
                                .setCounterTo(assignedCounters.getRight() + request.getCounterCount() - 1)
                                .build()
                );
                if(eventsServant.isRegistered(request.getAirlineName()))
                    eventsServant.notify(request.getAirlineName(), String.format("%d counters (%d-%d) in Sector %s are now checking in passengers from %s %s flights",
                        request.getCounterCount(), assignedCounters.getRight(), assignedCounters.getRight() + request.getCounterCount() - 1, request.getSectorName(),request.getAirlineName(), String.join("|", request.getFlightCodesList()) ));
            }
            else{
                // counter is pending
                responseObserver.onNext(
                        AssignCountersResponse.newBuilder()
                                .setStatus(StatusResponse.newBuilder()
                                        .setCode(Status.OK.getCode().value())
                                        .setMessage("Counters are pending assignment")
                                        .build()
                                )
                                .setSectorName(request.getSectorName())
                                .setAirlineName(request.getAirlineName())
                                .addAllFlightCodes(request.getFlightCodesList())
                                .setCounterCount(request.getCounterCount())
                                .setPendingAssignments(assignedCounters.getRight())
                                .build()
                );


                eventsServant.notify(request.getAirlineName(),
                        String.format("%d counters in Sector %s for flights %s is pending with %d other pendings ahead",
                                request.getCounterCount(),
                                request.getSectorName(),
                                String.join("|",
                                request.getFlightCodesList()),
                                assignedCounters.getRight()));

            }


        } catch (Exception e) {
            responseObserver.onNext(
                    AssignCountersResponse.newBuilder()
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
    public void freeCounters(FreeCountersRequest request, StreamObserver<FreeCountersResponse> responseObserver) {
        try {
            CounterGroup counterGroup = airportRepository.freeCounters(request.getSectorName(), request.getAirlineName(), request.getCounterFrom(), eventsServant );

            responseObserver.onNext(
                    FreeCountersResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.OK.getCode().value())
                                    .setMessage("Counters freed successfully")
                                    .build()
                            )
                            .setSectorName(request.getSectorName())
                            .setCounterCount(counterGroup.getCounterCount())
                            .setCounterFrom(request.getCounterFrom())
                            .setCounterTo(request.getCounterFrom() + counterGroup.getCounterCount() - 1)
                            .addAllFlightCodes(counterGroup.getFlightCodes())
                            .build()
            );

            if(eventsServant.isRegistered(request.getAirlineName()))
                eventsServant.notify(request.getAirlineName(),String.format("Ended check-in for flights %s on counters (%d-%d) from Sector %s",
                        String.join("|", counterGroup.getFlightCodes()), request.getCounterFrom(), request.getCounterFrom() + counterGroup.getCounterCount() - 1, request.getSectorName()));

            List<CheckinAssignment> pendingAssignments = airportRepository.listPendingAssignments(request.getSectorName());

            IntStream.range(0, pendingAssignments.size())
                .forEach(idx -> {
                    CheckinAssignment pending = pendingAssignments.get(idx);
                    if(eventsServant.isRegistered(pending.airlineName()))
                        eventsServant.notify(pending.airlineName(), String.format("%d counters in Sector %s for flights %s is pending with %d other pendings ahead",
                                pending.counterCount(), request.getSectorName(), String.join("|", pending.flightCodes()), idx ));
                });


        } catch (Exception e) {
            responseObserver.onNext(
                    FreeCountersResponse.newBuilder()
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
    public void checkinCounters(CheckinCountersRequest request, StreamObserver<CheckinCountersResponse> responseObserver) {
        try {
            List<BookingHist> checkins = airportRepository.checkInCounters(request.getSectorName(), request.getCounterFrom(), request.getAirlineName());
            CheckinCountersResponse.Builder responseBuilder = CheckinCountersResponse.newBuilder();

            checkins.forEach(supposedBooking -> {
                        if(supposedBooking.getAirlineName() == null){
                            responseBuilder.addIdleCounters(
                                    supposedBooking.getCheckinCounter()
                            );
                        }
                        else{
                            responseBuilder.addCheckins(
                                    Checkin.newBuilder()
                                            .setBookingCode(supposedBooking.getBookingCode())
                                            .setFlightCode(supposedBooking.getFlightCode())
                                            .setCounterNumber(supposedBooking.getCheckinCounter())
                                            .build()
                            );

                            if(eventsServant.isRegistered(supposedBooking.getAirlineName())){
                                eventsServant.notify(supposedBooking.getAirlineName(), String.format("Check-in successful of %s for flight %s at counter %d in Sector %s",
                                        supposedBooking.getBookingCode(), supposedBooking.getFlightCode(), supposedBooking.getCheckinCounter(), supposedBooking.getSector()));
                            }
                        }

                    }

            );
            responseBuilder.setStatus(
                    StatusResponse.newBuilder()
                            .setCode(Status.OK.getCode().value())
                            .setMessage("Passengers checked in successfully")
                            .build()
            );
            responseObserver.onNext(responseBuilder.build());

        } catch (Exception e) {
            responseObserver.onNext(
                    CheckinCountersResponse.newBuilder()
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
        } catch (Exception e) {
            responseObserver.onNext(
                ListPendingAssignmentsResponse.newBuilder()
                        .setStatus(StatusResponse.newBuilder()
                                .setCode(Status.INVALID_ARGUMENT.getCode().value())
                                .setMessage(e.getMessage())
                                .build())
                        .build()
            );
        }
        finally {
            responseObserver.onCompleted();
        }
    }
}
