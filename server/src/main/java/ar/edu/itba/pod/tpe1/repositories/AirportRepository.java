package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.*;
import ar.edu.itba.pod.tpe1.models.CounterGroup.AssignedCounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.UnassignedCounterGroup;

import java.util.*;

public class AirportRepository {
    private final Map<String, Booking> expectedPassengerList;
    private final List<BookingHist> checkedinPassengerList;

    private final SortedMap<String, Sector> sectors;

    private final Map<String, List<String>> airlineFlightCodes;

    private int nextAvailableCounter;

    public AirportRepository(Map<String, Booking> expectedPassengerList, List<BookingHist> checkedinPassengerList, Map<String, List<String>> airlineFlightCodes) {
        this.expectedPassengerList = expectedPassengerList;
        this.checkedinPassengerList = checkedinPassengerList;
        this.airlineFlightCodes = airlineFlightCodes;
        this.sectors = new TreeMap<>();
        nextAvailableCounter = 1;
    }

    public synchronized void addSector(String sectorName) {
        if (sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector name already exists");
        }

        sectors.put(sectorName, new Sector());
    }

    public synchronized int addCounters(String sectorName, int counterCount) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        if (counterCount <= 0) {
            throw new IllegalArgumentException("Counter count must be greater than 0");
        }

        Sector sector = sectors.get(sectorName);
        sector.addCounterGroup(nextAvailableCounter, new UnassignedCounterGroup(counterCount));

        nextAvailableCounter += counterCount;
        return nextAvailableCounter;
    }

    // TODO: solo va a funcionar con los expectedPassengers (y no los que ya pasaron) con esta impl
    public synchronized void addPassenger(Booking booking) {
        if (expectedPassengerList.containsKey(booking.getBookingCode()))
            throw new IllegalArgumentException("Booking with code " + booking.getBookingCode() + " already exists");

        if(!airlineFlightCodes.containsKey(booking.getAirlineName()))
            airlineFlightCodes.put(booking.getAirlineName(), new ArrayList<>());
        airlineFlightCodes.get(booking.getAirlineName()).add(booking.getFlightCode());

        expectedPassengerList.put(booking.getBookingCode(), booking);
    }

    public SortedMap<String, SortedMap<Integer, Integer>> listSectors() {
        SortedMap<String, SortedMap<Integer, Integer>> mappedSectors = new TreeMap<>();

        if (sectors.isEmpty()) {
            throw new IllegalStateException("No sectors registered");
        }

        for (Map.Entry<String, Sector> entry : sectors.entrySet()) {
            mappedSectors.put(entry.getKey(), entry.getValue().listGroupedCounters());
        }

        return mappedSectors;
    }

    // TODO: fromVal and toVal (teniendo 2-5, si arranca en 2 y me piden del 3, lo muestro?)
    public SortedMap<Integer, CounterGroup> listCounters(String sectorName) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        return sectors.get(sectorName).getCounterGroupMap();
    }

    public void assignCounters(String sectorName, String airlineName, List<String> flightCodes, int counterCount) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        CheckinAssignment checkinAssignment = new CheckinAssignment(airlineName, flightCodes, counterCount);

        sectors.get(sectorName).assignCounterGroup(checkinAssignment);
    }


    public void freeCounters(String sectorName, String airlineName, int counterFrom) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        sectors.get(sectorName).freeCounters(airlineName, counterFrom);
    }

    public List<BookingHist> checkInCounters(String sectorName, int counterFrom, String airlineName) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }
        List<BookingHist> toRet = sectors.get(sectorName).checkinCounters(counterFrom, airlineName);
        checkedinPassengerList.addAll(toRet.stream().filter(b -> b.getAirlineName() != null).toList());
        return toRet;
    }


    public List<CheckinAssignment> listPendingAssignments(String sectorName) {
        if (!sectors.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        }

        return sectors.get(sectorName).listPendingAssignments();
    }

    /*
        Falla si:
            No existe un pasajero esperado con ese código de reserva

        Funcionalidad:
            Consultar el rango de mostradores asignado para realizar el
            check-in de un vuelo a partir del código de reserva booking,
            indicando la cantidad de pasajeros esperando en la cola de
            ese rango y el sector en donde se encuentra.
     */
    //WARNING: Asquerosamente funcional.
    public CounterGroup fetchCounter(String bookingCode){
        if(!expectedPassengerList.containsKey(bookingCode))
            throw new IllegalArgumentException("Booking code not found");

        Booking booking = expectedPassengerList.get(bookingCode);
        CounterGroup curr = null;
        for(Sector sector : sectors.values()) {
            curr = sector.fetchCounter(booking.getFlightCode());
            if (curr != null)
                return curr;
        }
        return null;
    }

    /*
        Funcionalidad:
            Ingresar a la cola de un rango de mostradores para realizar el check-in de un vuelo
            a partir del número de mostrador counterNumber correspondiente al inicio del rango, el
            nombre del sector sectorName y el código de reserva booking, indicando además la
            cantidad de pasajeros esperando en la cola de ese rango
        Falla si:
            No existe una reserva con ese código
            No existe un sector con ese nombre
            El número de mostrador no corresponde con el inicio de un rango de mostradores asignado a la aerolínea que esté aceptando pasajeros del vuelo de la reserva
            El pasajero ya ingresó en la cola del rango
            El pasajero ya realizó el check-in de la reserva
     */
    public CounterGroup passengerCheckin(String bookingCode, String sectorName, int counterFrom){
        if(!expectedPassengerList.containsKey(bookingCode))
             throw new IllegalArgumentException("Booking code not found or user checked-in");

        if(!sectors.containsKey(sectorName))
            throw new IllegalArgumentException("Sector not found");

        Booking booking = expectedPassengerList.get(bookingCode);
        CounterGroup toRet = sectors.get(sectorName).passengerCheckin(booking, counterFrom);
        expectedPassengerList.remove(bookingCode);
        return toRet;
    }

    /*
        Falla si:
            No existe un pasajero esperado con ese código de reserva
            No hay un rango de mostradores asignados que atiendan pasajeros del vuelo correspondiente al código de reserva indicado
     */
//    public void passengerStatus(String bookingCode){
//
//    }


}
