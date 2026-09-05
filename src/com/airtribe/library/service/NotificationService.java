package com.airtribe.library.service;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Patron;
import com.airtribe.library.enums.NotificationType;
import com.airtribe.library.factory.NotificationFactory;
import com.airtribe.library.observer.NotificationObserver;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private final List<NotificationObserver> observers;

    public NotificationService() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(NotificationObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException(
                    "Observer cannot be null."
            );
        }

        observers.add(observer);
    }

    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Book book) {
        for (NotificationObserver observer : observers) {
            observer.update(book);
        }
    }

    public void clearObservers() {
        observers.clear();
    }

    public void addObserverForPatron(NotificationType type, Patron patron) {
        NotificationObserver observer = NotificationFactory.createObserver(type, patron);
        addObserver(observer);
    }
}