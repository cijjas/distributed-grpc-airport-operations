package ar.edu.itba.pod.tpe1.models.CounterGroup;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import lombok.Getter;

import java.util.*;

@Getter
public class AssignedCounterGroup extends CounterGroup{
    private final String airlineName;
    private final List<String> flightCodes;
    private final Queue<Booking> pendingPassengers;

    public AssignedCounterGroup(CheckinAssignment checkinAssignment, int counterStart) {
        super(counterStart, checkinAssignment.counterCount());
        this.airlineName = checkinAssignment.airlineName();
        this.flightCodes = checkinAssignment.flightCodes();

        this.pendingPassengers = new LinkedList<>();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public List<BookingHist> checkinCounters(){
        List<BookingHist> toRet = new ArrayList<>();

        Booking curr = pendingPassengers.poll();
        for(int i = 0 ; i < counterCount ; i++, curr = pendingPassengers.poll()){
            if(curr != null)
                toRet.add(new BookingHist(curr, i));
            else
                //Ojo con esto
                toRet.add(BookingHist.empty(i));
        }
        return toRet;
    }

    @Override
    public boolean containsFlightCode(String flightCode){
        return flightCodes.contains(flightCode);
    }

    public void addPendingPassenger(Booking booking){
        pendingPassengers.add(booking);
    }
}
