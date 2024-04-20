package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;

import java.util.Map;

public class PassengerRepository {
    private final Map<String, Booking> allRegisteredPassengers;
    private final Map<String, Booking> expectedPassengerList;
    private final Map<String, BookingHist> checkedinPassengerList;

    public PassengerRepository(Map<String, Booking> allRegisteredPassengers, Map<String, Booking> expectedPassengerList, Map<String, BookingHist> checkedinPassengerList) {
        this.allRegisteredPassengers = allRegisteredPassengers;
        this.expectedPassengerList = expectedPassengerList;
        this.checkedinPassengerList = checkedinPassengerList;
    }

    public void addNewPassenger(Booking booking) {
        allRegisteredPassengers.put(booking.getBookingCode(), booking);
        expectedPassengerList.put(booking.getBookingCode(), booking);
    }

    public boolean passengerWasRegistered(String bookingCode) {
        return allRegisteredPassengers.containsKey(bookingCode);
    }

    public Booking getBookingData(String bookingCode) {
        return allRegisteredPassengers.get(bookingCode);
    }
}
