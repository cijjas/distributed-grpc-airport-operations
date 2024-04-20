package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import ar.edu.itba.pod.tpe1.models.CounterGroup.AssignedCounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.Sector;

import java.util.*;
import java.util.stream.Collectors;

public class NotificationRepository {
    private final Map<String, Queue<String>> registeredAirlines;

    private final AirportRepository airportRepository;

    public NotificationRepository(AirportRepository airportRepository) {
        this.registeredAirlines = new HashMap<>();
        this.airportRepository = airportRepository;
    }

    public void registerAirline(String airlineName) {
        if(registeredAirlines.containsKey(airlineName)) {
            throw new IllegalArgumentException("Airline Already Registered");
        }
        if(!airportRepository.hasPendingPassenger(airlineName)){
            throw new IllegalArgumentException("No passengers for airline");
        }

        registeredAirlines.put(airlineName, new LinkedList<>());
    }

    public boolean isAirlineRegistered(String airlineName){
        return registeredAirlines.containsKey(airlineName);
    }

    public boolean hasNewNotifications(String airlineName){
        return registeredAirlines.containsKey(airlineName) && !registeredAirlines.get(airlineName).isEmpty();
    }
    public void unregisterAirline(String airlineName) {
        if(!registeredAirlines.containsKey(airlineName))
            throw new IllegalArgumentException("Airline Not Registered");

        this.registeredAirlines.remove(airlineName);
    }

    private void addNotification(String airlineName, String notification){
        registeredAirlines.get(airlineName).add(notification);
    }


    public List<String> getLatestNotifications(String airlineName){
        if(!registeredAirlines.containsKey(airlineName))
            throw new IllegalArgumentException("Airline Not Registered");

        List<String> toRet = new ArrayList<>();
        toRet.addAll(registeredAirlines.get(airlineName));
        registeredAirlines.get(airlineName).clear();

        return toRet;
    }



    public void addCheckinStartedNotification(String airlineName, AssignedCounterGroup group, int firstCounter, String sector){
        //2 counters (3-4) in Sector C are now checking in passengers from AmericanAirlines AA123|AA124|AA125 flights
        //make it with a string builder
        if(registeredAirlines.containsKey(airlineName)) {
            StringBuilder sb = new StringBuilder();
            sb.append(group.getCounterCount())
                    .append(" counters (")
                    .append(firstCounter)
                    .append("-")
                    .append(firstCounter + group.getCounterCount() - 1)
                    .append(") in Sector ")
                    .append(sector)
                    .append(" are now checking in passengers from ")
                    .append(airlineName).append(" ")
                    .append(group.getFlightCodes().stream().collect(Collectors.joining("|")))
                    .append(" flights");
            addNotification(airlineName, sb.toString());
        }

    }
    public void addPassengerInQueueNotification(Booking booking, AssignedCounterGroup group, int firstCounter, int counterCount, String sector){
        //Booking ABC123 for flight AA123 from AmericanAirlines is now waiting to check-in on counters (2-4) in Sector C with 6 people in line
        //make it with string builder
        if(registeredAirlines.containsKey(booking.getAirlineName())) {
            StringBuilder sb = new StringBuilder();
            sb.append("Booking ")
                    .append(booking.getBookingCode())
                    .append(" for flight ")
                    .append(booking.getFlightCode())
                    .append(" from ")
                    .append(booking.getAirlineName())
                    .append(" is now waiting to check-in on counters (")
                    .append(firstCounter)
                    .append("-")
                    .append(firstCounter + counterCount - 1)
                    .append(") in Sector ")
                    .append(sector)
                    .append(" with ")
                    .append(group.getPendingPassengers().size())
                    .append(" people in line");
            addNotification(booking.getAirlineName(), sb.toString());
        }

    }
    public void addPassengerSuccessfulNotification(BookingHist booking, String sector){
        //Check-in successful of XYZ345 for flight AA123 at counter 3 in Sector C
        //make it with string builder
        if(registeredAirlines.containsKey(booking.getAirlineName())) {
            StringBuilder sb = new StringBuilder();
            sb.append("Check-in successful of ")
                    .append(booking.getBookingCode())
                    .append(" for flight ")
                    .append(booking.getFlightCode())
                    .append(" at counter ")
                    .append(booking.getCheckinCounter())
                    .append(" in Sector ")
                    .append(sector);

            addNotification(booking.getAirlineName(), sector);
        }
    }
    public void addCheckinEndedNotification(String airlineName, AssignedCounterGroup group, String sector){
        //Ended check-in for flights AA123|AA124|AA125 on counters (2-4) from Sector C
        //make it with string builder
        if(registeredAirlines.containsKey(airlineName)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Ended check-in for flights ")
                    .append(group.getFlightCodes().stream().collect(Collectors.joining("|")))
                    .append(" on counters (")
                    .append(group.getCounterCount())
                    .append(") from Sector ")
                    .append(sector);
        }
    }
    public void addPendingAssignmentNotification(String airlineName, CheckinAssignment assignment, Sector sector){
        //7 counters in Sector C for flights AA888|AA999 is pending with 5 other pendings ahead
        //make it with string builder
        if(registeredAirlines.containsKey(airlineName)) {
            StringBuilder sb = new StringBuilder();
            sb.append(assignment.counterCount())
                    .append(" counters in Sector ")
                    .append(sector.getName())
                    .append(" for flights ")
                    .append(assignment.flightCodes().stream().collect(Collectors.joining("|")))
                    .append(" is pending with ")
                    .append(sector.listPendingAssignments().size())
                    .append(" other pendings ahead");
        }

    }

}
