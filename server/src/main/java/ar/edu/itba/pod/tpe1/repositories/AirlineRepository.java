package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.Flight;
import ar.edu.itba.pod.tpe1.models.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class AirlineRepository {
    private final Map<String, List<Flight>> airlineFlightCodes;

    public AirlineRepository(Map<String, List<Flight>> airlineFlightCodes) {
        this.airlineFlightCodes = airlineFlightCodes;
    }

    public void addAirlineIfNotExists(String airlineName) {
        if (!airlineFlightCodes.containsKey(airlineName))
            airlineFlightCodes.put(airlineName, new ArrayList<>());
    }

    public void addFlightToAirline(String airlineName, String flightCode) {
        airlineFlightCodes.get(airlineName).add(new Flight(flightCode));
    }

    public boolean flightCodeAlreadyExistsForOtherAirlines(String currentAirline, List<String> flightCodes) {
        for (Map.Entry<String, List<Flight>> entry : airlineFlightCodes.entrySet()) {
            if (!entry.getKey().equals(currentAirline)
                    && entry.getValue().stream().map(Flight::getFlightCode).anyMatch(flightCodes::contains)) {
                return true;
            }
        }
        return false;
    }

    public boolean allFlightCodesRegistered(List<String> flightCodes) {
        Collection<List<Flight>> flights = airlineFlightCodes.values();
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
        airlineFlightCodes.get(airlineName).stream()
                .filter(flight -> flightCodes.contains(flight.getFlightCode()))
                .forEach(Flight::assign);
    }


    public void assignCountersToFlights(String sectorName, int counterStart, CounterGroup counterGroup) {
        List<Flight> flights = airlineFlightCodes.get(counterGroup.getAirlineName()).stream()
                .filter(flight -> counterGroup.getFlightCodes().contains(flight.getFlightCode()))
                .toList();

        for (Flight flight : flights) {
            flight.setCounterStart(counterStart);
            flight.setCounterGroup(counterGroup);
            flight.setSector(sectorName);
        }
    }

    public Pair<String, Integer> getSectorAndCounterFromAirlineAndFlight(String airlineName, String flightCode) {
        Optional<Flight> optionalFlight = airlineFlightCodes.get(airlineName).stream().filter(flight -> flight.getFlightCode().equals(flightCode)).findFirst();

        return optionalFlight.map(flight -> new Pair<>(flight.getSector(), flight.getCounterStart())).orElse(null);
    }
}
