package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.TestUtils;
import ar.edu.itba.pod.tpe1.models.BookingHist;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static ar.edu.itba.pod.tpe1.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class CounterReservationTest {
    private AirportRepository airportRepository;

    @BeforeEach
    public void setUp() {
        airportRepository = new AirportRepository(new HashMap<>(), new HashMap<>(), new ArrayList<>(), new HashMap<>());
    }

    @Test
    public final void noSectorsTest() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> airportRepository.listSectors(),
                "Expected IllegalStateException since no sectors were registered");

        assertTrue(exception.getMessage().contains("No sectors registered"));
    }

    @Test
    public final void listSectorsJoinCountersTest() {
        airportRepository.addSector(SECTOR_A);

        airportRepository.addCounters(SECTOR_A, 5);
        airportRepository.addCounters(SECTOR_A, 10);

        SortedMap<String, SortedMap<Integer, Integer>> sectors = airportRepository.listSectors();

        assertEquals(1, sectors.size());
        assertEquals(1, sectors.get(SECTOR_A).size());
        assertEquals(15, sectors.get(SECTOR_A).get(1));

    }

    @Test
    public final void listSectorsJoinAssignedCountersTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        SortedMap<String, SortedMap<Integer, Integer>> sectors = airportRepository.listSectors();

        assertEquals(1, sectors.size());
        assertEquals(1, sectors.get(SECTOR_A).size());
    }

    @Test
    public final void listSectorsSplitBySectorTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addSector(SECTOR_B);

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.addCounters(SECTOR_B, 15);
        airportRepository.addCounters(SECTOR_A, 15);

        SortedMap<String, SortedMap<Integer, Integer>> sectors = airportRepository.listSectors();

        assertEquals(2, sectors.size());
        assertEquals(2, sectors.get(SECTOR_A).size());
    }

    @Test
    public final void listCountersSectorDoesNotExistTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.listCounters(SECTOR_A),
                "Expected IllegalArgumentException since sector not found");

        assertTrue(exception.getMessage().contains("Sector not found"));
    }

    @Test
    public final void listCountersSectorSplitWhenAssignedTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        SortedMap<Integer, CounterGroup> counters = airportRepository.listCounters(SECTOR_A);

        assertEquals(2, counters.size());
    }

    @Test
    public final void assignCountersSectorDoesNotExistTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10),
                "Expected IllegalArgumentException since sector not found");

        assertTrue(exception.getMessage().contains("Sector not found"));
    }

    @Test
    public final void assignCountersNoFlightCodesRegisteredTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10),
                "Expected IllegalArgumentException at least one of the flight codes have not been registered");

        assertTrue(exception.getMessage().contains("At least one of the flight codes have not been registered"));
    }

    @Test
    public final void assignCountersAssignedToOtherAirlineTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.assignCounters(SECTOR_A, AIRLINE_B, List.of(FLIGHT_CODE_1), 10),
                "Expected IllegalArgumentException flight code missmatch");

        assertTrue(exception.getMessage().contains("A flight code is already assigned to another airline"));
    }


    // TODO:
    /*
    *   ○ Ya existe al menos un mostrador asignado para al menos uno de los vuelos
        solicitados (no se permiten agrandar rangos de mostradores asignados)
        ○ Ya existe una solicitud pendiente de un rango de mostradores para al menos uno
        de los vuelos solicitados (no se permiten reiterar asignaciones pendientes)
        ○ Ya se asignó y luego se liberó un rango de mostradores para al menos uno de los
        vuelos solicitados (no se puede iniciar el check-in de un vuelo dos o más veces)
    * */

    @Test
    public final void freeCountersSectorDoesNotExistTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 10),
                "Expected IllegalArgumentException since sector not found");

        assertTrue(exception.getMessage().contains("Sector not found"));
    }

    @Test
    public final void freeCountersInvalidRangeTest() {
        airportRepository.addSector(SECTOR_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 10),
                "Expected IllegalArgumentException since sector no sectors start with counterFrom value");

        assertTrue(exception.getMessage().contains("No counter groups start on counter"));
    }

    @Test
    public final void freeCountersSuccessTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 1);

        SortedMap<Integer, CounterGroup> counters = airportRepository.listCounters(SECTOR_A);

        assertEquals(1, counters.size());
    }

    @Test
    public final void freeCountersWrongSectorTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addSector(SECTOR_B);
        airportRepository.addPassenger(PASSENGER_A);

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.freeCounters(SECTOR_B, AIRLINE_A, 1),
                "Expected IllegalArgumentException since sector no sectors start with counterFrom value");

        assertTrue(exception.getMessage().contains("No counter groups start on counter"));
    }

    @Test
    public final void freeCountersWrongAirlineTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.freeCounters(SECTOR_A, AIRLINE_B, 1),
                "Expected IllegalArgumentException since airlines don't match");

        assertTrue(exception.getMessage().contains("Counter does not correspond to requested airline"));
    }

    @Test
    public final void checkinCountersSectorNotFoundTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.checkInCounters(SECTOR_A, 1, AIRLINE_A),
                "Expected IllegalArgumentException since sector not found");

        assertTrue(exception.getMessage().contains("Sector not found"));
    }

    @Test
    public final void checkinCountersWrongCountersTest() {
        airportRepository.addSector(SECTOR_A);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.checkInCounters(SECTOR_A, 1, AIRLINE_A),
                "Expected IllegalArgumentException since no counter group starts at requested counter");

        assertTrue(exception.getMessage().contains("No counter groups start on counter"));
    }

    @Test
    public final void checkinCountersWrongAirlineTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        airportRepository.addCounters(SECTOR_A, 15);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.checkInCounters(SECTOR_A, 1, AIRLINE_B),
                "Expected IllegalArgumentException since airlines don't match");

        assertTrue(exception.getMessage().contains("Counter does not correspond to requested airline"));
    }

    @Test
    public final void checkinCountersTest() {
        airportRepository.addSector(SECTOR_A);

        airportRepository.addCounters(SECTOR_A, 15);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);
        airportRepository.passengerCheckin(PASSENGER_A.getBookingCode(), SECTOR_A, 1);

        List<BookingHist> checkins = airportRepository.checkInCounters(SECTOR_A, 1, AIRLINE_A);

        assertEquals(10, checkins.size());
        assertNotNull(checkins.get(0).getAirlineName());
        assertNull(checkins.get(1).getAirlineName());
    }

    @Test
    public final void listPendingAssignmentsNotExistTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.listPendingAssignments(SECTOR_A),
                "Expected IllegalArgumentException since sector not found");

        assertTrue(exception.getMessage().contains("Sector not found"));
    }

    @Test
    public final void listPendingAssignmentsTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addPassenger(PASSENGER_B);

        Pair<Boolean, Integer> firstRet = airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);
        Pair<Boolean, Integer> secondRet = airportRepository.assignCounters(SECTOR_A, AIRLINE_B, List.of(FLIGHT_CODE_2), 10);

        List<CheckinAssignment> checkinAssignments = airportRepository.listPendingAssignments(SECTOR_A);
        assertEquals(2, checkinAssignments.size());
        assertFalse(firstRet.getLeft());
        assertFalse(secondRet.getLeft());
        assertEquals(0, firstRet.getRight());
        assertEquals(1, secondRet.getRight());
    }

    @Test
    public final void listPendingAssignmentsAddedSizeAfterTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);
        airportRepository.addCounters(SECTOR_A, 34);

        List<CheckinAssignment> checkinAssignments = airportRepository.listPendingAssignments(SECTOR_A);
        assertTrue(checkinAssignments.isEmpty());

        assertEquals(2, airportRepository.listCounters(SECTOR_A).size());
        assertEquals(1, airportRepository.listSectors().get(SECTOR_A).size());

    }

    @Test
    public final void listPendingAssignmentsFreeCountersTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);
        airportRepository.addPassenger(PASSENGER_B);

        airportRepository.addCounters(SECTOR_A, 15);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_B, List.of(FLIGHT_CODE_2), 10);

        List<CheckinAssignment> checkinAssignments = airportRepository.listPendingAssignments(SECTOR_A);
        assertEquals(1, checkinAssignments.size());

        airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 1);
        checkinAssignments = airportRepository.listPendingAssignments(SECTOR_A);
        assertTrue(checkinAssignments.isEmpty());

        assertEquals(2, airportRepository.listCounters(SECTOR_A).size());
        assertEquals(1, airportRepository.listSectors().get(SECTOR_A).size());
    }

    @Test
    public final void dummyTestForTesting() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addPassenger(PASSENGER_A);

        airportRepository.addCounters(SECTOR_A, 10);

        System.out.println(airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10));
        System.out.println(airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1), 10));

        printCounters(airportRepository.listCounters(SECTOR_A));

        System.out.println("======================================");

        airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 1);
//        printCounters(airportRepository.listCounters(SECTOR_A));
//        airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 11);
        airportRepository.addCounters(SECTOR_A, 5);
        printCounters(airportRepository.listCounters(SECTOR_A));

    }


}
