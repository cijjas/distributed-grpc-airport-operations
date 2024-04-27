package ar.edu.itba.pod.tpe1;

import ar.edu.itba.pod.grpc.CheckInStatus;
import ar.edu.itba.pod.grpc.FlightStatus;
import ar.edu.itba.pod.tpe1.models.FlightStatus.FlightStatusInfo;
import ar.edu.itba.pod.tpe1.models.PassengerStatus.PassengerStatusInfo;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static ar.edu.itba.pod.tpe1.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class PassengerCheckinTest {
    private AirportRepository airportRepository;

    @BeforeEach
    public void setUp() {
        airportRepository = new AirportRepository(
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>());
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

        FlightStatusInfo flightStatusInfo = airportRepository.fetchCounter(PASSENGER_A.getBookingCode());

        assertEquals(FlightStatus.PENDING, flightStatusInfo.getFlightStatus());
    }

    @Test
    public final void fetchCountersAssignedTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);

        FlightStatusInfo flightStatusInfo = airportRepository.fetchCounter(PASSENGER_A.getBookingCode());

        assertEquals(FlightStatus.CHECKING_IN, flightStatusInfo.getFlightStatus());
        assertEquals(PASSENGER_A.getAirlineName(), flightStatusInfo.getAirlineName());
    }

    @Test
    public final void fetchCountersExpiredTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);
        airportRepository.freeCounters(SECTOR_A, PASSENGER_A.getAirlineName(), 1);

        FlightStatusInfo flightStatusInfo = airportRepository.fetchCounter(PASSENGER_A.getBookingCode());

        assertEquals(FlightStatus.EXPIRED, flightStatusInfo.getFlightStatus());
        assertEquals(PASSENGER_A.getAirlineName(), flightStatusInfo.getAirlineName());
    }

    @Test
    public final void passengerCheckinBookingCodeNotFoundTest() {
        airportRepository.addSector(SECTOR_A);

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

        assertTrue(exception.getMessage().contains("User checking in"));
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

        assertTrue(exception.getMessage().contains("User checked-in"));
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

        PassengerStatusInfo returnVal = airportRepository.passengerStatus(PASSENGER_A.getBookingCode());

        assertSame(PASSENGER_A.getAirlineName(), returnVal.getBooking().getAirlineName());
        assertSame(PASSENGER_A.getFlightCode(), returnVal.getBooking().getFlightCode());
        assertSame(PASSENGER_A.getBookingCode(), returnVal.getBooking().getBookingCode());
        assertEquals(1, returnVal.getCounterGroup().getCounterStart());
        assertEquals(10, returnVal.getCounterGroup().getCounterCount());
        assertEquals(CheckInStatus.NOT_CHECKED_IN, returnVal.getCheckInStatus());
    }

    @Test
    public final void passengerStatusInQueueTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);

        PassengerStatusInfo returnVal = airportRepository.passengerStatus(PASSENGER_A.getBookingCode());

        assertSame(PASSENGER_A.getAirlineName(), returnVal.getBooking().getAirlineName());
        assertSame(PASSENGER_A.getFlightCode(), returnVal.getBooking().getFlightCode());
        assertSame(PASSENGER_A.getBookingCode(), returnVal.getBooking().getBookingCode());
        assertEquals(1, returnVal.getCounterGroup().getCounterStart());
        assertEquals(CheckInStatus.AWAITING, returnVal.getCheckInStatus());
    }

    @Test
    public final void passengerStatusCheckedInTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, PASSENGER_A.getAirlineName(), List.of(PASSENGER_A.getFlightCode()), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);
        airportRepository.checkInCounters(SECTOR_A, 1, PASSENGER_A.getAirlineName());

        PassengerStatusInfo returnVal = airportRepository.passengerStatus(PASSENGER_A.getBookingCode());

        assertSame(PASSENGER_A.getAirlineName(), returnVal.getBooking().getAirlineName());
        assertSame(PASSENGER_A.getFlightCode(), returnVal.getBooking().getFlightCode());
        assertSame(PASSENGER_A.getBookingCode(), returnVal.getBooking().getBookingCode());
        assertNull(returnVal.getCounterGroup());
        assertEquals(CheckInStatus.CHECKED_IN, returnVal.getCheckInStatus());
    }
}
