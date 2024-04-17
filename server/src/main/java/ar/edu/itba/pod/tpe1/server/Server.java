package ar.edu.itba.pod.tpe1.server;

import ar.edu.itba.pod.tpe1.models.Counter;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import ar.edu.itba.pod.tpe1.servants.AirportServant;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info(" Server Starting ...");

        int port = 50051;
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new AirportServant(new AirportRepository(
                        new HashMap<>(),
                        new ArrayList<>(),
                        new HashMap<>()
                )))
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
