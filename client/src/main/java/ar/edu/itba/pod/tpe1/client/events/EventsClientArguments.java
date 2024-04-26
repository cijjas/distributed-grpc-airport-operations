package ar.edu.itba.pod.tpe1.client.events;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Setter
public class EventsClientArguments extends Arguments {
    @Getter
    private EventsClientAction action;
    private String airline;

    public Optional<String> getAirline() {
        return Optional.ofNullable(airline);
    }

}
