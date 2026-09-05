package com.airtribe.library.service;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Patron;
import com.airtribe.library.entity.Reservation;
import com.airtribe.library.enums.BookStatus;
import com.airtribe.library.enums.NotificationType;
import com.airtribe.library.repository.InMemoryReservationRepository;
import com.airtribe.library.repository.ReservationRepository;
import com.airtribe.library.util.LoggerUtil;

import java.util.List;
import java.util.logging.Logger;

public class ReservationService {

    private static final Logger logger = LoggerUtil.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;

    private final BookService bookService;
    private final PatronService patronService;

    private final NotificationService notificationService;

    public ReservationService(
            ReservationRepository reservationRepository,
            BookService bookService,
            PatronService patronService,
            NotificationService notificationService) {

        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
        this.patronService = patronService;
        this.notificationService = notificationService;
    }

    public Reservation reserveBook(String isbn, String patronId) {

        logger.info("Reservation requested. ISBN: " + isbn + ", Patron: " + patronId);

        Book book = bookService.getBookByIsbn(isbn);
        Patron patron = patronService.getPatronById(patronId);

        if (book.getStatus() == BookStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Book with ISBN " + isbn
                            + " is available. No reservation is required."
            );
        }

        List<Reservation> existingReservations = reservationRepository.findByBook(isbn);

        boolean alreadyReserved = existingReservations.stream()
                .anyMatch(reservation ->
                        reservation.getPatron()
                                .getPatronId()
                                .equals(patronId));

        if (alreadyReserved) {
            throw new IllegalStateException(
                    "Patron " + patronId
                            + " has already reserved this book."
            );
        }

        Reservation reservation = new Reservation(book, patron);

        reservationRepository.save(reservation);

        logger.info("Book reserved successfully. ISBN: " + isbn
                + ", Patron: " + patronId);

        return reservation;
    }

    public void cancelReservation(String isbn, String patronId) {

        logger.info("Cancellation requested. ISBN: " + isbn + ", Patron: " + patronId);

        List<Reservation> reservations = reservationRepository.findByBook(isbn);

        Reservation reservation = reservations.stream()
                .filter(r -> r.getPatron()
                        .getPatronId()
                        .equals(patronId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No reservation found for patron "
                                + patronId
                                + " and book "
                                + isbn
                ));

        reservationRepository.delete(reservation);

        logger.info("Reservation cancelled successfully. ISBN: " + isbn + ", Patron: " + patronId);
    }

    public List<Reservation> getReservationsForBook(String isbn) {
        bookService.getBookByIsbn(isbn);

        return reservationRepository.findByBook(isbn);
    }

    public List<Reservation> getReservationsForPatron(String patronId) {
        patronService.getPatronById(patronId);

        return reservationRepository.findByPatron(patronId);
    }

    public Reservation getNextReservation(String isbn) {
        bookService.getBookByIsbn(isbn);

        return reservationRepository.pollNextReservation(isbn);
    }

    public void notifyNextReservation(String isbn, NotificationType notificationType) {
        Book book = bookService.getBookByIsbn(isbn);
        Reservation reservation = reservationRepository.pollNextReservation(isbn);

        if (reservation == null) {
            logger.info("No reservations found for ISBN: " + isbn);
            return;
        }

        Patron patron = reservation.getPatron();

        notificationService.clearObservers();

        notificationService.addObserverForPatron(notificationType, patron);

        notificationService.notifyObservers(book);

        notificationService.clearObservers();

        reservation.markAsNotified();

        logger.info("Reservation notification sent. ISBN: " + isbn + ", Patron: " + patron.getPatronId());
    }
}