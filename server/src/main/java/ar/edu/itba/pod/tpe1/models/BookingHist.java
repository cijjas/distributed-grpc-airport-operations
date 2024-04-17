package ar.edu.itba.pod.tpe1.models;

public class BookingHist extends Booking {
    private int checkinCounter;

    public BookingHist(String airlineName, String flightCode, String bookingCode, int checkinCounter) {
        super(airlineName, flightCode, bookingCode);
        this.checkinCounter = checkinCounter;
    }

    public BookingHist(Booking booking, int checkinCounter){
        super(booking.getAirlineName(), booking.getFlightCode(), booking.getBookingCode());
        this.checkinCounter = checkinCounter;
    }

    public static BookingHist empty(int checkinCounter){
        return new BookingHist(null, null, null, checkinCounter);
    }

    public void setCheckinCounter(int checkinCounter) {
        this.checkinCounter = checkinCounter;
    }

    public int getCheckinCounter() {
        return checkinCounter;
    }
}
