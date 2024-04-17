package ar.edu.itba.pod.tpe1.client.counter.actions;

import ar.edu.itba.pod.grpc.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.ListPendingAssignmentsResponse;
import ar.edu.itba.pod.grpc.PendingAssignment;
import ar.edu.itba.pod.tpe1.client.Action;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientArguments;
import ar.edu.itba.pod.tpe1.client.counter.CounterClient;
import ar.edu.itba.pod.tpe1.client.counter.CounterClientArguments;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ListPendingAssignmentsAction implements Action {
    ManagedChannel channel;
    CounterClientArguments arguments;
    private static final Logger logger = LoggerFactory.getLogger(CounterClient.class);
    private static final String HEADER = "Counters\t Airline\t Flights\n";

    public ListPendingAssignmentsAction(ManagedChannel channel, CounterClientArguments arguments) {
        this.arguments = arguments;
        this.channel = channel;
    }

    @Override
    public void execute() {
        try {
            listPendingAssignments(channel, arguments.getSector());
        } catch (Exception e) {
            logger.error("Failed to list pending assignments", e);
        }
    }

    private void listPendingAssignments(ManagedChannel channel, String sector) {
        CounterServiceGrpc.CounterServiceBlockingStub stub = CounterServiceGrpc.newBlockingStub(channel);
        Optional<ListPendingAssignmentsResponse> response = Optional.ofNullable(
                stub.listPendingAssignments(StringValue.of(sector)));

        response.ifPresentOrElse(
            this::handleResponse,
            () -> System.out.println("Failed to list pending assignments")
        );
    }

    private void handleResponse(ListPendingAssignmentsResponse listPendingAssignmentsResponse) {
        if (listPendingAssignmentsResponse.getStatus().getCode() == Status.OK.getCode().value()) {
            System.out.println(HEADER);
            listPendingAssignmentsResponse.getPendingAssignmentsList().forEach(this::printAssignment);
        } else {
            logger.error("Error listing pending assignments: {}", listPendingAssignmentsResponse.getStatus().getMessage());
        }
    }

    private void printAssignment(PendingAssignment pendingAssignment) {
        System.out.printf("%d\t %s\t %s\n",
                pendingAssignment.getCounterCount(),
                pendingAssignment.getAirlineName(),
                String.join("|", pendingAssignment.getFlightCodesList()));
    }
}
