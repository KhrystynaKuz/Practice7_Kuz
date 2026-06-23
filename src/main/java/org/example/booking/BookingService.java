package org.example.booking;

import java.util.ArrayList;
import java.util.List;

class BookingService {

    private final BookingRepository repository;
    private final PaymentService paymentService;
    private final EmailNotifier emailNotifier;
    private final List<Seat> bookedSeats = new ArrayList<>();

    public BookingService(BookingRepository repository, PaymentService paymentService, EmailNotifier emailNotifier) {
        this.repository = repository;
        this.paymentService = paymentService;
        this.emailNotifier = emailNotifier;
    }

    public BookingStatus bookSeat(long seatId, String email) {

        Seat seat = repository.findSeatById(seatId);

        if (seat == null) {
            return BookingStatus.NOT_FOUND;
        }

        if (seat.booked) {
            return BookingStatus.ALREADY_BOOKED;
        }

        boolean paid = paymentService.pay(seat.price);

        if (!paid) {
            return BookingStatus.PAYMENT_FAILED;
        }

        seat.booked = true;
        repository.save(seat);
        bookedSeats.add(seat);
        emailNotifier.sendEmail(email, seatId);

        return BookingStatus.SUCCESS;
    }

    public List<Seat> getBookedSeats() {
        return bookedSeats;
    }

    public String getSeatCategory(double price) {

        if (price < 100) {
            return "STANDARD";
        } else if (price < 200) {
            return "PREMIUM";
        } else {
            return "VIP";
        }
    }
}