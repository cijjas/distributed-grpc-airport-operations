package ar.edu.itba.pod.tpe1.models.CounterGroup;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;

import java.util.List;
import java.util.Queue;

public class UnassignedCounterGroup extends CounterGroup {

    private static final String UNASSIGNED = "Counter is inactive";

    public UnassignedCounterGroup(int counterStart, int counterCount) {
        super(counterStart, counterCount);
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
