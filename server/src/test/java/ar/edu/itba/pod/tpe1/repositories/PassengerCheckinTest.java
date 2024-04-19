package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static ar.edu.itba.pod.tpe1.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class PassengerCheckinTest {
    private AirportRepository airportRepository;

    @BeforeEach
    public void setUp() {
        airportRepository = new AirportRepository(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Test
    public final void fetchCounterNoBookingCodeTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.fetchCounter(BOOKING_CODE_1),
                "Expected IllegalArgumentException since booking code was not found");

        assertTrue(exception.getMessage().contains("Booking code not found"));
    }

    @Test
    public final void fetchCountersNotYetAssignedTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        CounterGroup counterGroup = airportRepository.fetchCounter(PASSENGER_A.getBookingCode());

        assertNull(counterGroup);
    }

    @Test
    public final void fetchCountersAssignedTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);

        CounterGroup counterGroup = airportRepository.fetchCounter(PASSENGER_A.getBookingCode());

        assertNotNull(counterGroup);
        assertEquals(PASSENGER_A.getAirlineName(), counterGroup.getAirlineName());
    }

    @Test
    public final void passengerCheckinBookingCodeNotFoundTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.passengerCheckin(BOOKING_CODE_1, SECTOR_A, 1),
                "Expected IllegalArgumentException since booking code was not found");

        assertTrue(exception.getMessage().contains("Booking code not found"));
    }

    @Test
    public final void passengerCheckinSectorNotFoundTest() {
        airportRepository.addPassenger(PASSENGER_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.passengerCheckin(BOOKING_CODE_1, SECTOR_A, 1),
                "Expected IllegalArgumentException since sector not found");

        assertTrue(exception.getMessage().contains("Sector not found"));
    }

    @Test
    public final void passengerCheckinInvalidCounterTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.passengerCheckin(BOOKING_CODE_1, SECTOR_A, 1),
                "Expected IllegalArgumentException since invalid counter was asked for");

        assertTrue(exception.getMessage().contains("Invalid counter start"));
    }

    @Test
    public final void passengerCheckinWrongBookingTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addPassenger(PASSENGER_B);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.passengerCheckin(BOOKING_CODE_2, SECTOR_A, 1),
                "Expected IllegalArgumentException since invalid counter was asked for");

        assertTrue(exception.getMessage().contains("Invalid counter start"));
    }

    @Test
    public final void passengerCheckinAlreadyInQueueTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);

        airportRepository.passengerCheckin(BOOKING_CODE_1, SECTOR_A, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.passengerCheckin(BOOKING_CODE_1, SECTOR_A, 1),
                "Expected IllegalArgumentException since user already checked in");

        assertTrue(exception.getMessage().contains("Booking code not found or user checked-in"));
    }

    @Test
    public final void passengerCheckinAlreadyCheckedInTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);

        airportRepository.passengerCheckin(BOOKING_CODE_1, SECTOR_A, 1);
        airportRepository.checkInCounters(SECTOR_A, 1, PASSENGER_A.getAirlineName());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.passengerCheckin(BOOKING_CODE_1, SECTOR_A, 1),
                "Expected IllegalArgumentException since user already checked in");

        assertTrue(exception.getMessage().contains("Booking code not found or user checked-in"));
    }

    @Test
    public final void passengerStatusUnregisteredTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.passengerStatus(PASSENGER_A.getBookingCode()),
                "Expected IllegalArgumentException since no passenger with requested booking code was found");

        assertTrue(exception.getMessage().contains("No expected passenger with requested booking code"));

    }

    @Test
    public final void passengerStatusNoCountersAvailableTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 10);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> airportRepository.passengerStatus(PASSENGER_A.getBookingCode()),
                "Expected IllegalStateException since no counter range was assigned for the flight");

        assertTrue(exception.getMessage().contains("No counter range was assigned for the flight"));

    }


    @Test
    public final void passengerStatusPendingTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);

        Triple<Booking, CounterGroup, Integer> returnVal = airportRepository.passengerStatus(PASSENGER_A.getBookingCode());

        assertSame(PASSENGER_A.getAirlineName(), returnVal.getLeft().getAirlineName());
        assertSame(PASSENGER_A.getFlightCode(), returnVal.getLeft().getFlightCode());
        assertSame(PASSENGER_A.getBookingCode(), returnVal.getLeft().getBookingCode());
        assertEquals(1, returnVal.getMiddle().getCounterStart());
        assertEquals(10, returnVal.getMiddle().getCounterCount());
        assertEquals(-1, returnVal.getRight());
    }

    @Test
    public final void passengerStatusInQueueTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);

        Triple<Booking, CounterGroup, Integer> returnVal = airportRepository.passengerStatus(PASSENGER_A.getBookingCode());

        assertSame(PASSENGER_A.getAirlineName(), returnVal.getLeft().getAirlineName());
        assertSame(PASSENGER_A.getFlightCode(), returnVal.getLeft().getFlightCode());
        assertSame(PASSENGER_A.getBookingCode(), returnVal.getLeft().getBookingCode());
        assertEquals(1, returnVal.getMiddle().getCounterStart());
        assertEquals(1, returnVal.getRight());
    }

    @Test
    public final void passengerStatusCheckedInTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);
        airportRepository.checkInCounters(SECTOR_A, 1, PASSENGER_A.getAirlineName());

        Triple<Booking, CounterGroup, Integer> returnVal = airportRepository.passengerStatus(PASSENGER_A.getBookingCode());

        assertSame(PASSENGER_A.getAirlineName(), returnVal.getLeft().getAirlineName());
        assertSame(PASSENGER_A.getFlightCode(), returnVal.getLeft().getFlightCode());
        assertSame(PASSENGER_A.getBookingCode(), returnVal.getLeft().getBookingCode());
        assertNull(returnVal.getMiddle());
        assertEquals(1, returnVal.getRight());
    }
}
