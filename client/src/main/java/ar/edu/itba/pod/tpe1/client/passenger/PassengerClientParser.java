package ar.edu.itba.pod.tpe1.client.passenger;

import org.apache.commons.cli.*;

import java.util.Optional;

public class PassengerClientParser {
    Options options = new Options();
    private final CommandLineParser parser  = new GnuParser();

    public PassengerClientParser() {
        // Required
        Option serverAddressOption = new Option("DserverAddress", true, "Server address");
        serverAddressOption.setRequired(true);

        Option actionOption = new Option("Daction", true, "Action");
        actionOption.setRequired(true);

        options.addOption(actionOption);
        options.addOption(serverAddressOption);
        // Options
        options.addOption("Dbooking",true, "Booking id");
        options.addOption("Dsector",true, "Sector name");
        options.addOption("Dcounter",true, "Counter number");
    }


    public Optional<PassengerClientArguments> getPassengerClientArguments(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);

            PassengerClientArguments arguments = new PassengerClientArguments();
            // Server address
            arguments.setServerAddress(cmd.getOptionValue("DserverAddress"));
            // Action
            arguments.setAction(PassengerClientAction.valueOf(cmd.getOptionValue("Daction").toUpperCase()));
            // Booking
            arguments.setBooking(cmd.getOptionValue("Dbooking"));
            // Sector
            arguments.setSector(cmd.getOptionValue("Dsector"));
            // Counter
            String countersValue = cmd.getOptionValue("Dcounter");
            arguments.setCounter(countersValue != null ? Integer.parseInt(countersValue) : null);

            return Optional.of(arguments);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

}
