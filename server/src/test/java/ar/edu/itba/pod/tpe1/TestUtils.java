package ar.edu.itba.pod.tpe1;

import ar.edu.itba.pod.tpe1.models.Booking;

public class TestUtils {

    public static final String SECTOR_A = "A";
    public static final String SECTOR_B = "B";

    public static final String AIRLINE_A = "Airline A";
    public static final String AIRLINE_B = "Airline B";
    public static final String FLIGHT_CODE_1 = "ABC123";
    public static final String FLIGHT_CODE_2 = "CDE123";
    public static final String BOOKING_CODE_1 = "123123";
    public static final String BOOKING_CODE_2 = "234234";

    public static final Booking PASSENGER_A = new Booking(AIRLINE_A, FLIGHT_CODE_1, BOOKING_CODE_1);
    public static final Booking PASSENGER_B_SAME_FLIGHT = new Booking(AIRLINE_B, FLIGHT_CODE_1, BOOKING_CODE_2);
    public static final Booking PASSENGER_C_SAME_BOOKING = new Booking(AIRLINE_A, FLIGHT_CODE_2, BOOKING_CODE_1);

}
