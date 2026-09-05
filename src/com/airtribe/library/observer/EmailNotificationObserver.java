package com.airtribe.library.observer;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Patron;

public class EmailNotificationObserver implements NotificationObserver {

    private final Patron patron;

    public EmailNotificationObserver(Patron patron) {
        this.patron = patron;
    }

    @Override
    public void update(Book book) {
        System.out.println("Sending email to " + patron.getEmail() + ": Book '" + book.getTitle() + "' is now available.");
    }
}
