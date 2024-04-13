package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CheckinRepository {
    private final String airlineName;
    private final List<String> flightCodes;
    private final Integer firstCounter;
    private final Integer counterCount;
    private final Queue<Booking> pendingPassengers;

    public CheckinRepository(String airlineName, List<String> flightCodes, Integer firstCounter, Integer counterCount) {
        this.airlineName = airlineName;
        this.flightCodes = flightCodes;
        this.firstCounter = firstCounter;
        this.counterCount = counterCount;
        this.pendingPassengers = new LinkedList<>();
    }

    public synchronized void addPassangerToQueue(Booking passanger){
        pendingPassengers.add(passanger);
    }

    public synchronized void addFlightCode(String flightCode){
        flightCodes.add(flightCode);
    }




}
