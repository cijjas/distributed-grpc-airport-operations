package ar.edu.itba.pod.tpe1.client.counter;

import ar.edu.itba.pod.tpe1.client.admin.AdminClientAction;
import ar.edu.itba.pod.tpe1.client.admin.AdminClientArguments;
import org.apache.commons.cli.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CounterClientParser {
    Options options = new Options();
    private final CommandLineParser parser  = new GnuParser();

    public CounterClientParser() {
        // Required
        Option serverAddressOption = new Option("DserverAddress", true, "Server address");
        serverAddressOption.setRequired(true);

        Option actionOption = new Option("Daction", true, "Action");
        actionOption.setRequired(true);

        options.addOption(actionOption);
        options.addOption(serverAddressOption);
        // Options
        options.addOption("Dsector",true, "Sector name");
        options.addOption("DcounterFrom",true, "Counter from");
        options.addOption("DcounterTo",true, "Counter to");
        options.addOption("Dflights",true, "Flights");
        options.addOption("Dairline",true, "Airline name");
        options.addOption("DcounterCount",true, "Counter count");

    }


    public Optional<CounterClientArguments> getCounterClientArguments(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);

            CounterClientArguments arguments = new CounterClientArguments();
            // Server address
            arguments.setServerAddress(cmd.getOptionValue("DserverAddress"));
            // Action
            arguments.setAction(CounterClientAction.valueOf(cmd.getOptionValue("Daction").toUpperCase()));
            // Sector
            arguments.setSector(cmd.getOptionValue("Dsector"));
            // Counters
            String counterFromValue = cmd.getOptionValue("DcounterFrom");
            arguments.setCounterFrom(counterFromValue != null ? Integer.parseInt(counterFromValue) : null);
            String counterToValue = cmd.getOptionValue("DcounterTo");
            arguments.setCounterTo(counterToValue != null ? Integer.parseInt(counterToValue) : null);
            // Flights
            arguments.setFlights(
                    parseFlights(cmd.getOptionValue("Dflights"))
            );
            // Airline
            arguments.setAirline(cmd.getOptionValue("Dairline"));
            // Counter count
            String counterCountValue = cmd.getOptionValue("DcounterCount");
            arguments.setCounterCount(counterCountValue != null ? Integer.parseInt(counterCountValue) : null);

            return Optional.of(arguments);
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            return Optional.empty();
        }
    }

    private List<String> parseFlights(String flights)
    {
        if (flights == null)
            return null;
        return List.of(flights.split("\\|"));
    }

}
