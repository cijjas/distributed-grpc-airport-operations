package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.grpc.CheckInStatus;
import ar.edu.itba.pod.grpc.FlightStatus;
import ar.edu.itba.pod.tpe1.models.*;
import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.UnassignedCounterGroup;
import ar.edu.itba.pod.tpe1.models.FlightStatus.FlightStatusInfo;
import ar.edu.itba.pod.tpe1.models.PassengerStatus.PassengerStatusInfo;

import java.net.PasswordAuthentication;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import ar.edu.itba.pod.tpe1.semaphores.SemaphoreAdministrator;
import ar.edu.itba.pod.tpe1.servants.EventsServant;
import lombok.Getter;

public class AirportRepository {
    @Getter
    private final PassengerRepository passengerRepository;
    @Getter
    private final AirlineRepository airlineRepository;

    private final ConcurrentSkipListMap<String, Sector> sectors;
    private final SemaphoreAdministrator semaphoreAdmin;
    private int nextAvailableCounter;
    private static final Object nextAvailableCounterLock = new Object();

    public AirportRepository(
            ConcurrentMap<String, Booking> allRegisteredPassengers,
            ConcurrentMap<String, Booking> expectedPassengerList,
            ConcurrentMap<String, BookingHist> checkedinPassengerList,
            ConcurrentMap<String, CopyOnWriteArrayList<Flight>> airlineFlightCodes
    ) {
        this.semaphoreAdmin = new SemaphoreAdministrator();
        this.passengerRepository = new PassengerRepository(allRegisteredPassengers, expectedPassengerList, checkedinPassengerList);
        this.airlineRepository = new AirlineRepository(airlineFlightCodes, this.semaphoreAdmin);
        this.sectors = new ConcurrentSkipListMap<>();
        nextAvailableCounter = 1;
    }

