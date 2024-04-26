package ar.edu.itba.pod.tpe1.client.query;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Getter;
import lombok.Setter;

import javax.swing.text.html.Option;
import java.nio.file.Path;
import java.util.Optional;

@Setter
public class QueryClientArguments extends Arguments {
    @Getter
    private QueryClientAction action;
    private String sector;
    private String airline;
    private Path outPath;

    public Optional<String> getSector() {
        return Optional.ofNullable(sector);
    }

    public Optional<String> getAirline() {
        return Optional.ofNullable(airline);
    }

    public Optional<Path> getOutPath() {
        return Optional.ofNullable(outPath);
    }


}
