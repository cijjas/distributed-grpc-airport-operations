package ar.edu.itba.pod.tpe1.client.admin;

import ar.edu.itba.pod.tpe1.client.Arguments;

import javax.swing.text.html.Option;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class AdminClientArguments extends Arguments {


    private AdminClientAction action;
    private String sector;
    private Integer counters;
    private Path inPath;


    public AdminClientAction getAction() {
        return action;
    }

    public Optional<String> getSector() {
        return Optional.ofNullable(sector);
    }

    public Optional<Integer> getCounters() {
        return Optional.ofNullable(counters);
    }

    public Path getInPath() {
        return Optional.ofNullable(inPath).orElse(Paths.get(""));
    }

    public void setAction(AdminClientAction action) {
        this.action = action;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setCounters(Integer counters) {
        this.counters = counters;
    }

    public void setInPath(Path inPath) {
        this.inPath = inPath;
    }
}
