package ar.edu.itba.pod.tpe1.models.CounterGroup;



import java.util.List;

public record CheckinAssignment(String airlineName, List<String> flightCodes, int counterCount) {
}
