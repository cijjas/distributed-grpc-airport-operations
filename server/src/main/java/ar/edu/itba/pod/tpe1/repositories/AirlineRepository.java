package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Flight;
import ar.edu.itba.pod.tpe1.models.Pair;
import ar.edu.itba.pod.tpe1.semaphores.SemaphoreAdministrator;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AirlineRepository {
    private final ConcurrentMap<String, CopyOnWriteArrayList<Flight>> airlineFlightCodes;
    private final SemaphoreAdministrator semaphoreAdministrator;

    public AirlineRepository(ConcurrentMap<String, CopyOnWriteArrayList<Flight>> airlineFlightCodes, SemaphoreAdministrator semaphoreAdministrator) {
        this.semaphoreAdministrator = semaphoreAdministrator;
        this.airlineFlightCodes = airlineFlightCodes;
    }

    public void addAirlineIfNotExists(String airlineName) {
        semaphoreAdministrator.addAndWriteLockAirline(airlineName);
        if (!airlineFlightCodes.containsKey(airlineName))
            airlineFlightCodes.put(airlineName, new CopyOnWriteArrayList<>());
        semaphoreAdministrator.writeUnlockAirlineName(airlineName);
    }

    public void addFlightToAirline(String airlineName, String flightCode) {
        semaphoreAdministrator.writeLockAirlineName(airlineName);
        airlineFlightCodes.get(airlineName).add(new Flight(flightCode));
        semaphoreAdministrator.writeUnlockAirlineName(airlineName);
    }

    public boolean flightCodeAlreadyExistsForOtherAirlines(String currentAirline, List<String> flightCodes) {
        return airlineFlightCodes.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(currentAirline))
                .flatMap(entry -> entry.getValue().stream())
                .map(Flight::getFlightCode)
                .anyMatch(flightCodes::contains);
    }

    public boolean allFlightCodesRegistered(List<String> flightCodes) {
        Collection<CopyOnWriteArrayList<Flight>> flights = airlineFlightCodes.values();
        Set<String> allFlightCodes = flights.stream().flatMap(List::stream).map(Flight::getFlightCode).collect(Collectors.toSet());
        return allFlightCodes.containsAll(flightCodes);
    }

    public boolean allFlightCodesAreNew(String airlineName, List<String> flightCodes) {
        List<Flight> flights = airlineFlightCodes.get(airlineName).stream()
                .filter(flight -> flightCodes.contains(flight.getFlightCode()))
                .toList();

        for (Flight flight : flights) {
            if (flight.isWasAssigned())
                return false;
        }

        return true;
    }

    public void markFlightsAsAssigned(String airlineName, List<String> flightCodes) {
        semaphoreAdministrator.writeLockFlightCodes();
        airlineFlightCodes
                .get(airlineName)
                .stream()
                .filter(flight -> flightCodes.contains(flight.getFlightCode()))
                .forEach(Flight::assign);
        semaphoreAdministrator.writeUnlockFlightCodes();
    }


    public void assignCountersToFlights(String sectorName, int counterStart, CounterGroup counterGroup) {
        List<Flight> flights = airlineFlightCodes
                .get(counterGroup.getAirlineName())
                .stream()
                .filter(flight -> counterGroup.getFlightCodes().contains(flight.getFlightCode()))
                .toList();

        semaphoreAdministrator.writeLockFlightCodes();
        for (Flight flight : flights) {
            flight.setCounterStart(counterStart);
            flight.setCounterGroup(counterGroup);
            flight.setSector(sectorName);
        }
        semaphoreAdministrator.writeUnlockFlightCodes();
    }

    public Pair<String, Integer> getSectorAndCounterFromAirlineAndFlight(String airlineName, String flightCode) {
        Optional<Flight> optionalFlight = airlineFlightCodes.get(airlineName).stream().filter(flight -> flight.getFlightCode().equals(flightCode)).findFirst();

        return optionalFlight.map(flight -> new Pair<>(flight.getSector(), flight.getCounterStart())).orElse(null);
    }
}
