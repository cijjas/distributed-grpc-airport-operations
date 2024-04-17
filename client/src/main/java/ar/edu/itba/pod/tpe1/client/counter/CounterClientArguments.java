package ar.edu.itba.pod.tpe1.client.counter;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CounterClientArguments extends Arguments {

    private CounterClientAction action;
    private String sector;
    private Integer counterFrom;
    private Integer counterTo;
    private List<String> flights;
    private String airline;
    private Integer counterCount;

}
