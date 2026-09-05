package com.airtribe.library.entity;

import java.time.LocalDateTime;

public class Reservation {

    private final Book book;
    private final Patron patron;
    private final LocalDateTime reservationDate;
    private boolean notified;

    public Reservation(Book book, Patron patron) {
        this.book = book;
        this.patron = patron;
        this.reservationDate = LocalDateTime.now();
        this.notified = false;
    }

    public Book getBook() {
        return book;
    }

    public Patron getPatron() {
        return patron;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public boolean isNotified() {
        return notified;
    }

    public void markAsNotified() {
        this.notified = true;
    }
}