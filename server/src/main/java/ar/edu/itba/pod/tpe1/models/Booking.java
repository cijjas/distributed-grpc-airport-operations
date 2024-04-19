package ar.edu.itba.pod.tpe1.models;

import io.grpc.Status;
import lombok.Getter;

@Getter
public class Booking {
    private final String airlineName;
    private final String flightCode;
    private final String bookingCode;

    public Booking(String airlineName, String flightCode, String bookingCode) {
        this.airlineName = airlineName;
        this.flightCode = flightCode;
        this.bookingCode = bookingCode;
    }

}
