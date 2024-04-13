package ar.edu.itba.pod.tpe1.client;

import org.checkerframework.checker.units.qual.A;

import java.awt.print.Book;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVParserService {

    public static void parseCSV(String filePath) {
        if(filePath != null){
            try {
                Files.lines(Paths.get(filePath))
                    .skip(1)
                    .forEach(line -> {
                        String[] parts = line.split(";");
                        if (parts.length == 3) {
                            String bookingId = parts[0].trim();
                            String flightId = parts[1].trim();
                            String airline = parts[2].trim();
                        }
                    });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
