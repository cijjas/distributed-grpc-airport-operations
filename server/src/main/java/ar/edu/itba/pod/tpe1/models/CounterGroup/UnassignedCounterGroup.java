package ar.edu.itba.pod.tpe1.models.CounterGroup;

import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.models.BookingHist;

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

    @Override
    public List<BookingHist> checkinCounters() {
        throw new RuntimeException(UNASSIGNED);
    }

    @Override
    public boolean containsFlightCode(String flightCode) {
        return false;
    }

    @Override
    public void addPendingPassenger(Booking booking) {
        throw new RuntimeException(UNASSIGNED);
    }
}
