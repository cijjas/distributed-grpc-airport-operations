package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Sector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static ar.edu.itba.pod.tpe1.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class AirportAdministrationTest {

    private AirportRepository airportRepository;


    @BeforeEach
    public void setUp() {
        airportRepository = new AirportRepository(new HashMap<>(), new HashMap<>(), new ArrayList<>(), new HashMap<>());
    }

    @Test
    public final void addSectorTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addSector(SECTOR_B);

        assertEquals(2, airportRepository.listSectors().size());
    }

    @Test
    public final void addDuplicateSectorTest() {
        airportRepository.addSector(SECTOR_A);

        assertThrows(IllegalArgumentException.class,
                () -> airportRepository.addSector(SECTOR_A));
    }

    @Test
    public final void addCountersTest() {
        airportRepository.addSector(SECTOR_A);

        airportRepository.addCounters(SECTOR_A, 3);
    }

    @Test
    public final void addCountersSectorsNotExistTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.addCounters(SECTOR_A, 3),
                "Expected IllegalArgumentException because sector was not found");

        assertTrue(exception.getMessage().contains("Sector not found"));
    }

    @Test
    public final void addCountersNonPositiveTest() {
        airportRepository.addSector(SECTOR_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.addCounters(SECTOR_A, -3),
                "Expected IllegalArgumentException because counter count must be greater than 0");

        assertTrue(exception.getMessage().contains("Counter count must be greater than 0"));
    }


    @Test
    public final void addExpectedPassengersBookingAlreadyExistsTest() {
        airportRepository.addPassenger(PASSENGER_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.addPassenger(PASSENGER_C_SAME_BOOKING),
                "Expected IllegalArgumentException because booking already exists");

        assertTrue(exception.getMessage().contains("Booking with code " + PASSENGER_A.getBookingCode() + " already exists"));
    }

    @Test
    public final void addExpectedPassengersSameFlightDifferentAirlineTest() {
        airportRepository.addPassenger(PASSENGER_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.addPassenger(PASSENGER_B_SAME_FLIGHT),
                "Expected IllegalArgumentException because flight is already assigned to other airline");

        assertTrue(exception.getMessage().contains("Flight with code " + PASSENGER_A.getFlightCode() + " is already assigned to another airline"));
    }
}