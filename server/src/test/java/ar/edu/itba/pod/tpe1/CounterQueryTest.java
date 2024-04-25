package ar.edu.itba.pod.tpe1;

import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import ar.edu.itba.pod.tpe1.models.Sector;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

import static ar.edu.itba.pod.tpe1.TestUtils.*;
import static ar.edu.itba.pod.tpe1.TestUtils.FLIGHT_CODE_1;
import static org.junit.jupiter.api.Assertions.*;

public class CounterQueryTest {
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
    public final void countersNoCountersAddedTest() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> airportRepository.counters(SECTOR_A),
                "Expected IllegalStateException because no counters were registered");

        assertTrue(exception.getMessage().contains("No counters registered"));
    }

    @Test
    public final void countersAllCountersTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        airportRepository.addSector(SECTOR_B);
        airportRepository.addPassenger(PASSENGER_B);
        airportRepository.addCounters(SECTOR_B, 10);
        airportRepository.assignCounters(SECTOR_B, AIRLINE_B, List.of(FLIGHT_CODE_2), 10);


        SortedMap<String, Sector> sectors = airportRepository.counters(null);

        assertEquals(2, sectors.size());
        assertEquals(2, sectors.get(SECTOR_A).getCounterGroupMap().size());
        assertEquals(1, sectors.get(SECTOR_B).getCounterGroupMap().size());
    }

    @Test
    public final void countersFilteredTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        airportRepository.addSector(SECTOR_B);
        airportRepository.addPassenger(PASSENGER_B);
        airportRepository.addCounters(SECTOR_B, 10);
        airportRepository.assignCounters(SECTOR_B, AIRLINE_B, List.of(FLIGHT_CODE_2), 10);


        SortedMap<String, Sector> sectors = airportRepository.counters(SECTOR_A);

        assertEquals(1, sectors.size());
        assertEquals(2, sectors.get(SECTOR_A).getCounterGroupMap().size());
    }

    @Test
    public final void countersFilteredNoResultsTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        SortedMap<String, Sector> sectors = airportRepository.counters(SECTOR_B);

        assertEquals(1, sectors.size());
        assertNull(sectors.get(SECTOR_B));
    }

    @Test
    public final void checkinsNoCheckinsRegisteredTest() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> airportRepository.checkins(null, null),
                "Expected IllegalStateException because no checkins were registered");

        assertTrue(exception.getMessage().contains("No checkins registered"));
    }

    @Test
    public final void checkinsTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 10);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);
        airportRepository.checkInCounters(SECTOR_A, 1, AIRLINE_A);

        List<BookingHist> checkins = airportRepository.checkins(null, null);

        assertEquals(1, checkins.size());
        assertEquals(AIRLINE_A, checkins.get(0).getAirlineName());
        assertEquals(PASSENGER_A.getBookingCode(), checkins.get(0).getBookingCode());
        assertEquals(FLIGHT_CODE_1, checkins.get(0).getFlightCode());
        assertEquals(SECTOR_A, checkins.get(0).getSector());
    }

    @Test
    public final void checkinsWithSectorTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 10);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);
        airportRepository.checkInCounters(SECTOR_A, 1, AIRLINE_A);

        List<BookingHist> checkins = airportRepository.checkins(SECTOR_A, null);

        assertEquals(1, checkins.size());
        assertEquals(AIRLINE_A, checkins.get(0).getAirlineName());
        assertEquals(PASSENGER_A.getBookingCode(), checkins.get(0).getBookingCode());
        assertEquals(FLIGHT_CODE_1, checkins.get(0).getFlightCode());
        assertEquals(SECTOR_A, checkins.get(0).getSector());
    }

    @Test
    public final void checkinsWithAirlineTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 10);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);
        airportRepository.checkInCounters(SECTOR_A, 1, AIRLINE_A);

        List<BookingHist> checkins = airportRepository.checkins(null, AIRLINE_A);

        assertEquals(1, checkins.size());
        assertEquals(AIRLINE_A, checkins.get(0).getAirlineName());
        assertEquals(PASSENGER_A.getBookingCode(), checkins.get(0).getBookingCode());
        assertEquals(FLIGHT_CODE_1, checkins.get(0).getFlightCode());
        assertEquals(SECTOR_A, checkins.get(0).getSector());
    }

    @Test
    public final void checkinsWithDifferentAirlineTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addCounters(SECTOR_A, 10);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);
        airportRepository.checkInCounters(SECTOR_A, 1, AIRLINE_A);

        List<BookingHist> checkins = airportRepository.checkins(null, AIRLINE_B);

        assertEquals(0, checkins.size());
    }


}