    public synchronized void addSector(String sectorName) {
        if (sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector name already exists");

        semaphoreAdmin.addSectorLock(sectorName);
        sectors.put(sectorName, new Sector(sectorName, airlineRepository));
    }

    public Integer addCounters(String sectorName, int counterCount) {
        return addCounters(sectorName, counterCount, null);
    }

    public Integer addCounters(String sectorName, int counterCount, EventsServant eventsServant) {
        if (!sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector not found");

        if (counterCount <= 0)
            throw new IllegalArgumentException("Counter count must be greater than 0");

        Sector sector = sectors.get(sectorName);

        synchronized (nextAvailableCounterLock) {
            sector.addCounterGroup(nextAvailableCounter, new UnassignedCounterGroup(nextAvailableCounter, counterCount), eventsServant);
            nextAvailableCounter += counterCount;

            return nextAvailableCounter - counterCount;
        }
    }


    public void addPassenger(Booking booking) {
        semaphoreAdmin.addAndWriteLockBookingAndFlightCodes(booking);

        if (passengerRepository.passengerWasRegistered(booking.getBookingCode())){
            semaphoreAdmin.writeUnlockBookingAndFlightCodes(booking);
            throw new IllegalArgumentException("Booking with code " + booking.getBookingCode() + " already exists");
        }

        if (airlineRepository.flightCodeAlreadyExistsForOtherAirlines(booking.getAirlineName(), List.of(booking.getFlightCode()))){
            semaphoreAdmin.writeUnlockBookingAndFlightCodes(booking);
            throw new IllegalArgumentException("Flight with code " + booking.getFlightCode() + " is already assigned to another airline");
        }
        airlineRepository.addAirlineIfNotExists(booking.getAirlineName());
        airlineRepository.addFlightToAirline(booking.getAirlineName(), booking.getFlightCode());
        passengerRepository.addNewPassenger(booking);

        semaphoreAdmin.writeUnlockBookingAndFlightCodes(booking);
    }

    public SortedMap<String, SortedMap<Integer, Integer>> listSectors() {
        SortedMap<String, SortedMap<Integer, Integer>> mappedSectors = new TreeMap<>();
        if (sectors.isEmpty())
            throw new IllegalStateException("No sectors registered");

        for (Map.Entry<String, Sector> entry : sectors.entrySet())
            mappedSectors.put(entry.getKey(), entry.getValue().listGroupedCounters());

        return mappedSectors;
    }

    public SortedMap<Integer, CounterGroup> listCounters(String sectorName) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        return sectors.get(sectorName).getCounterGroupMap();
    }

    public SortedMap<Integer, CounterGroup> listCounters(String sectorName, int fromVal, int toVal) {
        if (!sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector not found");

        if (toVal - fromVal < 0)
            throw new IllegalArgumentException("Requested range is not valid");

        return sectors.get(sectorName).listCounters(fromVal, toVal);
    }

    public Pair<Boolean, Integer> assignCounters(String sectorName, String airlineName, List<String> flightCodes, int counterCount) {
        if (!sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector not found");

        semaphoreAdmin.readLockFlightCodes();

        if (!airlineRepository.allFlightCodesRegistered(flightCodes)) {
            semaphoreAdmin.readUnlockFlightCodes();
            throw new IllegalArgumentException("At least one of the flight codes have not been registered");
        }

        if (airlineRepository.flightCodeAlreadyExistsForOtherAirlines(airlineName, flightCodes)) {
            semaphoreAdmin.readUnlockFlightCodes();
            throw new IllegalArgumentException("A requested flight code is assigned to another airline");
        }

        if (!airlineRepository.allFlightCodesAreNew(airlineName, flightCodes)) {
            semaphoreAdmin.readUnlockFlightCodes();
            throw new IllegalStateException("At least one flight code has been assigned, is pending or has ended");
        }

        CheckinAssignment checkinAssignment = new CheckinAssignment(airlineName, flightCodes, counterCount);

        semaphoreAdmin.unlockReadLockWriteFlightCodes();

        Pair<Boolean, Integer> toRet = sectors.get(sectorName).assignCounterGroup(checkinAssignment);
        airlineRepository.markFlightsAsAssigned(airlineName, flightCodes);

        semaphoreAdmin.writeUnlockFlightCodes();
        return toRet;
    }

    public CounterGroup freeCounters(String sectorName, String airlineName, int counterFrom) {
        return freeCounters(sectorName, airlineName, counterFrom, null);
    }

    public CounterGroup freeCounters(String sectorName, String airlineName, int counterFrom, EventsServant eventsServant) {
        if (!sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector not found");
        return sectors.get(sectorName).freeCounters(airlineName, counterFrom, eventsServant);
    }

    public List<BookingHist> checkInCounters(String sectorName, int counterFrom, String airlineName) {
        if (!sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector not found");

        List<BookingHist> toRet = sectors.get(sectorName).checkinCounters(counterFrom, airlineName);
        passengerRepository.addToCheckinHistory(sectorName, toRet);
        return toRet;
    }


    public List<CheckinAssignment> listPendingAssignments(String sectorName) {
        if (!sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector not found");

        return sectors.get(sectorName).listPendingAssignments();
    }

    public FlightStatusInfo fetchCounter(String bookingCode) {
        if (!passengerRepository.passengerIsExpected(bookingCode)) {
            throw new IllegalArgumentException("Booking code not found");
        }

        semaphoreAdmin.readLockBooking(bookingCode);

        Booking booking = passengerRepository.getExpectedPassenger(bookingCode);
        Pair<String, Integer> sectorAndCounter = airlineRepository.getSectorAndCounterFromAirlineAndFlight(booking.getAirlineName(), booking.getFlightCode());

        if (sectorAndCounter == null || sectorAndCounter.getLeft() == null) {
            semaphoreAdmin.readUnlockBooking(bookingCode);
            return new FlightStatusInfo(FlightStatus.PENDING, booking.getAirlineName(), booking.getFlightCode());
        }

        CounterGroup counterGroup = sectors.get(sectorAndCounter.getLeft()).getCounterGroupMap().get(sectorAndCounter.getRight());
        if (counterGroup == null || !counterGroup.isActive() || !counterGroup.getFlightCodes().contains(booking.getFlightCode())) {
            semaphoreAdmin.readUnlockBooking(bookingCode);
            return new FlightStatusInfo(FlightStatus.EXPIRED, booking.getAirlineName(), booking.getFlightCode());

        }

        semaphoreAdmin.readUnlockBooking(bookingCode);
        return new FlightStatusInfo(FlightStatus.CHECKING_IN, booking.getAirlineName(), booking.getFlightCode(), sectorAndCounter.getLeft(), counterGroup);
    }

    public PassengerStatusInfo passengerCheckin(String bookingCode, String sectorName, int counterFrom) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        semaphoreAdmin.writeLockBooking(bookingCode);

        if (!passengerRepository.passengerIsExpected(bookingCode)) {
            semaphoreAdmin.writeUnlockBooking(bookingCode);
            throw new IllegalArgumentException("Booking code not found or user checked-in");
        }

        Booking booking = passengerRepository.getExpectedPassenger(bookingCode);
        CounterGroup counterGroup = sectors.get(sectorName).passengerCheckin(booking, counterFrom);
        passengerRepository.removeExpectedPassenger(bookingCode);

        semaphoreAdmin.writeUnlockBooking(bookingCode);
        return new PassengerStatusInfo(CheckInStatus.AWAITING, booking, sectorName, counterGroup);
    }

    public PassengerStatusInfo passengerStatus(String bookingCode) {
        if (!passengerRepository.passengerWasRegistered(bookingCode)) {
            throw new IllegalArgumentException("No expected passenger with requested booking code");
        }

        semaphoreAdmin.readLockBooking(bookingCode);

        BookingHist bookingHist = passengerRepository.findCheckedinPassenger(bookingCode);
        if (bookingHist != null) {
            semaphoreAdmin.readUnlockBooking(bookingCode);
            return new PassengerStatusInfo(CheckInStatus.CHECKED_IN,
                    bookingHist,
                    bookingHist.getSector(),
                    bookingHist.getCheckinCounter());
        }

        Booking booking = passengerRepository.getBookingData(bookingCode);
        Pair<String, Integer> sectorAndCounter = airlineRepository.getSectorAndCounterFromAirlineAndFlight(booking.getAirlineName(), booking.getFlightCode());

        if (sectorAndCounter == null || sectorAndCounter.getLeft() == null) {
            semaphoreAdmin.readUnlockBooking(bookingCode);
            throw new IllegalStateException("No counter range was assigned for the flight");
        }

        CounterGroup counterGroup = sectors.get(sectorAndCounter.getLeft()).getCounterGroupMap().get(sectorAndCounter.getRight());

        booking = passengerRepository.getExpectedPassenger(bookingCode);
        if (booking != null) {
            semaphoreAdmin.readUnlockBooking(bookingCode);
            return new PassengerStatusInfo(CheckInStatus.NOT_CHECKED_IN,
                    booking,
                    sectorAndCounter.getLeft(),
                    counterGroup);
        }

        booking = counterGroup.getPendingPassengers().stream().filter(b -> b.getBookingCode().equals(bookingCode)).findFirst().orElseThrow(IllegalStateException::new);
        semaphoreAdmin.readUnlockBooking(bookingCode);
        return new PassengerStatusInfo(CheckInStatus.AWAITING,
                booking,
                sectorAndCounter.getLeft(),
                counterGroup);
    }

    public SortedMap<String, Sector> counters(String sectorName) {
        if (nextAvailableCounter == 1)
            throw new IllegalStateException("No counters registered");

        if (sectorName == null || sectorName.isEmpty())
            return sectors;

        SortedMap<String, Sector> toRet = new TreeMap<>();
        toRet.put(sectorName, sectors.get(sectorName));
        return toRet;
    }

    public List<BookingHist> checkins(String sectorName, String airlineName) {
        return passengerRepository.checkins(sectorName, airlineName);
    }
}