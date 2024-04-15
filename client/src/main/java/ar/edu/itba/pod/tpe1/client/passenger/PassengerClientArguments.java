package ar.edu.itba.pod.tpe1.client.passenger;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Setter;

@Setter
public class PassengerClientArguments extends Arguments {

    private PassengerClientAction action;
    private String booking;
    private String sector;
    private Integer counterNumber;

}
