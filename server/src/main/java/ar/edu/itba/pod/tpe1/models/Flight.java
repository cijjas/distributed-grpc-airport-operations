package ar.edu.itba.pod.tpe1.models;

import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import lombok.Getter;

@Getter
public class Flight {
    private final String flightCode;
    private String sector;
    private int counterStart;
    private CounterGroup counterGroup;
    private boolean wasAssigned;

    public Flight(String flightCode) {
        this.flightCode = flightCode;
        this.wasAssigned = false;
    }

    public void setCounterGroup(CounterGroup counterGroup) {
        this.counterGroup = counterGroup;
    }

    public void setCounterStart(int counterStart) {
        this.counterStart = counterStart;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void assign() {
        this.wasAssigned = true;
    }
}
