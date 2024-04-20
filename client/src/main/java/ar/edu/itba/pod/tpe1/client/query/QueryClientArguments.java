package ar.edu.itba.pod.tpe1.client.query;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Setter
@Getter
public class QueryClientArguments extends Arguments {

    private QueryClientAction action;
    private String sector;
    private String airline;
    private Path outPath;

}
