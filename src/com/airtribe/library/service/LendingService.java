package com.airtribe.library.service;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Loan;
import com.airtribe.library.entity.Patron;
import com.airtribe.library.enums.BookStatus;
import com.airtribe.library.enums.NotificationType;
import com.airtribe.library.exception.BookUnavailableException;
import com.airtribe.library.exception.InvalidLoanException;
import com.airtribe.library.util.LoggerUtil;

import java.time.LocalDate;
import java.util.logging.Logger;

public class LendingService {

    private final BookService bookService;
    private final PatronService patronService;

    private final ReservationService reservationService;

    private static final Logger logger = LoggerUtil.getLogger(LendingService.class);

    public LendingService(BookService bookService, PatronService patronService, ReservationService reservationService) {
        this.bookService = bookService;
        this.patronService = patronService;
        this.reservationService = reservationService;
    }

    public Loan checkOutBook(String isbn, String patronId) {

        logger.info("Checkout requested. ISBN: " + isbn + ", Patron: " + patronId);

        Book book = bookService.getBookByIsbn(isbn);
        Patron patron = patronService.getPatronById(patronId);

        if (book.getStatus() != BookStatus.AVAILABLE) {

            logger.warning("Checkout failed. Book unavailable: " + isbn);

            throw new BookUnavailableException("Book with ISBN " + isbn + " is not available.");
        }

        Loan loan = new Loan(book, patron, LocalDate.now());

        book.markAsBorrowed();
        patron.addLoan(loan);
        logger.info("Book checked out successfully. ISBN: " + isbn + ", Patron: " + patronId);
        return loan;
    }

    public void returnBook(String isbn, String patronId){
        logger.info("Return requested. ISBN: " + isbn + ", Patron: " + patronId);
        Book book = bookService.getBookByIsbn(isbn);
        Patron patron = patronService.getPatronById(patronId);

        if (book.getStatus() != BookStatus.BORROWED) {
            logger.warning("Return failed. Book is not borrowed: " + isbn);
            throw new InvalidLoanException("Book with ISBN " + isbn + " is not currently borrowed.");
        }

        Loan activeLoan = patron.getBorrowingHistory().stream()
                .filter(loan -> loan.getBook().getIsbn().equals(isbn) && loan.getReturnDate() == null)
                .findFirst()
                .orElseThrow(() -> {
                    logger.warning("No active loan found. ISBN: " + isbn + ", Patron: " + patronId);

                    return new InvalidLoanException("No active loan found for this book and patron.");
                });
        activeLoan.markAsReturned(LocalDate.now());
        book.markAsAvailable();
        reservationService.notifyNextReservation(isbn, NotificationType.CONSOLE);
        logger.info("Book returned successfully. ISBN: " + isbn + ", Patron: " + patronId);
    }

    public boolean isBookAvailable(String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return book.getStatus() == BookStatus.AVAILABLE;
    }
}
