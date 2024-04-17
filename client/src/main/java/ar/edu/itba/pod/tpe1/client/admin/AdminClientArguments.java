package ar.edu.itba.pod.tpe1.client.admin;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Setter
public class AdminClientArguments extends Arguments {


    @Getter
    private AdminClientAction action;
    private String sector;
    private Integer counters;
    private Path inPath;


    public Optional<String> getSector() {
        return Optional.ofNullable(sector);
    }

    public Optional<Integer> getCounters() {
        return Optional.ofNullable(counters);
    }

    public Optional<Path> getInPath() {
        return Optional.of(inPath);
    }

}
