package ar.edu.itba.pod.tpe1.models;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;
import ar.edu.itba.pod.tpe1.models.Booking.BookingHist;
import ar.edu.itba.pod.tpe1.models.CounterGroup.AssignedCounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CheckinAssignment;
import ar.edu.itba.pod.tpe1.models.CounterGroup.CounterGroup;
import ar.edu.itba.pod.tpe1.models.CounterGroup.UnassignedCounterGroup;
import ar.edu.itba.pod.tpe1.repositories.AirlineRepository;
import ar.edu.itba.pod.tpe1.servants.EventsServant;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@Getter
public class Sector {
    private final CopyOnWriteArrayList<CheckinAssignment> pendingAssignmentsList;
    private final Object pendingAssignmentsLock = new Object();

    private final SortedMap<Integer, CounterGroup> counterGroupMap;
    private final Object counterGroupMapLock = new Object();

    private final AirlineRepository airlineRepository;

    private final String name;

    public Sector(String name, AirlineRepository airlineRepository) {
        pendingAssignmentsList = new CopyOnWriteArrayList<>();
        counterGroupMap = new TreeMap<>();
        this.name = name;
        this.airlineRepository = airlineRepository;
    }

    public CounterGroup fetchCounter(String flightCode) {
        for (CounterGroup group : counterGroupMap.values())
            if (group.containsFlightCode(flightCode)) return group;
        return null;
    }

    public void addCounterGroup(int firstCounter, CounterGroup newCounterGroup, EventsServant eventsServant) {
        synchronized (counterGroupMapLock) {
            SortedMap<Integer, CounterGroup> headMap = counterGroupMap.headMap(firstCounter);

            if (headMap.isEmpty() || headMap.get(headMap.lastKey()).isActive() || headMap.lastKey() + headMap.get(headMap.lastKey()).getCounterCount() != firstCounter) {
                counterGroupMap.put(firstCounter, newCounterGroup);
            } else {
                CounterGroup prevCounterGroup = counterGroupMap.get(headMap.lastKey());
                counterGroupMap.put(headMap.lastKey(), new UnassignedCounterGroup(headMap.lastKey(), newCounterGroup.getCounterCount() + prevCounterGroup.getCounterCount()));
                counterGroupMap.remove(firstCounter);
                firstCounter = headMap.lastKey();
                newCounterGroup.setCounterCount(newCounterGroup.getCounterCount() + prevCounterGroup.getCounterCount());
            }

            SortedMap<Integer, CounterGroup> tailMap = counterGroupMap.tailMap(firstCounter + newCounterGroup.getCounterCount());

            if (!tailMap.isEmpty() && !tailMap.get(tailMap.firstKey()).isActive() && tailMap.firstKey() == firstCounter + newCounterGroup.getCounterCount()) {
                CounterGroup nextCounterGroup = counterGroupMap.get(tailMap.firstKey());
                counterGroupMap.put(firstCounter, new UnassignedCounterGroup(firstCounter, newCounterGroup.getCounterCount() + nextCounterGroup.getCounterCount()));
                counterGroupMap.remove(tailMap.firstKey());
            }

            assignCounterGroupForPendingAssignments(eventsServant);
        }
    }

    public Pair<Boolean, Integer> assignCounterGroupOrEnqueue(CheckinAssignment checkinAssignment, boolean addIfNoSpace) {
        synchronized (counterGroupMapLock) {
            for (Map.Entry<Integer, CounterGroup> entry : counterGroupMap.entrySet()) {
                if (entry.getValue().getCounterCount() >= checkinAssignment.counterCount() && !entry.getValue().isActive()) {
                    splitUnoccupiedCounter(entry.getKey(), checkinAssignment.counterCount());
                    AssignedCounterGroup assignedCounterGroup = new AssignedCounterGroup(checkinAssignment, entry.getKey());
                    counterGroupMap.put(entry.getKey(), assignedCounterGroup);
                    airlineRepository.assignCountersToFlights(name, entry.getKey(), assignedCounterGroup);
                    return new Pair<>(true, entry.getKey());
                }
            }
        }

        if (addIfNoSpace) pendingAssignmentsList.add(checkinAssignment);
        return new Pair<>(false, pendingAssignmentsList.size() - 1);

    }

    public Pair<Boolean, Integer> assignCounterGroup(CheckinAssignment checkinAssignment) {
        return assignCounterGroupOrEnqueue(checkinAssignment, true);
    }


