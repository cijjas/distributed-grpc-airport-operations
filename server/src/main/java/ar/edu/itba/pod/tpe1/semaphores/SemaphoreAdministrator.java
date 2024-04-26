package ar.edu.itba.pod.tpe1.semaphores;

import ar.edu.itba.pod.tpe1.models.Booking.Booking;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SemaphoreAdministrator {
    private final ConcurrentSkipListMap<String, ReadWriteLock> bookingReadWriteLockMap;
    private final ReadWriteLock flightCodesReadWriteLock;
    private final ConcurrentSkipListMap<String, ReadWriteLock> airlineReadWriteLockMap;

    private final ConcurrentSkipListMap<String, ReadWriteLock> sectorReadWriteLockMap;
    public SemaphoreAdministrator() {
        this.bookingReadWriteLockMap = new ConcurrentSkipListMap<>();
        this.flightCodesReadWriteLock = new ReentrantReadWriteLock();
        this.airlineReadWriteLockMap = new ConcurrentSkipListMap<>();
        this.sectorReadWriteLockMap = new ConcurrentSkipListMap<>();
    }

    // BOOKING

    private synchronized ReadWriteLock addBookingLock(String bookingCode) {
        bookingReadWriteLockMap.put(bookingCode, new ReentrantReadWriteLock());
        return bookingReadWriteLockMap.get(bookingCode);
    }

    // SECTOR
    public synchronized void addSectorLock(String sectorName) {
        if (sectorReadWriteLockMap.get(sectorName) != null)
            throw new IllegalStateException("Sector lock already exists");

        sectorReadWriteLockMap.put(sectorName, new ReentrantReadWriteLock());
    }


    // BOOKING AND FLIGHTCODE
    public void addAndWriteLockBookingAndFlightCodes(Booking booking) {
        ReadWriteLock bookingLock = addBookingLock(booking.getBookingCode());

        bookingLock.writeLock().lock();
        flightCodesReadWriteLock.writeLock().lock();
    }

    public void writeUnlockBookingAndFlightCodes(Booking booking) {
        ReadWriteLock bookingLock = bookingReadWriteLockMap.get(booking.getBookingCode());

        bookingLock.writeLock().unlock();
        flightCodesReadWriteLock.writeLock().unlock();
    }

    // BOOKING

    public void writeLockBooking(String bookingCode) {
        ReadWriteLock bookingLock = bookingReadWriteLockMap.get(bookingCode);

        if (bookingLock != null)
            bookingLock.writeLock().lock();
    }

    public void writeUnlockBooking(String bookingCode) {
        ReadWriteLock bookingLock = bookingReadWriteLockMap.get(bookingCode);

        if (bookingLock != null)
            bookingLock.writeLock().unlock();
    }

    public void readLockBooking(String bookingCode) {
        ReadWriteLock bookingLock = bookingReadWriteLockMap.get(bookingCode);

        bookingLock.readLock().lock();
    }

    public void readUnlockBooking(String bookingCode) {
        ReadWriteLock bookingLock = bookingReadWriteLockMap.get(bookingCode);

        bookingLock.readLock().unlock();
    }

    public void writeLockSector(String sectorName) {
        ReadWriteLock sectorLock = sectorReadWriteLockMap.get(sectorName);

        sectorLock.writeLock().lock();
    }

    public void writeUnlockSector(String sectorName) {
        ReadWriteLock sectorLock = sectorReadWriteLockMap.get(sectorName);

        sectorLock.writeLock().unlock();
    }

    public void writeLockFlightCodes() {
        flightCodesReadWriteLock.writeLock().lock();
    }

    public void writeUnlockFlightCodes() {
        flightCodesReadWriteLock.writeLock().unlock();
    }

    public void unlockReadLockWriteFlightCodes() {
        readUnlockFlightCodes();
        writeLockFlightCodes();
    }

    public void readLockFlightCodes() {
        flightCodesReadWriteLock.readLock().lock();
    }

    public void readUnlockFlightCodes() {
        flightCodesReadWriteLock.readLock().unlock();
    }

    public void addAndWriteLockAirline(String airlineName) {
        airlineReadWriteLockMap.put(airlineName, new ReentrantReadWriteLock());

        writeLockAirlineName(airlineName);
    }

    public void writeLockAirlineName(String airlineName) {
        ReadWriteLock airlineNameLock = airlineReadWriteLockMap.get(airlineName);

        if (airlineNameLock != null)
            airlineNameLock.writeLock().lock();
    }

    public void writeUnlockAirlineName(String airlineName) {
        ReadWriteLock airlineNameLock = airlineReadWriteLockMap.get(airlineName);

        if (airlineNameLock != null)
            airlineNameLock.writeLock().unlock();
    }
}
