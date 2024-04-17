package ar.edu.itba.pod.tpe1.models.CounterGroup;

import ar.edu.itba.pod.tpe1.models.Booking;

import java.util.List;
import java.util.Queue;

public class UnassignedCounterGroup extends CounterGroup {

    private static final String UNASSIGNED = "Counter is inactive";

    public UnassignedCounterGroup(int counterCount) {
        super(counterCount);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public String getAirlineName() {
        throw new RuntimeException(UNASSIGNED);
    }

    @Override
    public List<String> getFlightCodes() {
        throw new RuntimeException(UNASSIGNED);
    }

    @Override
    public Queue<Booking> getPendingPassengers() {
        throw new RuntimeException(UNASSIGNED);
    }
}
