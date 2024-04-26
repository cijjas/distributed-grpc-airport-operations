package ar.edu.itba.pod.tpe1.client.counter.actions;

import ar.edu.itba.pod.grpc.CounterServiceGrpc;
import ar.edu.itba.pod.grpc.ListPendingAssignmentsResponse;
import ar.edu.itba.pod.grpc.PendingAssignment;
import ar.edu.itba.pod.tpe1.client.Action;
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
    private static final String HEADER = String.format("%-10s %-25s %-25s\n", "Counters", "Airline", "Flights");
    private static final String HASHTAG_DIVIDER= "#".repeat(55);

    public ListPendingAssignmentsAction(ManagedChannel channel, CounterClientArguments arguments) {
        this.arguments = arguments;
        this.channel = channel;
    }

    @Override
    public void execute() {
       if (arguments.getSector().isPresent()) {
           try {
               listPendingAssignments(channel, arguments.getSector().get());
           } catch (Exception e) {
               handleListCountersError(e);
           }
       } else {
           printListCountersUsageInstructions();
       }
    }


    private void handleListCountersError(Exception e) {
        System.out.println("Failed to list counters due to an error: " + e.getMessage());
        printListCountersUsageInstructions();
    }

    private void printListCountersUsageInstructions() {
        System.out.println("Invalid or missing parameters for listing counters.");
        System.out.println("Required parameters: -Dsector=<sectorName> -DcounterFrom=<startingCounterNumber> -DcounterTo=<endingCounterNumber>");
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
            System.out.println(HASHTAG_DIVIDER);
            listPendingAssignmentsResponse.getPendingAssignmentsList().forEach(this::printAssignment);
        } else {
            System.out.println(listPendingAssignmentsResponse.getStatus().getMessage());
        }
    }

    private void printAssignment(PendingAssignment pendingAssignment) {
        System.out.printf("%-10d %-25s %-25s \n",
                pendingAssignment.getCounterCount(),
                pendingAssignment.getAirlineName(),
                String.join("|", pendingAssignment.getFlightCodesList()));
    }
}
