package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.models.BookingHist;
import ar.edu.itba.pod.tpe1.models.Counter;

import java.util.*;

public class AirportRepository {
    private Integer currentCounterCount;
    private final List<Booking> expectedPassengerList;
    private final Map<String, SortedSet<Counter>> countersInSectorsMap;
    private final List<CheckinRepository> activeCheckinList;
    private final List<BookingHist> checkedinPassengerList;

    public AirportRepository(List<Booking> expectedPassengerList, Map<String, SortedSet<Counter>> countersInSectorsMap, List<CheckinRepository> activeCheckinList, List<BookingHist> checkedinPassengerList) {
        this.expectedPassengerList = expectedPassengerList;
        this.countersInSectorsMap = countersInSectorsMap;
        this.activeCheckinList = activeCheckinList;
        this.checkedinPassengerList = checkedinPassengerList;
        this.currentCounterCount = 0;
    }

    public synchronized void addSector(String sectorName) {
        if (countersInSectorsMap.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector name already exists");
        }

        countersInSectorsMap.put(sectorName, new TreeSet<>());
    }

    public synchronized Integer addCounters(String sectorName, int counterCount) {
        if (!countersInSectorsMap.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        } else if (counterCount <= 0) {
            throw new IllegalArgumentException("Counter count must be greater than 0");
        }

        SortedSet<Counter> currentCounters = countersInSectorsMap.get(sectorName);


        for (int i = 1; i <= counterCount; i++) {
            currentCounters.add(new Counter(currentCounterCount + i, false));
        }

        currentCounterCount += counterCount;

        return currentCounterCount - counterCount;

    }

    // TODO: solo va a funcionar con los expectedPassengers (y no los que ya pasaron) con esta impl
    public synchronized void addPassenger(Booking booking) {
        if (expectedPassengerList.stream().anyMatch(b -> booking.getBookingCode().equals(b.getBookingCode())))
            throw new IllegalArgumentException("Booking with code " + booking.getBookingCode() + " already exists");
        if (expectedPassengerList.stream().anyMatch(b -> booking.getFlightCode().equals(b.getFlightCode()) && !booking.getAirlineName().equals(b.getAirlineName()))){
            throw new IllegalArgumentException("Flight with code " + booking.getFlightCode() + " is already assigned to another airline");
        }

        expectedPassengerList.add(booking);
    }

    public void listSectors() {

    }

    public void listCounters() {

    }

    public void assignCounters() {

    }

    public void freeCounters() {

    }

    public void checkInCounters() {

    }

    public void listPendingAssignments() {

    }
}
