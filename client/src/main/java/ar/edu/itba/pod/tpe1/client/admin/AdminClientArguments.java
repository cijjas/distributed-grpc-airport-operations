package ar.edu.itba.pod.tpe1.client.admin;

import ar.edu.itba.pod.tpe1.client.Arguments;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Getter
@Setter
public class AdminClientArguments extends Arguments {


    private AdminClientAction action;
    private String sector;
    private Integer counters;
    private Path inPath;



}
