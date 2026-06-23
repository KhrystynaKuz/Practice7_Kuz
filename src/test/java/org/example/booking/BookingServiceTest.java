package org.example.booking;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private EmailNotifier emailNotifier;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void shouldBookSeatSuccessfully() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(true);

        BookingStatus result = bookingService.bookSeat(1L, "person@mail.com");

        assertEquals(BookingStatus.SUCCESS, result);
    }

    @Test
    void shouldReturnNotFoundWhenSeatDoesNotExist() {

        when(repository.findSeatById(1L)).thenReturn(null);

        BookingStatus result = bookingService.bookSeat(1L, "person@mail.com");

        assertEquals(BookingStatus.NOT_FOUND, result);
    }

    @Test
    void shouldFailWhenPaymentFails() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(false);

        BookingStatus result = bookingService.bookSeat(1L, "person@mail.com");

        assertEquals(BookingStatus.PAYMENT_FAILED, result);
    }

    @Test
    void shouldReturnAlreadyBookedWhenSeatIsBooked() {
        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = true;

        when(repository.findSeatById(1L)).thenReturn(seat);

        BookingStatus result = bookingService.bookSeat(1L, "test@mail.com");

        assertEquals(BookingStatus.ALREADY_BOOKED, result);
    }

    @Test
    void shouldSendEmailOnSuccessfulBooking() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(true);

        bookingService.bookSeat(1L, "person@mail.com");

        verify(emailNotifier).sendEmail("person@mail.com", 1L);
    }

    @Test
    void shouldCallEmailExactlyOnce() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(true);

        bookingService.bookSeat(1L, "person@mail.com");

        verify(emailNotifier, times(1))
                .sendEmail("person@mail.com", 1L);
    }

    @Test
    void shouldNeverSendEmailWhenPaymentFails() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(false);

        bookingService.bookSeat(1L, "person@mail.com");

        verify(emailNotifier, never()).sendEmail(any(), anyLong());
    }

    @Test
    void shouldBookSeatAndCheckAllFieldsWithSoftAssertions() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(true);

        BookingStatus result = bookingService.bookSeat(1L, "person@mail.com");

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(result).isEqualTo(BookingStatus.SUCCESS);

        softly.assertThat(seat.booked).isTrue();

        softly.assertThat(seat.price).isEqualTo(100);

        softly.assertAll();
    }

    @Test
    void shouldContainBookedSeat() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(true);

        bookingService.bookSeat(1L, "person@mail.com");

        assertThat(bookingService.getBookedSeats()).contains(seat);
    }

    @Test
    void shouldHaveOneBookedSeat() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(true);

        bookingService.bookSeat(1L, "person@mail.com");

        assertThat(bookingService.getBookedSeats()).hasSize(1);
    }

    @Test
    void shouldContainSeatWithCorrectId() {

        Seat seat = new Seat();
        seat.id = 1;
        seat.booked = false;
        seat.price = 100;

        when(repository.findSeatById(1L)).thenReturn(seat);
        when(paymentService.pay(100)).thenReturn(true);

        bookingService.bookSeat(1L, "person@mail.com");

        assertThat(bookingService.getBookedSeats()).extracting(Seat::getId).contains(1L);
    }

    @Test
    void weakTest() {

        assertEquals("STANDARD", bookingService.getSeatCategory(50));
        assertEquals("PREMIUM", bookingService.getSeatCategory(150));
        assertEquals("VIP", bookingService.getSeatCategory(250));
    }

    @Test
    void strongTest() {
        assertEquals("STANDARD", bookingService.getSeatCategory(99));
        assertEquals("PREMIUM", bookingService.getSeatCategory(100));
        assertEquals("PREMIUM", bookingService.getSeatCategory(199));
        assertEquals("VIP", bookingService.getSeatCategory(200));
    }
}