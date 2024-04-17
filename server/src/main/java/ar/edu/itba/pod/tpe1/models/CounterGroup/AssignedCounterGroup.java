package ar.edu.itba.pod.tpe1.models.CounterGroup;

import ar.edu.itba.pod.tpe1.models.Booking;
import lombok.Getter;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

@Getter
public class AssignedCounterGroup extends CounterGroup{
    private final String airlineName;
    private final List<String> flightCodes;
    private final Queue<Booking> pendingPassengers;

    public AssignedCounterGroup(CheckinAssignment checkinAssignment) {
        super(checkinAssignment.counterCount());
        this.airlineName = checkinAssignment.airlineName();
        this.flightCodes = checkinAssignment.flightCodes();

        this.pendingPassengers = new PriorityQueue<>();
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
