package ar.edu.itba.pod.tpe1.servants;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Sector;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(QueryServant.class);

    private final AirportRepository airportRepository;

    public QueryServant(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Override
    public void counters(CountersRequest request, StreamObserver<CountersResponse> responseObserver) {
        try {
            SortedMap<String, Sector> counters = airportRepository.counters(request.getSectorName());

            List<CountersResponse.CounterGroup> counterGroupsList = new ArrayList<>();
            for (Sector sector : counters.values()) {
                for (CounterGroup counterGroup : sector.getCounterGroupMap().values()) {
                    counterGroupsList.add(CountersResponse.CounterGroup.newBuilder()
                            .setSectorName(sector.getName())
                            .setCounterFrom(counterGroup.getCounterStart())
                            .setCounterTo(counterGroup.getCounterStart() + counterGroup.getCounterCount() - 1)
                            .setAirlineName(counterGroup.getAirlineName())
                            .addAllFlightCodes(counterGroup.getFlightCodes())
                            .setPeopleInLine(counterGroup.getPendingPassengers().size())
                            .build());
                }
            }

            responseObserver.onNext(
                    CountersResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.OK.getCode().value())
                                    .setMessage("Successfully fetched counters for sector")
                                    .build())
                            .addAllCounters(counterGroupsList)
                            .build()
            );
        } catch (IllegalStateException e) {
            responseObserver.onNext(
                    CountersResponse.newBuilder()
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
    public void checkins(CheckinsRequest request, StreamObserver<CheckinsResponse> responseObserver) {
        try {
            List<CheckinsResponse.Checkin> checkins = airportRepository.checkins(request.getSectorName(), request.getAirlineName()).stream().map(bh ->
                    CheckinsResponse.Checkin.newBuilder().setSectorName(bh.getSector())
                            .setCounterNumber(bh.getCheckinCounter())
                            .setAirlineName(bh.getAirlineName())
                            .setFlightCode(bh.getFlightCode())
                            .setBookingCode(bh.getBookingCode())
                            .build()).collect(Collectors.toList());

            responseObserver.onNext(
                    CheckinsResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.OK.getCode().value())
                                    .setMessage("Successfully fetched booking hist")
                                    .build())
                            .addAllCheckins(checkins)
                    .build()
            );
        } catch (IllegalStateException e) {
            responseObserver.onNext(
                    CheckinsResponse.newBuilder()
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
}
