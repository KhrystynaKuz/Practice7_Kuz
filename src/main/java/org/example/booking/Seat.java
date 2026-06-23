package org.example.booking;

public class Seat {
    long id;
    boolean booked;
    double price;

    public Seat(){};

    public Seat(long id, boolean booked, double price) {
        this.id = id;
        this.booked = booked;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}