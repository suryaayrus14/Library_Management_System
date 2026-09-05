package com.airtribe.library.observer;

import com.airtribe.library.entity.Book;

public interface NotificationObserver {
    public void update(Book book);
}
