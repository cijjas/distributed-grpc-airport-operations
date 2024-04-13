package ar.edu.itba.pod.tpe1.servants;


import ar.edu.itba.pod.grpc.AddCountersRequest;
import ar.edu.itba.pod.grpc.AddExpectedPassengersRequest;
import ar.edu.itba.pod.grpc.AirportAdminServiceGrpc;
import ar.edu.itba.pod.grpc.StatusResponse;
import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class AirportServant extends AirportAdminServiceGrpc.AirportAdminServiceImplBase {

    private final AirportRepository airportRepository;

    public AirportServant(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Override
    public void addSector(StringValue request, StreamObserver<StatusResponse> responseObserver) {
        try {
            airportRepository.addSector(request.getValue());
            responseObserver.onNext(StatusResponse.newBuilder().setCode(Status.OK.getCode().value()).setMessage("Sector " + request.getValue() + " added successfully").build());
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(StatusResponse.newBuilder().setCode(Status.INVALID_ARGUMENT.getCode().value()).setMessage(e.getMessage()).build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void addCounters(AddCountersRequest request, StreamObserver<StatusResponse> responseObserver) {
        int counterCount = request.getCounterCount();
        try {
            Integer counterId = airportRepository.addCounters(request.getSectorName(), counterCount);
            responseObserver.onNext(StatusResponse.newBuilder().setCode(Status.OK.getCode().value()).setMessage(counterId.toString() + "-" + (counterId+counterCount)).build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e){
            responseObserver.onNext(StatusResponse.newBuilder().setCode(Status.INVALID_ARGUMENT.getCode().value()).setMessage(e.getMessage()).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<AddExpectedPassengersRequest> addExpectedPassengers(StreamObserver<StatusResponse> responseObserver) {
        return new StreamObserver<AddExpectedPassengersRequest>() {
            @Override
            public void onNext(AddExpectedPassengersRequest addExpectedPassengersRequest) {
                try {
                    airportRepository.addPassenger(
                        new Booking(
                            addExpectedPassengersRequest.getPassenger().getAirlineName(),
                            addExpectedPassengersRequest.getPassenger().getFlightCode(),
                            addExpectedPassengersRequest.getPassenger().getBookingCode()
                        )
                    );
                } catch (IllegalArgumentException e) {
                    responseObserver.onNext(StatusResponse.newBuilder().setCode(Status.INVALID_ARGUMENT.getCode().value()).setMessage(e.getMessage()).build());
                    responseObserver.onCompleted();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onNext(StatusResponse.newBuilder().setCode(Status.INTERNAL.getCode().value()).setMessage(throwable.getMessage()).build());
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(StatusResponse.newBuilder().setCode(Status.OK.getCode().value()).build());
                responseObserver.onCompleted();
            }
        };
    }
}
