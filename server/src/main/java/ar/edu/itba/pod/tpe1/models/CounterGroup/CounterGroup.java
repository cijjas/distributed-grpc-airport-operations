package ar.edu.itba.pod.tpe1.models.CounterGroup;

import ar.edu.itba.pod.tpe1.models.Booking;
import lombok.Getter;

import java.util.List;
import java.util.Queue;

@Getter
public abstract class CounterGroup {
    protected int counterCount;

    public CounterGroup(int counterCount) {
        this.counterCount = counterCount;
    }

    public abstract boolean isActive();

    public void setCounterCount(int counterCount) {
        this.counterCount = counterCount;
    }

    public abstract String getAirlineName();

    public abstract List<String> getFlightCodes();

    public abstract Queue<Booking> getPendingPassengers();
}
