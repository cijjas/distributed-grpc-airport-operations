package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AirportAdministrationTest {

    private AirportRepository airportRepository;

    public static final String SECTOR_A = "A";
    public static final String SECTOR_B = "B";

    public static final String AIRLINE_A = "Airline A";
    public static final String AIRLINE_B = "Airline B";
    public static final String FLIGHT_CODE_1 = "ABC123";
    public static final String FLIGHT_CODE_2 = "CDE123";
    public static final String BOOKING_CODE_1 = "123123";
    public static final String BOOKING_CODE_2 = "234234";

    public static final Booking PASSENGER_A = new Booking(AIRLINE_A, FLIGHT_CODE_1, BOOKING_CODE_1);
    public static final Booking PASSENGER_B_SAME_FLIGHT = new Booking(AIRLINE_B, FLIGHT_CODE_1, BOOKING_CODE_2);
    public static final Booking PASSENGER_C_SAME_BOOKING = new Booking(AIRLINE_A, FLIGHT_CODE_2, BOOKING_CODE_1);

    @BeforeEach
    public void setUp() {
        airportRepository = new AirportRepository(new ArrayList<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>());
    }

    // TODO: verificar que realmente se agregan
    @Test
    public final void addSectorTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addSector(SECTOR_B);
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