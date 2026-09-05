package com.airtribe.library.util;

import java.util.logging.*;

public class LoggerUtil {

    private LoggerUtil() {
        // Prevent object creation
    }

    public static Logger getLogger(Class<?> clazz) {

        Logger logger = Logger.getLogger(clazz.getName());

        logger.setUseParentHandlers(false);

        if (logger.getHandlers().length == 0) {

            StreamHandler streamHandler = new StreamHandler(
                    System.out,
                    new Formatter() {
                        @Override
                        public String format(LogRecord record) {
                            return String.format(
                                    "[%s] %s%n",
                                    record.getLevel(),
                                    record.getMessage()
                            );
                        }
                    }
            ) {
                @Override
                public synchronized void publish(LogRecord record) {
                    super.publish(record);
                    flush();
                }
            };

            logger.addHandler(streamHandler);
        }

        return logger;
    }
}