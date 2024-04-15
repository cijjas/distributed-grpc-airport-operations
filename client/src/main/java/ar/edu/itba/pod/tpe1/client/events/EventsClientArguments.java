package ar.edu.itba.pod.tpe1.client.events;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Setter;

import java.util.List;

@Setter
public class EventsClientArguments extends Arguments {

    private EventsClientAction action;
    private String airline;

}
