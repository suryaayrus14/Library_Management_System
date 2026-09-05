package com.airtribe.library.entity;

import java.time.LocalDate;

public class Loan {

    private final Book book;
    private final Patron patron;
    private final LocalDate checkoutDate;
    private LocalDate returnDate;

    public Loan(Book book, Patron patron, LocalDate checkoutDate) {
        this.book = book;
        this.patron = patron;
        this.checkoutDate = checkoutDate;
    }

    public Book getBook() {
        return book;
    }

    public Patron getPatron() {
        return patron;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void markAsReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}