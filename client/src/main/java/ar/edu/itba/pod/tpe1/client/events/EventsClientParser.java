package ar.edu.itba.pod.tpe1.client.events;

import org.apache.commons.cli.*;

import java.util.List;
import java.util.Optional;

public class EventsClientParser {
    Options options = new Options();
    private final CommandLineParser parser  = new GnuParser();

    public EventsClientParser() {
        // Required
        Option serverAddressOption = new Option("DserverAddress", true, "Server address");
        serverAddressOption.setRequired(true);

        Option actionOption = new Option("Daction", true, "Action");
        actionOption.setRequired(true);

        options.addOption(actionOption);
        options.addOption(serverAddressOption);

        // Options
        options.addOption("Dairline",true, "Airline name");

    }


    public Optional<EventsClientArguments> getEventsClientArguments(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);

            EventsClientArguments arguments = new EventsClientArguments();
            // Server address
            arguments.setServerAddress(cmd.getOptionValue("DserverAddress"));
            // Action
            arguments.setAction(EventsClientAction.valueOf(cmd.getOptionValue("Daction").toUpperCase()));
            // Airline
            arguments.setAirline(cmd.getOptionValue("Dairline"));

            return Optional.of(arguments);
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            return Optional.empty();
        }
    }

}
