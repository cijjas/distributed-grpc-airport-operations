package ar.edu.itba.pod.tpe1.client.admin;


import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

public class AdminClientParser {
    Options options = new Options();
    private final CommandLineParser parser  = new GnuParser();

    public AdminClientParser() {
        // Required

        Option serverAddressOption = new Option("DserverAddress", true, "Server address");
        serverAddressOption.setRequired(true);

        Option actionOption = new Option("Daction", true, "Action");
        actionOption.setRequired(true);

        options.addOption(actionOption);
        options.addOption(serverAddressOption);
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
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }




}
