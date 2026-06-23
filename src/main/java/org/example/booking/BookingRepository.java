package org.example.booking;

public interface BookingRepository {
    Seat findSeatById(long id);
    void save(Seat seat);
}
