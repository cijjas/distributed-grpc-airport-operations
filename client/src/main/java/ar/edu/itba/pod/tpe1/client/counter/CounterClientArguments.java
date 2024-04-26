package ar.edu.itba.pod.tpe1.client.counter;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Setter
public class CounterClientArguments extends Arguments {
    @Getter
    private CounterClientAction action;
    private String sector;
    private Integer counterFrom;
    private Integer counterTo;
    private List<String> flights;
    private String airline;
    private Integer counterCount;

    public Optional<String> getSector() {
        return Optional.ofNullable(sector);
    }

    public Optional<Integer> getCounterFrom() {
        return Optional.ofNullable(counterFrom);
    }

    public Optional<Integer> getCounterTo() {
        return Optional.ofNullable(counterTo);
    }

    public Optional<List<String>> getFlights() {
        return Optional.ofNullable(flights);
    }

    public Optional<String> getAirline() {
        return Optional.ofNullable(airline);
    }

    public Optional<Integer> getCounterCount() {
        return Optional.ofNullable(counterCount);
    }


}
