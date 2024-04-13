package ar.edu.itba.pod.tpe1.models;

public class BookingHist extends Booking {
    private final int checkinCounter;

    public BookingHist(String airlineName, String flightCode, String bookingCode, int checkinCounter) {
        super(airlineName, flightCode, bookingCode);
        this.checkinCounter = checkinCounter;
    }

    public int getCheckinCounter() {
        return checkinCounter;
    }
}
