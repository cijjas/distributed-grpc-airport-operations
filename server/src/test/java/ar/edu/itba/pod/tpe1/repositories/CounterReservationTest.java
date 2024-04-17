package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static ar.edu.itba.pod.tpe1.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class CounterReservationTest {
    private AirportRepository airportRepository;

    @BeforeEach
    public void setUp() {
        airportRepository = new AirportRepository(new ArrayList<>(), new ArrayList<>());
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

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10);

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

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10);

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

    // TODO:
    /*
    *   ○ No se agregaron pasajeros esperados con el código de vuelo, para al menos uno
        de los vuelos solicitados
        ○ Se agregaron pasajeros esperados con el código de vuelo pero con otra aerolínea,
        para al menos uno de los vuelos solicitados
        ○ Ya existe al menos un mostrador asignado para al menos uno de los vuelos
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

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10);

        airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 1);

        SortedMap<Integer, CounterGroup> counters = airportRepository.listCounters(SECTOR_A);

        assertEquals(1, counters.size());
    }

    @Test
    public final void freeCountersWrongSectorTest() {
        airportRepository.addSector(SECTOR_A);
        airportRepository.addSector(SECTOR_B);

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.freeCounters(SECTOR_B, AIRLINE_A, 1),
                "Expected IllegalArgumentException since sector no sectors start with counterFrom value");

        assertTrue(exception.getMessage().contains("No counter groups start on counter"));
    }

    @Test
    public final void freeCountersWrongAirlineTest() {
        airportRepository.addSector(SECTOR_A);

        airportRepository.addCounters(SECTOR_A, 34);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> airportRepository.freeCounters(SECTOR_A, AIRLINE_B, 1),
                "Expected IllegalArgumentException since airlines don't match");

        assertTrue(exception.getMessage().contains("Counter does not correspond to requested airline"));
    }

    // TODO:
    // Existen pasajeros esperando a ser atendidos en la cola del rango -> Funciona, no se pueden agregar pass todv.


    @Test
    public final void dummyTestForTesting() {
        airportRepository.addSector(SECTOR_A);

        airportRepository.addCounters(SECTOR_A, 34);

        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10);
        airportRepository.assignCounters(SECTOR_A, AIRLINE_A, List.of(FLIGHT_CODE_1, FLIGHT_CODE_2), 10);

        printCounters(airportRepository.listCounters(SECTOR_A));

        System.out.println("======================================");

        airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 1);
//        printCounters(airportRepository.listCounters(SECTOR_A));
//        airportRepository.freeCounters(SECTOR_A, AIRLINE_A, 11);
        airportRepository.addCounters(SECTOR_A, 5);
        printCounters(airportRepository.listCounters(SECTOR_A));

    }

    private void printSectors() {
        SortedMap<String, SortedMap<Integer, Integer>> sectors = airportRepository.listSectors();

        for (Map.Entry<String, SortedMap<Integer, Integer>> entry : sectors.entrySet()) {
            System.out.println(entry.getKey());
            printCountersFromInt(entry.getValue());
            System.out.println();
        }
    }

    private void printCountersFromInt(SortedMap<Integer, Integer> counterGroupMap) {
        for (Map.Entry<Integer, Integer> counterGroupEntry : counterGroupMap.entrySet()) {
            int from = counterGroupEntry.getKey();
            int to = from + counterGroupEntry.getValue() - 1;
            System.out.print("   (" + from + " - " + to + ')');
        }

    }

    private void printCounters(SortedMap<Integer, CounterGroup> counterGroupMap) {
        for (Map.Entry<Integer, CounterGroup> counterGroupEntry : counterGroupMap.entrySet()) {
            int from = counterGroupEntry.getKey();
            int to = from + counterGroupEntry.getValue().getCounterCount() - 1;
            CounterGroup counterGroup = counterGroupEntry.getValue();
            if (counterGroup.isActive()) {
                System.out.println("(" + from + " - " + to + ")\t" + counterGroup.getAirlineName() + "\t" + counterGroup.getFlightCodes().toString() + "\t" + counterGroup.getPendingPassengers().size());
            } else {
                System.out.println("(" + from + " - " + to + ")\t-\t-\t-");
            }
        }

    }
}
