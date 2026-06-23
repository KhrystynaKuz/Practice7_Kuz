package org.example.booking;

public interface EmailNotifier {
    void sendEmail(String email, long seatId);
}
