package ar.edu.itba.pod.tpe1.models.CounterGroup;

import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.models.BookingHist;
import lombok.Getter;

import java.util.ArrayList;
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
