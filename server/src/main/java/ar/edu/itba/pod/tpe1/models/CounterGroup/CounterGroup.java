package ar.edu.itba.pod.tpe1.models.CounterGroup;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Queue;

@Setter
@Getter
public abstract class CounterGroup {
    protected int counterStart;
    protected int counterCount;
    public CounterGroup(int counterStart, int counterCount) {
        this.counterStart = counterStart;
        this.counterCount = counterCount;
    }

    public abstract boolean isActive();

    public abstract String getAirlineName();

    public abstract List<String> getFlightCodes();

    public abstract Queue<Booking> getPendingPassengers();

    public abstract List<BookingHist> checkinCounters();

    public abstract boolean containsFlightCode(String flightCode);

    public abstract void addPendingPassenger(Booking booking);

}
