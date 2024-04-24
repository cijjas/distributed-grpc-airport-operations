package ar.edu.itba.pod.tpe1.server;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import ar.edu.itba.pod.tpe1.servants.AdminServant;
import ar.edu.itba.pod.tpe1.servants.CounterServant;
import ar.edu.itba.pod.tpe1.servants.QueryServant;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info(" Server Starting ...");

        int port = 50051;
        AirportRepository airportRepository = new AirportRepository(
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>()
        );
        addDataToAirport(airportRepository);
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new AdminServant(
                        airportRepository
                ))
                .addService(new CounterServant(
                        airportRepository
                ))
                .addService(new QueryServant(
                        airportRepository
                ))
                .build();
        server.start();
        logger.info("Server started, listening on " + port);
        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }

    public static void addDataToAirport(AirportRepository airportRepository) {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addSector(SECTOR_B);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addPassenger(PASSENGER_B);

//        airportRepository.addCounters(SECTOR_A, 10);
//        airportRepository.addCounters(SECTOR_B, 10);
//        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 4);
//        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);
//        airportRepository.checkInCounters(SECTOR_A, 1, PASSENGER_A.getAirlineName());
    }

    // TODO: borrar esto para entregar!!!!
    public static final String SECTOR_A = "A";
    public static final String SECTOR_B = "B";

    public static final String AIRLINE_A = "Airline A";
    public static final String AIRLINE_B = "Airline B";
    public static final String FLIGHT_CODE_1 = "ABC123";
    public static final String FLIGHT_CODE_2 = "CDE123";
    public static final String BOOKING_CODE_1 = "123123";
    public static final String BOOKING_CODE_2 = "234234";

    public static final Booking PASSENGER_A = new Booking(AIRLINE_A, FLIGHT_CODE_1, BOOKING_CODE_1);
    public static final Booking PASSENGER_B_SAME_FLIGHT = new Booking(AIRLINE_B, FLIGHT_CODE_1, BOOKING_CODE_2);
    public static final Booking PASSENGER_C_SAME_BOOKING = new Booking(AIRLINE_A, FLIGHT_CODE_2, BOOKING_CODE_1);
    public static final Booking PASSENGER_B = new Booking(AIRLINE_B, FLIGHT_CODE_2, BOOKING_CODE_2);

}
