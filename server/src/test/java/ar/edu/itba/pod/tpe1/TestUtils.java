package ar.edu.itba.pod.tpe1;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.repositories.AirportRepository;

import java.util.Map;
import java.util.SortedMap;

public class TestUtils {

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
    public static final Booking PASSENGER_B = new Booking(AIRLINE_B, FLIGHT_CODE_2, BOOKING_CODE_2);


    public static void printSectors(AirportRepository airportRepository) {
        SortedMap<String, SortedMap<Integer, Integer>> sectors = airportRepository.listSectors();

        for (Map.Entry<String, SortedMap<Integer, Integer>> entry : sectors.entrySet()) {
            System.out.println(entry.getKey());
            printCountersFromInt(entry.getValue());
            System.out.println();
        }
    }

    public static void printCountersFromInt(SortedMap<Integer, Integer> counterGroupMap) {
        for (Map.Entry<Integer, Integer> counterGroupEntry : counterGroupMap.entrySet()) {
            int from = counterGroupEntry.getKey();
            int to = from + counterGroupEntry.getValue() - 1;
            System.out.print("   (" + from + " - " + to + ')');
        }

    }

    public static void printCounters(SortedMap<Integer, CounterGroup> counterGroupMap) {
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