    public void assignCounterGroupForPendingAssignments(EventsServant eventsServant) {
        synchronized (counterGroupMapLock) {
            Integer minCountNotAdded = null;
            boolean removed = false;
            for (CheckinAssignment checkinAssignment : pendingAssignmentsList) {
                if ((minCountNotAdded == null || minCountNotAdded > checkinAssignment.counterCount())) {
                    Pair<Boolean, Integer> assignedData = assignCounterGroupOrEnqueue(checkinAssignment, false);
                    if (assignedData.getLeft()) {
                        pendingAssignmentsList.remove(checkinAssignment);
                        minCountNotAdded = checkinAssignment.counterCount();
                        removed = true;
                        if (eventsServant != null) {
                            eventsServant.notify(checkinAssignment.airlineName(), String.format("%d counters (%d-%d) in Sector %s are now checking in passengers from %s %s flights.",
                                    checkinAssignment.counterCount(), assignedData.getRight(), assignedData.getRight() + checkinAssignment.counterCount() - 1, name, checkinAssignment.airlineName(), String.join("|", checkinAssignment.flightCodes())));
                        }
                    }
                }
            }

            if (removed) {
                IntStream.range(0, pendingAssignmentsList.size()).forEach(i -> {
                    CheckinAssignment checkinAssignment = pendingAssignmentsList.get(i);
                    eventsServant.notify(checkinAssignment.airlineName(), String.format("%d counters in Sector %s is pending with %d other pendings ahead.",
                            checkinAssignment.counterCount(), name, i));
                });
            }
        }
    }


    private void splitUnoccupiedCounter(int firstCount, int counterGroupSize) {
        CounterGroup leftCounterGroup = counterGroupMap.get(firstCount);
        int prevSize = leftCounterGroup.getCounterCount();
        leftCounterGroup.setCounterCount(counterGroupSize);

        if (prevSize == counterGroupSize) return;
        counterGroupMap.put(firstCount + counterGroupSize, new UnassignedCounterGroup(firstCount + counterGroupSize, prevSize - counterGroupSize));
    }

    public SortedMap<Integer, Integer> listGroupedCounters() {
        synchronized (counterGroupMapLock) {
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
    }

    public CounterGroup freeCounters(String airlineName, int counterFrom, EventsServant eventsServant) {
        synchronized (counterGroupMapLock) {
            CounterGroup counterGroup = counterGroupMap.get(counterFrom);

            if (counterGroup == null || !counterGroup.isActive()) {
                throw new IllegalArgumentException("No counter groups start on counter " + counterFrom + " in requested sector");
            }

            if (!counterGroup.getAirlineName().equals(airlineName)) {
                throw new IllegalArgumentException("Counter does not correspond to requested airline");
            }

            synchronized (pendingAssignmentsLock) {
                if (!counterGroup.getPendingPassengers().isEmpty()) {
                    throw new IllegalArgumentException("Cannot free counters, there are passengers in line");
                }

                addCounterGroup(counterFrom, new UnassignedCounterGroup(counterFrom, counterGroup.getCounterCount()), eventsServant);
                return counterGroup;
            }
        }
    }

    public List<BookingHist> checkinCounters(int counterFrom, String airlineName) {
        synchronized (counterGroupMapLock) {
            CounterGroup counterGroup = counterGroupMap.get(counterFrom);

            if (counterGroup == null || !counterGroup.isActive()) {
                throw new IllegalArgumentException("No counter groups start on counter " + counterFrom + " in requested sector");
            }

            if (!counterGroup.getAirlineName().equals(airlineName)) {
                throw new IllegalArgumentException("Counter does not correspond to requested airline");
            }

            return counterGroup.checkinCounters().stream().peek(bookingHist -> bookingHist.setCheckinCounter(bookingHist.getCheckinCounter() + counterFrom)).toList();
        }
    }

    public List<CheckinAssignment> listPendingAssignments() {
        return new ArrayList<>(pendingAssignmentsList);
    }

    public CounterGroup passengerCheckin(Booking booking, int fromCounter) {
        synchronized (counterGroupMapLock) {
            if (!counterGroupMap.containsKey(fromCounter)) throw new IllegalArgumentException("Invalid counter start");
            CounterGroup group = counterGroupMap.get(fromCounter);

            if (!group.containsFlightCode(booking.getFlightCode()))
                throw new IllegalArgumentException("Invalid counter start for flight");

            synchronized (pendingAssignmentsLock) {
                group.addPendingPassenger(booking);
                return group;
            }
        }
    }

    public SortedMap<Integer, CounterGroup> listCounters(int fromVal, int toVal) {
        return counterGroupMap.headMap(toVal + 1).tailMap(fromVal - 1);
    }

}
