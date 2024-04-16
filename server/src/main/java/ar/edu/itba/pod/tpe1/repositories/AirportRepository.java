package ar.edu.itba.pod.tpe1.repositories;

import ar.edu.itba.pod.tpe1.models.Booking;
import ar.edu.itba.pod.tpe1.models.BookingHist;
import ar.edu.itba.pod.tpe1.models.Counter;

import java.util.*;

public class AirportRepository {
    private Integer currentCounterCount;
    private final List<Booking> expectedPassengerList;
    private final Map<String, List<Counter>> countersInSectorsMap;
    private final List<CheckinRepository> activeCheckinList;
    private final List<BookingHist> checkedinPassengerList;

    public AirportRepository(List<Booking> expectedPassengerList, Map<String, List<Counter>> countersInSectorsMap, List<CheckinRepository> activeCheckinList, List<BookingHist> checkedinPassengerList) {
        this.expectedPassengerList = expectedPassengerList;
        this.countersInSectorsMap = countersInSectorsMap;
        this.activeCheckinList = activeCheckinList;
        this.checkedinPassengerList = checkedinPassengerList;
        this.currentCounterCount = 0;
    }

    public synchronized void addSector(String sectorName) {
        if (countersInSectorsMap.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector name already exists");
        }

        countersInSectorsMap.put(sectorName, new ArrayList<>());
    }

    public synchronized Integer addCounters(String sectorName, int counterCount) {
        if (!countersInSectorsMap.containsKey(sectorName)) {
            throw new IllegalArgumentException("Sector not found");
        } else if(counterCount <= 0){
            throw new IllegalArgumentException("Counter count must be greater than 0");
        }

        List<Counter> currentCounters = countersInSectorsMap.get(sectorName);



        for(int i = 1 ; i <= counterCount ; i++){
            currentCounters.add(new Counter(currentCounterCount+i, false));
        }

        currentCounterCount += counterCount;

        return currentCounterCount - counterCount;

    }

    public synchronized void addPassenger(Booking booking) {
        expectedPassengerList.add(booking);
    }

}
