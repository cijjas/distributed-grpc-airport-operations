package ar.edu.itba.pod.tpe1.models.FlightStatus;

import ar.edu.itba.pod.grpc.FlightStatus;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import lombok.Getter;

@Getter
public class FlightStatusInfo {
    private final FlightStatus flightStatus;
    private final String airlineName;
    private final String flightCode;
    private final String sectorName;
    private final CounterGroup counterGroup;

    public FlightStatusInfo(FlightStatus flightStatus, String airlineName, String flightCode, String sectorName, CounterGroup counterGroup) {
        this.flightStatus = flightStatus;
        this.airlineName = airlineName;
        this.flightCode = flightCode;
        this.sectorName = sectorName;
        this.counterGroup = counterGroup;
    }

    public FlightStatusInfo(FlightStatus flightStatus, String airlineName, String flightCode) {
        this.flightStatus = flightStatus;
        this.airlineName = airlineName;
        this.flightCode = flightCode;
        this.sectorName = null;
        this.counterGroup = null;
    }
}
