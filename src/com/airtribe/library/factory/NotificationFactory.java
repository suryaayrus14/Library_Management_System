package com.airtribe.library.factory;

import com.airtribe.library.entity.Patron;
import com.airtribe.library.enums.NotificationType;
import com.airtribe.library.observer.ConsoleNotificationObserver;
import com.airtribe.library.observer.EmailNotificationObserver;
import com.airtribe.library.observer.NotificationObserver;
import com.airtribe.library.observer.SmsNotificationObserver;

public class NotificationFactory {

    private NotificationFactory() {
        // Prevent object creation
    }

    public static NotificationObserver createObserver(
            NotificationType type,
            Patron patron) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Notification type cannot be null."
            );
        }

        if (patron == null) {
            throw new IllegalArgumentException(
                    "Patron cannot be null."
            );
        }

        return switch (type) {
            case EMAIL -> new EmailNotificationObserver(patron);
            case SMS -> new SmsNotificationObserver(patron);
            case CONSOLE -> new ConsoleNotificationObserver(patron);
        };
    }
}