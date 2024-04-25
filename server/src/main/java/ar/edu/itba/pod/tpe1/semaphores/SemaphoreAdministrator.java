package ar.edu.itba.pod.tpe1.semaphores;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SemaphoreAdministrator {
    private final ConcurrentSkipListMap<String, ReadWriteLock> bookingReadWriteLockMap;
    private final ConcurrentSkipListMap<String, ReadWriteLock> flightCodeReadWriteLockMap;
    private final ConcurrentSkipListMap<String, ReadWriteLock> sectorReadWriteLockMap;
    public SemaphoreAdministrator() {
        this.bookingReadWriteLockMap = new ConcurrentSkipListMap<>();
        this.flightCodeReadWriteLockMap = new ConcurrentSkipListMap<>();
        this.sectorReadWriteLockMap = new ConcurrentSkipListMap<>();
    }

    // BOOKING

    private synchronized ReadWriteLock addBookingLock(String bookingCode) {
        bookingReadWriteLockMap.put(bookingCode, new ReentrantReadWriteLock());
        return bookingReadWriteLockMap.get(bookingCode);
    }

    private ReadWriteLock getBookingLock(String bookingCode) {
        return bookingReadWriteLockMap.get(bookingCode);
    }

    // FLIGHTCODE
    public synchronized ReadWriteLock addFlightCodeLock(String flightCode) {
        flightCodeReadWriteLockMap.put(flightCode, new ReentrantReadWriteLock());
        return flightCodeReadWriteLockMap.get(flightCode);
    }

    private ReadWriteLock getFlightCodeLock(String flightCode) {
        return flightCodeReadWriteLockMap.get(flightCode);
    }

    // SECTOR
    public synchronized void addSectorLock(String sectorName) {
        if (sectorReadWriteLockMap.get(sectorName) != null)
            throw new IllegalStateException("Sector lock already exists");

        sectorReadWriteLockMap.put(sectorName, new ReentrantReadWriteLock());
    }

    private ReadWriteLock getSectorLock(String sectorName){
        return sectorReadWriteLockMap.get(sectorName);
    }

    private void lockSector(String sectorName){

    }

    // BOOKING AND FLIGHTCODE
    public void addAndWriteLockBookingAndFlightCode(Booking booking) {
        ReadWriteLock bookingLock = addBookingLock(booking.getBookingCode());
        ReadWriteLock flightCodeLock = addFlightCodeLock(booking.getFlightCode());

        bookingLock.writeLock().lock();
        flightCodeLock.writeLock().lock();
    }

    public void writeUnlockBookingAndFlightCode(Booking booking) {
        ReadWriteLock bookingLock = getBookingLock(booking.getBookingCode());
        ReadWriteLock flightCodeLock = getFlightCodeLock(booking.getFlightCode());

        bookingLock.writeLock().unlock();
        flightCodeLock.writeLock().unlock();
    }

    // BOOKING

    public void writeLockBooking(String bookingCode) {
        ReadWriteLock bookingLock = getBookingLock(bookingCode);

        if (bookingLock != null)
            bookingLock.writeLock().lock();
    }

    public void writeUnlockBooking(String bookingCode) {
        ReadWriteLock bookingLock = getBookingLock(bookingCode);

        if (bookingLock != null)
            bookingLock.writeLock().unlock();
    }

    public void readLockBooking(String bookingCode) {
        ReadWriteLock bookingLock = getBookingLock(bookingCode);

        bookingLock.readLock().lock();
    }

    public void readUnlockBooking(String bookingCode) {
        ReadWriteLock bookingLock = getBookingLock(bookingCode);

        bookingLock.readLock().unlock();
    }

    public void writeLockSector(String sectorName) {
        ReadWriteLock sectorLock = getSectorLock(sectorName);

        sectorLock.writeLock().lock();
    }

    public void writeUnlockSector(String sectorName) {
        ReadWriteLock sectorLock = getSectorLock(sectorName);

        sectorLock.writeLock().unlock();
    }

    public void writeLockFlightCode(String flightCode) {
        ReadWriteLock flightCodeLock = getFlightCodeLock(flightCode);

        if (flightCodeLock != null)
            flightCodeLock.writeLock().lock();
    }

    public void writeUnlockFlightCode(String flightCode) {
        ReadWriteLock flightCodeLock = getFlightCodeLock(flightCode);

        if (flightCodeLock != null)
            flightCodeLock.writeLock().unlock();
    }
}
