package ar.edu.itba.pod.tpe1.servants;


import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.jar.Manifest;

public class AirportServant extends AdminServiceGrpc.AdminServiceImplBase {

    private final AirportRepository airportRepository;

    public AirportServant(AirportRepository airportRepository) {
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
        int counterCount = request.getCounterCount();
        try {
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
        } catch (IllegalArgumentException e){
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
        return new StreamObserver<>() {
            @Override
            public void onNext(ManifestRequest request) {
                try {
                    BookingData passenger = request.getPassenger();
                    airportRepository.addPassenger(
                            new Booking(
                                    passenger.getAirlineName(),
                                    passenger.getFlightCode(),
                                    passenger.getBookingCode()
                            )
                    );
                    responseObserver.onNext(
                            ManifestResponse.newBuilder()
                                    .setStatus(StatusResponse.newBuilder()
                                            .setCode(Status.OK.getCode().value())
                                            .setMessage("Passenger processed")
                                            .build())
                                    .setPassenger(
                                            BookingData.newBuilder()
                                                    .setBookingCode(passenger.getBookingCode())
                                                    .setFlightCode(passenger.getFlightCode())
                                                    .setAirlineName(passenger.getAirlineName())
                                                    .build()
                                    )
                                    .build()
                    );
                } catch (IllegalArgumentException e) {
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

            /*
            * TODO: Implement onError method
            *  - Ya se agregó un pasajero con ese código de reserva
            *  - Ya se agregó un vuelo con ese código pero con otra aerolínea
            *  y poner el mensaje correcto en el StatusResponse
            * */
            @Override
            public void onError(Throwable throwable) {
                responseObserver.onNext(
                        ManifestResponse.newBuilder()
                                .setStatus(StatusResponse.newBuilder()
                                        .setCode(Status.INTERNAL.getCode().value())
                                        .setMessage(throwable.getMessage())
                                        .build())
                                .build()
                );
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        ManifestResponse.newBuilder()
                                .setStatus(StatusResponse.newBuilder()
                                        .setCode(Status.OK.getCode().value())
                                        .setMessage("Manifest processed")
                                        .build())
                                .build()
                );
                responseObserver.onCompleted();
            }
        };
    }

}
