package ar.edu.itba.pod.tpe1.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ChannelBuilder {
    public static ManagedChannel buildChannel(String target) {
        return ManagedChannelBuilder
                .forTarget(target)
                .usePlaintext()
                .build();
    }
}