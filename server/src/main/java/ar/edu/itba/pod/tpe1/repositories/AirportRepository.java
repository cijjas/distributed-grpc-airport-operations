package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.*;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.UnassignedCounterGroup;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AirportRepository {
    private final Map<String, Booking> allRegisteredPassengers;
    private final Map<String, Booking> expectedPassengerList;
    private final Map<String, BookingHist> checkedinPassengerList;
    private final AirlineRepository airlineRepository;
    private final SortedMap<String, Sector> sectors;

    private int nextAvailableCounter;

    public AirportRepository(Map<String, Booking> allRegisteredPassengers, Map<String, Booking> expectedPassengerList, Map<String, BookingHist> checkedinPassengerList, Map<String, List<Flight>> airlineFlightCodes) {
        this.allRegisteredPassengers = allRegisteredPassengers;
        this.expectedPassengerList = expectedPassengerList;
        this.checkedinPassengerList = checkedinPassengerList;
        this.airlineRepository = new AirlineRepository(airlineFlightCodes);
        this.sectors = new TreeMap<>();
        nextAvailableCounter = 1;
    }

    public synchronized void addSector(String sectorName) {
        if (sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector name already exists");
        }

        sectors.put(sectorName, new Sector(sectorName, airlineRepository));
    }

    public synchronized Integer addCounters(String sectorName, int counterCount) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        if (counterCount <= 0) {
            throw new IllegalArgumentException("Counter count must be greater than 0");
        }

        Sector sector = sectors.get(sectorName);
        sector.addCounterGroup(nextAvailableCounter, new UnassignedCounterGroup(nextAvailableCounter, counterCount));

        nextAvailableCounter += counterCount;
        return nextAvailableCounter - counterCount;
    }


    public synchronized void addPassenger(Booking booking) {
        if (allRegisteredPassengers.containsKey(booking.getBookingCode()))
            throw new IllegalArgumentException("Booking with code " + booking.getBookingCode() + " already exists");

        if (airlineRepository.flightCodeAlreadyExistsForOtherAirlines(booking.getAirlineName(), List.of(booking.getFlightCode())))
            throw new IllegalArgumentException("Flight with code " + booking.getFlightCode() + " is already assigned to another airline");

        airlineRepository.addAirlineIfNotExists(booking.getAirlineName());
        airlineRepository.addFlightToAirline(booking.getAirlineName(), booking.getFlightCode());

        allRegisteredPassengers.put(booking.getBookingCode(), booking);
        expectedPassengerList.put(booking.getBookingCode(), booking);
    }

    public SortedMap<String, SortedMap<Integer, Integer>> listSectors() {
        SortedMap<String, SortedMap<Integer, Integer>> mappedSectors = new TreeMap<>();
        System.out.println(sectors);
        if (sectors.isEmpty()) {
            throw new IllegalStateException("No sectors registered");
        }

        for (Map.Entry<String, Sector> entry : sectors.entrySet()) {
            mappedSectors.put(entry.getKey(), entry.getValue().listGroupedCounters());
        }

        return mappedSectors;
    }

    public SortedMap<Integer, CounterGroup> listCounters(String sectorName) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        return sectors.get(sectorName).getCounterGroupMap();
    }

    public SortedMap<Integer, CounterGroup> listCounters(String sectorName, int fromVal, int toVal) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        if (toVal - fromVal < 0) {
            throw new IllegalArgumentException("Requested range is not valid");
        }

        return sectors.get(sectorName).listCounters(fromVal, toVal);
    }

    public Pair<Boolean, Integer> assignCounters(String sectorName, String airlineName, List<String> flightCodes, int counterCount) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        if (!airlineRepository.allFlightCodesRegistered(flightCodes))
            throw new IllegalArgumentException("At least one of the flight codes have not been registered");

        if (airlineRepository.flightCodeAlreadyExistsForOtherAirlines(airlineName, flightCodes))
            throw new IllegalArgumentException("A requested flight code is assigned to another airline");

        if (!airlineRepository.allFlightCodesAreNew(airlineName, flightCodes))
            throw new IllegalStateException("At least one flight code has been assigned, is pending or has ended");

        CheckinAssignment checkinAssignment = new CheckinAssignment(airlineName, flightCodes, counterCount);

        Pair<Boolean, Integer> toRet = sectors.get(sectorName).assignCounterGroup(checkinAssignment);
        airlineRepository.markFlightsAsAssigned(airlineName, flightCodes);

        return toRet;
    }

    public CounterGroup freeCounters(String sectorName, String airlineName, int counterFrom) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        return sectors.get(sectorName).freeCounters(airlineName, counterFrom);
    }

    public List<BookingHist> checkInCounters(String sectorName, int counterFrom, String airlineName) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }
        List<BookingHist> toRet = sectors.get(sectorName).checkinCounters(counterFrom, airlineName);
        checkedinPassengerList.putAll(toRet.stream()
                .filter(b -> b.getAirlineName() != null)
                .peek(bh -> bh.setSector(sectorName))
                .collect(Collectors.toMap(BookingHist::getBookingCode, Function.identity())));
        return toRet;
    }


    public List<CheckinAssignment> listPendingAssignments(String sectorName) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        return sectors.get(sectorName).listPendingAssignments();
    }

    //WARNING: Asquerosamente funcional.
    public CounterGroup fetchCounter(String bookingCode) {
        if (!expectedPassengerList.containsKey(bookingCode))
            throw new IllegalArgumentException("Booking code not found");

        Booking booking = expectedPassengerList.get(bookingCode);
        CounterGroup curr = null;
        for (Sector sector : sectors.values()) {
            curr = sector.fetchCounter(booking.getFlightCode());
            if (curr != null)
                return curr;
        }
        return null;
    }

    public CounterGroup passengerCheckin(String bookingCode, String sectorName, int counterFrom) {
        if (!expectedPassengerList.containsKey(bookingCode))
            throw new IllegalArgumentException("Booking code not found or user checked-in");

        if (!sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector not found");

        Booking booking = expectedPassengerList.get(bookingCode);
        CounterGroup toRet = sectors.get(sectorName).passengerCheckin(booking, counterFrom);
        expectedPassengerList.remove(bookingCode);
        return toRet;
    }

    public Triple<Booking, CounterGroup, Integer> passengerStatus(String bookingCode) {
        if (!allRegisteredPassengers.containsKey(bookingCode))
            throw new IllegalArgumentException("No expected passenger with requested booking code");

        Booking booking = checkedinPassengerList.get(bookingCode);
        if (booking != null) {
            return new Triple<>(booking, null, checkedinPassengerList.get(bookingCode).getCheckinCounter());
        }

        booking = allRegisteredPassengers.get(bookingCode);
        Pair<String, Integer> sectorAndCounter = airlineRepository.getSectorAndCounterFromAirlineAndFlight(booking.getAirlineName(), booking.getFlightCode());

        if (sectorAndCounter == null || sectorAndCounter.getLeft() == null)
            throw new IllegalStateException("No counter range was assigned for the flight");

        CounterGroup counterGroup = sectors.get(sectorAndCounter.getLeft()).getCounterGroupMap().get(sectorAndCounter.getRight());

        booking = expectedPassengerList.get(bookingCode);
        if (booking != null) {
            return new Triple<>(booking, counterGroup, -1);
        }

        booking = counterGroup.getPendingPassengers().stream().filter(b -> b.getBookingCode().equals(bookingCode)).findFirst().orElseThrow(IllegalStateException::new);
        return new Triple<>(booking, counterGroup, 1);
    }

    public boolean hasPendingPassenger(String airlineName) {
        for (Booking booking : allRegisteredPassengers.values())
            if (booking.getAirlineName().equals(airlineName))
                return true;

        return false;
    }

    public SortedMap<String, Sector> counters(String sectorName) {
        if (nextAvailableCounter == 1)
            throw new IllegalStateException("No counters registered");

        if (sectorName == null)
            return sectors;

        SortedMap<String, Sector> toRet = new TreeMap<>();
        toRet.put(sectorName, sectors.get(sectorName));
        return toRet;
    }

    public List<BookingHist> checkins(String sectorName, String airlineName) {
        if (checkedinPassengerList.isEmpty())
            throw new IllegalStateException("No checkins registered");

        Stream<BookingHist> stream = checkedinPassengerList.values().stream();

        if (sectorName != null) {
            stream = stream.filter(bookingHist -> bookingHist.getSector().equals(sectorName));
        }

        if (airlineName != null) {
            stream = stream.filter(bookingHist -> bookingHist.getAirlineName().equals(airlineName));
        }

        return stream.collect(Collectors.toList());
    }

}
