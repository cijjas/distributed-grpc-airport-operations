package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Counter;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ar.edu.itba.pod.tpe1.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class PassengerCheckinTest {
    private AirportRepository airportRepository;

    @BeforeEach
    public void setUp() {
        airportRepository = new AirportRepository(new HashMap<>(), new HashMap<>(), new ArrayList<>(), new HashMap<>());
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

    // TODO: tests 3.3

}
