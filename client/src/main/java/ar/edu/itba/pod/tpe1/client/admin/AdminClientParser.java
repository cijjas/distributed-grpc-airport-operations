package ar.edu.itba.pod.tpe1.client.admin;


import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
//-DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Dsector=sectorName | -Dcounters=counterCount | -DinPath=manifestPath ]

public class AdminClientParser {
    Options options = new Options();
    private final CommandLineParser parser  = new GnuParser();

    public AdminClientParser() {
        // Required
        OptionGroup requiredAddress = new OptionGroup();
        requiredAddress.addOption(new Option("DserverAddress", "DserverAddress", true, "Server address"));
        requiredAddress.setRequired(true);

        OptionGroup requiredAction = new OptionGroup();
        requiredAction.addOption(new Option("Daction", "Daction", true, "Action"));
        requiredAction.setRequired(true);

        options.addOptionGroup(requiredAction);
        options.addOptionGroup(requiredAddress);
        // Options
        options.addOption("Dsector","Dsector",  true, "Sector name");
        options.addOption("Dcounters","Dcounters",  true, "Counter count");
        options.addOption("DinPath","DinPath",  true, "Manifest path");
    }


    public Optional<AdminClientArguments> getAdminClientArguments(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);

            AdminClientArguments arguments = new AdminClientArguments();
            // Server address
            arguments.setServerAddress(cmd.getOptionValue("DserverAddress"));
            // Action
            arguments.setAction(AdminClientAction.valueOf(cmd.getOptionValue("Daction").toUpperCase()));
            // Sector
            arguments.setSector(cmd.getOptionValue("Dsector"));
            // Counters
            String countersValue = cmd.getOptionValue("Dcounters");
            arguments.setCounters(countersValue != null ? Integer.parseInt(countersValue) : null);
            // Manifest path
            String inPathValue = cmd.getOptionValue("DinPath");
            arguments.setInPath(inPathValue != null ? Paths.get(inPathValue) : null);

            return Optional.of(arguments);
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            return Optional.empty();
        }
    }




}
