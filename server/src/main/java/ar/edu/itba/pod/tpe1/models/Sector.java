package ar.edu.itba.pod.tpe1.models;

import ar.edu.itba.pod.tpe1.models.CounterGroup.AssignedCounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.UnassignedCounterGroup;
import lombok.Getter;

import java.util.*;

@Getter
public class Sector {
    private final Queue<CheckinAssignment> pendingAssignmentsQueue;
    private final SortedMap<Integer, CounterGroup> counterGroupMap;

    public Sector() {
        pendingAssignmentsQueue = new PriorityQueue<>();
        counterGroupMap = new TreeMap<>();
    }

    public void addCounterGroup(int firstCounter, CounterGroup newCounterGroup) {

        SortedMap<Integer, CounterGroup> headMap = counterGroupMap.headMap(firstCounter);

        if (headMap.isEmpty() ||
                headMap.get(headMap.lastKey()).isActive() ||
                headMap.lastKey() + headMap.get(headMap.lastKey()).getCounterCount() != firstCounter) {
            counterGroupMap.put(firstCounter, newCounterGroup);
        } else {
            CounterGroup prevCounterGroup = counterGroupMap.get(headMap.lastKey());
            counterGroupMap.put(headMap.lastKey(), new UnassignedCounterGroup(newCounterGroup.getCounterCount() + prevCounterGroup.getCounterCount()));
            counterGroupMap.remove(firstCounter);
            firstCounter = headMap.lastKey();
            newCounterGroup.setCounterCount(newCounterGroup.getCounterCount() + prevCounterGroup.getCounterCount());
            System.out.println("Merged");
        }

        SortedMap<Integer, CounterGroup> tailMap = counterGroupMap.tailMap(firstCounter + newCounterGroup.getCounterCount());

        if (!tailMap.isEmpty() &&
                !tailMap.get(tailMap.firstKey()).isActive() &&
                tailMap.firstKey() == firstCounter + newCounterGroup.getCounterCount()) {
            CounterGroup nextCounterGroup = counterGroupMap.get(tailMap.firstKey());
            counterGroupMap.put(firstCounter, new UnassignedCounterGroup(newCounterGroup.getCounterCount() + nextCounterGroup.getCounterCount()));
            counterGroupMap.remove(tailMap.firstKey());
            System.out.println("Merged");
        }
    }

    public boolean assignCounterGroup(CheckinAssignment checkinAssignment) {
        for (Map.Entry<Integer, CounterGroup> entry : counterGroupMap.entrySet()) {
            if (entry.getValue().getCounterCount() >= checkinAssignment.counterCount() && !entry.getValue().isActive()) {
                splitUnoccupiedCounter(entry.getKey(), checkinAssignment.counterCount());
                counterGroupMap.put(entry.getKey(), new AssignedCounterGroup(checkinAssignment));
                return true;
            }
        }

        pendingAssignmentsQueue.add(checkinAssignment);
        return false;    // TODO: change

    }

    private CounterGroup splitUnoccupiedCounter(int firstCount, int counterGroupSize) {
        CounterGroup leftCounterGroup = counterGroupMap.get(firstCount);
        int prevSize = leftCounterGroup.getCounterCount();
        leftCounterGroup.setCounterCount(counterGroupSize);

        counterGroupMap.put(firstCount + counterGroupSize, new UnassignedCounterGroup(prevSize - counterGroupSize));

        return leftCounterGroup;
    }

    public SortedMap<Integer, Integer> listGroupedCounters() {
        SortedMap<Integer, Integer> returnMap = new TreeMap<>();
        Integer prevKey = null;
        int prevCount = 0;
        for (Map.Entry<Integer, CounterGroup> entry : counterGroupMap.entrySet()) {
            if (prevKey != null && prevKey + returnMap.get(prevKey) == entry.getKey()) {
                prevCount += entry.getValue().getCounterCount();
                returnMap.put(prevKey, prevCount);
            } else {
                returnMap.put(entry.getKey(), entry.getValue().getCounterCount());
                prevKey = entry.getKey();
                prevCount = entry.getValue().getCounterCount();
            }
        }

        return returnMap;
    }

    public void freeCounters(String airlineName, int counterFrom) {
        CounterGroup counterGroup = counterGroupMap.get(counterFrom);

        if (counterGroup == null || !counterGroup.isActive()) {
            throw new IllegalArgumentException("No counter groups start on counter " + counterFrom + " in requested sector");
        }

        if (!counterGroup.getAirlineName().equals(airlineName)) {
            throw new IllegalArgumentException("Counter does not correspond to requested airline");
        }

        if (!counterGroup.getPendingPassengers().isEmpty()) {
            throw new IllegalArgumentException("Cannot free counters, there are passengers in line");
        }

        addCounterGroup(counterFrom, new UnassignedCounterGroup(counterGroup.getCounterCount()));
    }
}
