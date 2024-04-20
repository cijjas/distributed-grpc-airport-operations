package ar.edu.itba.pod.tpe1.models.PassengerStatus;

import ar.edu.itba.pod.grpc.CheckInStatus;
import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import lombok.Getter;

@Getter
public class PassengerStatusInfo {
    private final CheckInStatus checkInStatus;
    private final Booking booking;
    private final String sectorName;
    private final CounterGroup counterGroup;
    private final int checkinCounter;

    public PassengerStatusInfo(CheckInStatus checkInStatus, Booking booking, String sectorName, CounterGroup counterGroup) {
        this.checkInStatus = checkInStatus;
        this.booking = booking;
        this.sectorName = sectorName;
        this.counterGroup = counterGroup;
        this.checkinCounter = -1;
    }

    public PassengerStatusInfo(CheckInStatus checkInStatus, Booking booking, String sectorName, int checkinCounter) {
        this.checkInStatus = checkInStatus;
        this.booking = booking;
        this.sectorName = sectorName;
        this.counterGroup = null;
        this.checkinCounter = checkinCounter;
    }
}
