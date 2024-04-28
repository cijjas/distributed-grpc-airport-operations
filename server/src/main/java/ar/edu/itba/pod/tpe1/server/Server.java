package ar.edu.itba.pod.tpe1.server;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import ar.edu.itba.pod.tpe1.servants.*;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args){
        logger.info(" Server Starting ...");

        int port = parsePort(args);
        AirportRepository airportRepository = initializeAirportRepository();

        EventsServant eventsServant = new EventsServant(airportRepository, new ConcurrentHashMap<>());
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new AdminServant(airportRepository, eventsServant))
                .addService(new CounterServant(airportRepository, eventsServant))
                .addService(new QueryServant(airportRepository))
                .addService(new PassengerServant(airportRepository, eventsServant))
                .addService(eventsServant)
                .build();

        try{
            startServer(server, port);
        }
        catch (Exception e){
            logger.error("Error starting server", e);
        }
    }

    private static void startServer(io.grpc.Server server, int port) throws IOException, InterruptedException {
        server.start();
        logger.info("Server started, listening on {}", port);
        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }


    private static AirportRepository initializeAirportRepository() {
        return new AirportRepository(
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>()
        );
    }

    private static int parsePort(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("-Dport=")) {
                try {
                    return Integer.parseInt(arg.substring(7));
                } catch (NumberFormatException e) {
                    logger.error("Invalid port number provided. Please use the format -Dport=<portNumber>");
                    System.exit(1);
                }
            }
        }
        logger.info("No port number provided. Defaulting to port 50051.");
        return 50051;
    }


}
