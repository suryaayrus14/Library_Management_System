package com.airtribe.library;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.LibraryBranch;
import com.airtribe.library.entity.Patron;
import com.airtribe.library.entity.Reservation;
import com.airtribe.library.enums.BookStatus;
import com.airtribe.library.enums.NotificationType;
import com.airtribe.library.exception.InvalidLoanException;
import com.airtribe.library.repository.InMemoryBookRepository;
import com.airtribe.library.repository.InMemoryBranchRepository;
import com.airtribe.library.repository.InMemoryPatronRepository;
import com.airtribe.library.repository.InMemoryReservationRepository;
import com.airtribe.library.service.*;
import com.airtribe.library.strategy.AuthorRecommendationStrategy;

import java.util.List;

public class LibraryTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("========== LIBRARY SYSTEM TESTS ==========\n");

        testAddBook();
        testDuplicateBook();
        testCheckout();
        testCannotCheckoutBorrowedBook();
        testReturnBook();
        testReservation();
        testDuplicateReservation();
        testReservationFIFO();
        testBranchTransfer();
        testAuthorRecommendation();

        System.out.println("\n========== TEST RESULTS ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed == 0) {
            System.out.println("ALL TESTS PASSED");
        } else {
            System.out.println("SOME TESTS FAILED");
        }
    }

    private static void testAddBook() {

        InMemoryBookRepository repository =
                new InMemoryBookRepository();

        BookService bookService =
                new BookService(repository);

        Book book = new Book(
                "ISBN-001",
                "Effective Java",
                "Joshua Bloch",
                2018
        );

        bookService.addBook(book);

        assertTrue(
                bookService.getBookByIsbn("ISBN-001") != null,
                "Add book"
        );
    }

    private static void testDuplicateBook() {

        InMemoryBookRepository repository =
                new InMemoryBookRepository();

        BookService bookService =
                new BookService(repository);

        Book book = new Book(
                "ISBN-001",
                "Effective Java",
                "Joshua Bloch",
                2018
        );

        bookService.addBook(book);

        try {
            bookService.addBook(book);

            assertTrue(false, "Duplicate book rejected");

        } catch (RuntimeException e) {

            assertTrue(true, "Duplicate book rejected");
        }
    }

    private static void testCheckout() {

        TestContext context = createContext();

        context.bookService.addBook(context.book);

        context.patronService.addPatron(context.patron);

        context.lendingService.checkOutBook(
                "ISBN-001",
                "P001"
        );

        assertTrue(
                context.book.getStatus() == BookStatus.BORROWED,
                "Checkout book"
        );
    }

    private static void testCannotCheckoutBorrowedBook() {

        TestContext context = createContext();

        context.bookService.addBook(context.book);

        context.patronService.addPatron(context.patron);

        context.lendingService.checkOutBook(
                "ISBN-001",
                "P001"
        );

        try {

            context.lendingService.checkOutBook(
                    "ISBN-001",
                    "P001"
            );

            assertTrue(
                    false,
                    "Cannot checkout borrowed book"
            );

        } catch (RuntimeException e) {

            assertTrue(
                    true,
                    "Cannot checkout borrowed book"
            );
        }
    }

    private static void testReturnBook() {

        TestContext context = createContext();

        context.bookService.addBook(context.book);

        context.patronService.addPatron(context.patron);

        context.lendingService.checkOutBook("ISBN-001", "P001");

        context.lendingService.returnBook("ISBN-001", "P001");

        assertTrue(context.book.getStatus() == BookStatus.AVAILABLE, "Return book");
    }

    private static void testReservation() {

        TestContext context = createContext();

        context.bookService.addBook(context.book);

        context.patronService.addPatron(context.patron);
        context.patronService.addPatron(context.patron2);

        context.lendingService.checkOutBook(
                "ISBN-001",
                "P001"
        );

        Reservation reservation =
                context.reservationService.reserveBook(
                        "ISBN-001",
                        "P002"
                );

        assertTrue(
                reservation != null
                        && context.reservationService
                        .getReservationsForBook("ISBN-001")
                        .size() == 1,
                "Reservation"
        );
    }

    private static void testDuplicateReservation() {

        TestContext context = createContext();

        context.bookService.addBook(context.book);

        context.patronService.addPatron(context.patron);
        context.patronService.addPatron(context.patron2);

        context.lendingService.checkOutBook(
                "ISBN-001",
                "P001"
        );

        context.reservationService.reserveBook(
                "ISBN-001",
                "P002"
        );

        try {

            context.reservationService.reserveBook(
                    "ISBN-001",
                    "P002"
            );

            assertTrue(
                    false,
                    "Duplicate reservation rejected"
            );

        } catch (RuntimeException e) {

            assertTrue(
                    true,
                    "Duplicate reservation rejected"
            );
        }
    }

    private static void testReservationFIFO() {

        TestContext context = createContext();

        context.bookService.addBook(context.book);

        Patron patron3 =
                new Patron(
                        "P003",
                        "Amit",
                        "amit@email.com"
                );

        context.patronService.addPatron(context.patron);
        context.patronService.addPatron(context.patron2);
        context.patronService.addPatron(patron3);

        context.lendingService.checkOutBook(
                "ISBN-001",
                "P001"
        );

        context.reservationService.reserveBook(
                "ISBN-001",
                "P002"
        );

        context.reservationService.reserveBook(
                "ISBN-001",
                "P003"
        );

        Reservation first =
                context.reservationService
                        .getNextReservation("ISBN-001");

        assertTrue(
                first != null
                        && first.getPatron()
                        .getPatronId()
                        .equals("P002"),
                "Reservation FIFO"
        );
    }

    private static void testBranchTransfer() {

        InMemoryBranchRepository repository =
                new InMemoryBranchRepository();

        BranchService branchService =
                new BranchService(repository);

        LibraryBranch branch1 =
                new LibraryBranch(
                        "B001",
                        "Central",
                        "Hyderabad"
                );

        LibraryBranch branch2 =
                new LibraryBranch(
                        "B002",
                        "West",
                        "Miyapur"
                );

        Book book =
                new Book(
                        "ISBN-001",
                        "Effective Java",
                        "Joshua Bloch",
                        2018
                );

        branchService.addBranch(branch1);
        branchService.addBranch(branch2);

        branchService.addBookToBranch(
                "B001",
                book
        );

        branchService.transferBook(
                "ISBN-001",
                "B001",
                "B002"
        );

        assertTrue(
                !branch1.hasBook("ISBN-001")
                        && branch2.hasBook("ISBN-001"),
                "Branch transfer"
        );
    }

    private static void testAuthorRecommendation() {

        TestContext context = createContext();

        Book book2 =
                new Book(
                        "ISBN-002",
                        "Java Puzzlers",
                        "Joshua Bloch",
                        2005
                );

        context.bookService.addBook(context.book);
        context.bookService.addBook(book2);

        context.patronService.addPatron(context.patron);

        context.lendingService.checkOutBook(
                "ISBN-001",
                "P001"
        );

        RecommendationService recommendationService =
                new RecommendationService(
                        context.bookService,
                        new AuthorRecommendationStrategy()
                );

        List<Book> recommendations =
                recommendationService
                        .getRecommendations(context.patron);

        assertTrue(
                recommendations.size() == 1
                        && recommendations.get(0)
                        .getIsbn()
                        .equals("ISBN-002"),
                "Author recommendation"
        );
    }

    private static TestContext createContext() {

        InMemoryBookRepository bookRepository =
                new InMemoryBookRepository();

        InMemoryPatronRepository patronRepository =
                new InMemoryPatronRepository();

        InMemoryReservationRepository reservationRepository =
                new InMemoryReservationRepository();

        BookService bookService =
                new BookService(bookRepository);

        PatronService patronService =
                new PatronService(patronRepository);

        NotificationService notificationService =
                new NotificationService();

        ReservationService reservationService =
                new ReservationService(
                        reservationRepository,
                        bookService,
                        patronService,
                        notificationService
                );

        LendingService lendingService =
                new LendingService(
                        bookService,
                        patronService,
                        reservationService
                );

        Book book =
                new Book(
                        "ISBN-001",
                        "Effective Java",
                        "Joshua Bloch",
                        2018
                );

        Patron patron =
                new Patron(
                        "P001",
                        "Surya",
                        "surya@email.com"
                );

        Patron patron2 =
                new Patron(
                        "P002",
                        "Rahul",
                        "rahul@email.com"
                );

        return new TestContext(
                bookService,
                patronService,
                reservationService,
                lendingService,
                book,
                patron,
                patron2
        );
    }

    private static void assertTrue(
            boolean condition,
            String testName) {

        if (condition) {
            System.out.println("PASS: " + testName);
            passed++;
        } else {
            System.out.println("FAIL: " + testName);
            failed++;
        }
    }

    private static class TestContext {

        private final BookService bookService;
        private final PatronService patronService;
        private final ReservationService reservationService;
        private final LendingService lendingService;
        private final Book book;
        private final Patron patron;
        private final Patron patron2;

        private TestContext(
                BookService bookService,
                PatronService patronService,
                ReservationService reservationService,
                LendingService lendingService,
                Book book,
                Patron patron,
                Patron patron2) {

            this.bookService = bookService;
            this.patronService = patronService;
            this.reservationService = reservationService;
            this.lendingService = lendingService;
            this.book = book;
            this.patron = patron;
            this.patron2 = patron2;
        }
    }
}