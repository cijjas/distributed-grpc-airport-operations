package ar.edu.itba.pod.tpe1.servants;


import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(AdminServant.class);
    private final AirportRepository airportRepository;

    public AdminServant(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Override
    public void addSector(StringValue request, StreamObserver<AddSectorResponse> responseObserver) {
        try {
            airportRepository.addSector(request.getValue());
            responseObserver.onNext(
                    AddSectorResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.OK.getCode().value())
                                    .setMessage("Sector added successfully")
                                    .build())
                            .setSectorName(request.getValue())
                            .build()
            );
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(
                    AddSectorResponse.newBuilder()
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
    public void addCounters(AddCountersRequest request, StreamObserver<AddCountersResponse> responseObserver) {
        try {
            int counterCount = request.getCounterCount();
            Integer counterId = airportRepository.addCounters(request.getSectorName(), counterCount);
            responseObserver.onNext(
                    AddCountersResponse.newBuilder()
                            .setStatus(StatusResponse.newBuilder()
                                    .setCode(Status.OK.getCode().value())
                                    .setMessage(counterCount + " new counters (" + counterId + "-" + (counterId + counterCount - 1) + ") in Sector " + request.getSectorName() + " added successfully")
                                    .build())
                            .setSectorName(request.getSectorName())
                            .setCounterCount(counterCount)
                            .setFirstCounterNumber(counterId)
                            .setLastCounterNumber(counterId + counterCount - 1)
                            .build()
            );
        } catch (Exception e){
            responseObserver.onNext(
                    AddCountersResponse.newBuilder()
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
    public StreamObserver<ManifestRequest> manifest(StreamObserver<ManifestResponse> responseObserver) {
        return new StreamObserver<ManifestRequest>() {
            @Override
            public void onNext(final ManifestRequest request) {
                Booking booking = new Booking(
                        request.getPassenger().getAirlineName(),
                        request.getPassenger().getFlightCode(),
                        request.getPassenger().getBookingCode()
                );
                try {
                    airportRepository.addPassenger(booking);
                    responseObserver.onNext(
                            ManifestResponse.newBuilder()
                                    .setStatus(StatusResponse.newBuilder()
                                            .setCode(Status.OK.getCode().value())
                                            .setMessage("-Booking " + booking.getBookingCode() + " for " + booking.getAirlineName() + " " + booking.getFlightCode() + " added successfully")
                                            .build())
                                    .setPassenger(request.getPassenger())
                                    .build()
                    );
                } catch (Exception e) {
                    responseObserver.onNext(
                            ManifestResponse.newBuilder()
                                    .setStatus(StatusResponse.newBuilder()
                                            .setCode(Status.INVALID_ARGUMENT.getCode().value())
                                            .setMessage(e.getMessage())
                                            .build())
                                    .build()
                    );

                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

}
