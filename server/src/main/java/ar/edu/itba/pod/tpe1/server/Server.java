package ar.edu.itba.pod.tpe1.server;

import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import ar.edu.itba.pod.tpe1.repositories.CheckinRepository;
import ar.edu.itba.pod.tpe1.servants.AdminServant;
import ar.edu.itba.pod.tpe1.servants.CounterServant;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info(" Server Starting ...");

        int port = 50051;
        AirportRepository airportRepository = new AirportRepository(
                new HashMap<>(),
                new ArrayList<>(),
                new HashMap<>()
        );
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new AdminServant(
                        airportRepository
                ))
                .addService(new CounterServant(
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
    }}
