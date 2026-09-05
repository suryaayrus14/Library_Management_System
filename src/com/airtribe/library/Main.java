
package com.airtribe.library;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.LibraryBranch;
import com.airtribe.library.entity.Patron;
import com.airtribe.library.repository.*;
import com.airtribe.library.service.*;
import com.airtribe.library.strategy.AuthorRecommendationStrategy;
import com.airtribe.library.strategy.RecentPublicationRecommendationStrategy;

public class Main {

    public static void main(String[] args) {

        // -------------------------------
        // Repositories
        // -------------------------------
        InMemoryBookRepository bookRepository = new InMemoryBookRepository();
        InMemoryPatronRepository patronRepository = new InMemoryPatronRepository();
        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();
        InMemoryBranchRepository branchRepository = new InMemoryBranchRepository();

        // -------------------------------
        // Services
        // -------------------------------
        BookService bookService = new BookService(bookRepository);
        PatronService patronService = new PatronService(patronRepository);
        NotificationService notificationService = new NotificationService();

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

        BranchService branchService =
                new BranchService(branchRepository);

        RecommendationService recommendationService =
                new RecommendationService(
                        bookService,
                        new AuthorRecommendationStrategy()
                );

        // -------------------------------
        // Books
        // -------------------------------
        Book book1 = new Book(
                "9780135166307",
                "Effective Java",
                "Joshua Bloch",
                2018
        );

        Book book2 = new Book(
                "9780134685991",
                "Java Concurrency in Practice",
                "Brian Goetz",
                2006
        );

        Book book3 = new Book(
                "9780321356680",
                "Java Puzzlers",
                "Joshua Bloch",
                2005
        );

        bookService.addBook(book1);
        bookService.addBook(book2);
        bookService.addBook(book3);

        System.out.println("Books added successfully.\n");

        // -------------------------------
        // Patrons
        // -------------------------------
        Patron patron1 =
                new Patron("P001", "Surya", "surya@email.com");

        Patron patron2 =
                new Patron("P002", "Rahul", "rahul@email.com");

        patronService.addPatron(patron1);
        patronService.addPatron(patron2);

        System.out.println("Patrons added successfully.\n");

        // -------------------------------
        // Book Search
        // -------------------------------
        System.out.println("========== ALL BOOKS ==========");

        bookService.getAllBooks().forEach(book ->
                System.out.println(
                        book.getIsbn()
                                + " | "
                                + book.getTitle()
                                + " | "
                                + book.getAuthor()
                                + " | "
                                + book.getStatus()
                ));

        System.out.println("\nSearch by title:");

        bookService.searchByTitle("Effective Java")
                .forEach(book ->
                        System.out.println(book.getTitle()));

        // -------------------------------
        // Multi Branch
        // -------------------------------
        System.out.println("\n========== BRANCHES ==========");

        LibraryBranch central =
                new LibraryBranch(
                        "B001",
                        "Central Library",
                        "Hyderabad"
                );

        LibraryBranch west =
                new LibraryBranch(
                        "B002",
                        "West Library",
                        "Miyapur"
                );

        branchService.addBranch(central);
        branchService.addBranch(west);

        branchService.addBookToBranch("B001", book1);
        branchService.addBookToBranch("B001", book2);

        branchService.transferBook(
                "9780135166307",
                "B001",
                "B002"
        );

        System.out.println(
                "Book transferred from Central to West Library."
        );

        // -------------------------------
        // Checkout
        // -------------------------------
        System.out.println("\n========== CHECKOUT ==========");

        lendingService.checkOutBook(
                "9780135166307",
                "P001"
        );

        System.out.println("Borrowed by: " + patron1.getName());
        System.out.println("Book status: " + book1.getStatus());

        // -------------------------------
        // Reservation
        // -------------------------------
        System.out.println("\n========== RESERVATION ==========");

        reservationService.reserveBook(
                "9780135166307",
                "P002"
        );

        System.out.println(
                "Rahul reserved Effective Java."
        );

        // -------------------------------
        // Borrowing History
        // -------------------------------
        System.out.println("\n========== HISTORY ==========");

        patronService.getBorrowingHistory("P001")
                .forEach(loan ->
                        System.out.println(
                                loan.getBook().getTitle()
                                        + " | Checkout: "
                                        + loan.getCheckoutDate()
                                        + " | Return: "
                                        + loan.getReturnDate()
                        ));

        // -------------------------------
        // Return + Notification
        // -------------------------------
        System.out.println("\n========== RETURN ==========");

        lendingService.returnBook(
                "9780135166307",
                "P001"
        );


        System.out.println("Book returned successfully.");
        System.out.println("Book status: " + book1.getStatus());

        // -------------------------------
        // Recommendation (Author)
        // -------------------------------
        System.out.println("\n========== AUTHOR RECOMMENDATIONS ==========");

        recommendationService.setStrategy(
                new AuthorRecommendationStrategy()
        );

        recommendationService
                .getRecommendations(patron1)
                .forEach(book ->
                        System.out.println(
                                book.getTitle()
                                        + " | "
                                        + book.getAuthor()
                        ));

        // -------------------------------
        // Recommendation (Recent)
        // -------------------------------
        System.out.println("\n========== RECENT BOOKS ==========");

        recommendationService.setStrategy(
                new RecentPublicationRecommendationStrategy()
        );

        recommendationService
                .getRecommendations(patron1)
                .forEach(book ->
                        System.out.println(
                                book.getTitle()
                                        + " | "
                                        + book.getPublicationYear()
                        ));

        // -------------------------------
        // Final History
        // -------------------------------
        System.out.println("\n========== FINAL HISTORY ==========");

        patronService.getBorrowingHistory("P001")
                .forEach(loan ->
                        System.out.println(
                                loan.getBook().getTitle()
                                        + " | Checkout: "
                                        + loan.getCheckoutDate()
                                        + " | Return: "
                                        + loan.getReturnDate()
                        ));

        System.out.println("\nLibrary Management System Demo Completed.");
    }
}