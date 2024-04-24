package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PassengerRepository {
    private final ConcurrentMap<String, Booking> allRegisteredPassengers;
    private final ConcurrentMap<String, Booking> expectedPassengerList;
    private final ConcurrentMap<String, BookingHist> checkedinPassengerList;

    public PassengerRepository(ConcurrentMap<String, Booking> allRegisteredPassengers, ConcurrentMap<String, Booking> expectedPassengerList, ConcurrentMap<String, BookingHist> checkedinPassengerList) {
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

    public boolean passengerIsExpected(String bookingCode) {
        return expectedPassengerList.containsKey(bookingCode);
    }

    public Booking getBookingData(String bookingCode) {
        return allRegisteredPassengers.get(bookingCode);
    }

    public Booking getExpectedPassenger(String bookingCode) {
        return expectedPassengerList.get(bookingCode);
    }

    public void removeExpectedPassenger(String bookingCode) {
        expectedPassengerList.remove(bookingCode);
    }

    public boolean passengersExpectedForAirline(String airlineName) {
        return expectedPassengerList.values().stream().anyMatch(b -> b.getAirlineName().equals(airlineName));
    }

    public void checkInPassengers(String sectorName, List<BookingHist> bookingHists) {
        checkedinPassengerList.putAll(bookingHists.stream()
                .filter(b -> b.getAirlineName() != null)
                .peek(bh -> bh.setSector(sectorName))
                .collect(Collectors.toMap(BookingHist::getBookingCode, Function.identity())));
    }

    public BookingHist findCheckedinPassenger(String bookingCode) {
        return checkedinPassengerList.get(bookingCode);
    }

    public List<BookingHist> checkins(String sectorName, String airlineName) {
        if (checkedinPassengerList.isEmpty())
            throw new IllegalStateException("No checkins registered");

        Stream<BookingHist> stream = checkedinPassengerList.values().stream();

        if (sectorName != null && !sectorName.isEmpty())
            stream = stream.filter(bookingHist -> bookingHist.getSector().equals(sectorName));

        if (airlineName != null && !airlineName.isEmpty())
            stream = stream.filter(bookingHist -> bookingHist.getAirlineName().equals(airlineName));

        return stream.collect(Collectors.toList());
    }
}
