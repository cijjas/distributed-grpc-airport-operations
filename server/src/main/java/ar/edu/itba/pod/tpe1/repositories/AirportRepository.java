package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.*;
import ar.edu.itba.pod.tpe1.models.CounterGroup.AssignedCounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.UnassignedCounterGroup;

import java.util.*;

public class AirportRepository {
    private final List<Booking> expectedPassengerList;
    private final List<BookingHist> checkedinPassengerList;

    private final SortedMap<String, Sector> sectors;
    private int nextAvailableCounter;

    public AirportRepository(List<Booking> expectedPassengerList, List<BookingHist> checkedinPassengerList) {
        this.expectedPassengerList = expectedPassengerList;
        this.checkedinPassengerList = checkedinPassengerList;
        this.sectors = new TreeMap<>();
        nextAvailableCounter = 1;
    }

    public synchronized void addSector(String sectorName) {
        if (sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector name already exists");
        }

        sectors.put(sectorName, new Sector());
    }

    public synchronized int addCounters(String sectorName, int counterCount) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        if (counterCount <= 0) {
            throw new IllegalArgumentException("Counter count must be greater than 0");
        }

        Sector sector = sectors.get(sectorName);
        sector.addCounterGroup(nextAvailableCounter, new UnassignedCounterGroup(counterCount));

        nextAvailableCounter += counterCount;
        return nextAvailableCounter;
    }



    // TODO: solo va a funcionar con los expectedPassengers (y no los que ya pasaron) con esta impl
    public synchronized void addPassenger(Booking booking) {
        if (expectedPassengerList.stream().anyMatch(b -> booking.getBookingCode().equals(b.getBookingCode())))
            throw new IllegalArgumentException("Booking with code " + booking.getBookingCode() + " already exists");
        if (expectedPassengerList.stream().anyMatch(b -> booking.getFlightCode().equals(b.getFlightCode())
                                                        && !booking.getAirlineName().equals(b.getAirlineName())))
            throw new IllegalArgumentException("Flight with code" + booking.getFlightCode() + " is already assigned to another airline");

        expectedPassengerList.add(booking);
    }

    public SortedMap<String, SortedMap<Integer, Integer>> listSectors() {
        SortedMap<String, SortedMap<Integer, Integer>> mappedSectors = new TreeMap<>();

        if (sectors.isEmpty()) {
            throw new IllegalStateException("No sectors registered");
        }

        for (Map.Entry<String, Sector> entry : sectors.entrySet()) {
            mappedSectors.put(entry.getKey(), entry.getValue().listGroupedCounters());
        }

        return mappedSectors;
    }

    // TODO: fromVal and toVal (teniendo 2-5, si arranca en 2 y me piden del 3, lo muestro?)
    public SortedMap<Integer, CounterGroup> listCounters(String sectorName) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        return sectors.get(sectorName).getCounterGroupMap();
    }

    public void assignCounters(String sectorName, String airlineName, List<String> flightCodes, int counterCount) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        CheckinAssignment checkinAssignment = new CheckinAssignment(airlineName, flightCodes, counterCount);

        sectors.get(sectorName).assignCounterGroup(checkinAssignment);
    }


    public void freeCounters(String sectorName, String airlineName, int counterFrom) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        sectors.get(sectorName).freeCounters(airlineName, counterFrom);
    }
//
//    public void checkInCounters() {
//
//    }
//
//    public void listPendingAssignments() {
//
//    }
}
