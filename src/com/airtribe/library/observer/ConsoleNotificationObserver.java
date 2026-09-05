package com.airtribe.library.observer;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Patron;

public class ConsoleNotificationObserver implements NotificationObserver {

    private final Patron patron;

    public ConsoleNotificationObserver(Patron patron) {
        this.patron = patron;
    }

    @Override
    public void update(Book book) {
        System.out.println("Notification to " + patron.getName() + ": Book '" + book.getTitle() + "' is now available.");
    }
}