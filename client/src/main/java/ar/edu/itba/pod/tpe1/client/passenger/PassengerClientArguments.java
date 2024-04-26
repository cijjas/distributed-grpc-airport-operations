package ar.edu.itba.pod.tpe1.client.passenger;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
public class PassengerClientArguments extends Arguments {
    @Getter
    private PassengerClientAction action;
    private String booking;
    private String sector;
    private Integer counterNumber;

    public Optional<String> getBooking() {
        return Optional.ofNullable(booking);
    }
    public Optional<String> getSector() {
        return Optional.ofNullable(sector);
    }
    public Optional<Integer> getCounterNumber() {
        return Optional.ofNullable(counterNumber);
    }

}
