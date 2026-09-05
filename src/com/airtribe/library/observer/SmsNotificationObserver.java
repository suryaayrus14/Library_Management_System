package com.airtribe.library.observer;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Patron;

public class SmsNotificationObserver implements NotificationObserver {

    private final Patron patron;

    public SmsNotificationObserver(Patron patron) {
        this.patron = patron;
    }

    @Override
    public void update(Book book) {
        System.out.println("Sending SMS to ID" + patron.getPatronId() + ": Book '" + book.getTitle() + "' is now available.");
    }
}
