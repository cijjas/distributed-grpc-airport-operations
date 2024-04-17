package ar.edu.itba.pod.tpe1.client.counter.actions;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientArguments;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ListSectorsAction implements Action {
    ManagedChannel channel;
    CounterClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(CounterClient.class);
    private static final String HEADER = "Sectors\t Counters";

    public ListSectorsAction(ManagedChannel channel,CounterClientArguments arguments) {
        this.channel = channel;
        this.arguments = arguments;
    }


    @Override
    public void execute() {
        try {
            listSectors(channel);
        } catch (Exception e) {
            logger.error("Failed to list sectors", e);
        }
    }

    private void listSectors(ManagedChannel channel){
        CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);
        Optional<ListSectorsResponse> response = Optional.ofNullable(stub.listSectors(Empty.newBuilder().build()));
        response.ifPresentOrElse(
                this::handleResponse,
                () -> System.out.println("Failed to list sectors")
        );

    }

    private void handleResponse(ListSectorsResponse response) {
        if (response.getStatus().getCode() == Status.OK.getCode().value()) {
            printSectors(response);
        } else {
            logger.error("Error listing sectors: {}", response.getStatus().getMessage());
        }
    }

    private void printSectors(ListSectorsResponse response) {
        System.out.println(HEADER);
        response.getSectorsList().forEach(this::printSector);
    }


    private void printSector(Sector sector) {
        String sectorName = sector.getSectorName();
        List<String> counterRanges = formatCounterRanges(sector);
        System.out.printf("%s\t %s%n", sectorName, String.join(" ", counterRanges));
    }

    private List<String> formatCounterRanges(Sector sector) {
        return sector.getCounterRangesList().stream()
                .map(range -> String.format("(%d-%d)", range.getCounterFrom(), range.getCounterTo()))
                .toList();
    }

}
