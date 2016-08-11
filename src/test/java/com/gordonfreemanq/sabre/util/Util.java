package com.gordonfreemanq.sabre.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;

public class Util {
    private Util() {}
    
    /**
     * Formatter to format log-messages in tests
     *
     */
    private static class MVTestLogFormatter extends Formatter {
        private static final DateFormat df = new SimpleDateFormat("HH:mm:ss");

        public String format(LogRecord record) {
            StringBuilder ret = new StringBuilder();

            ret.append("[").append(df.format(record.getMillis())).append("] [")
                    .append(record.getLoggerName()).append("] [")
                    .append(record.getLevel().getLocalizedName()).append("] ");
            ret.append(record.getMessage());
            ret.append('\n');

            if (record.getThrown() != null) {
                // An Exception was thrown! Let's print the StackTrace!
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                ret.append(writer);
            }

            return ret.toString();
        }
    }

    public static final Logger logger = Logger.getLogger("Sabre-Test");

    static {
        logger.setUseParentHandlers(false);

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new MVTestLogFormatter());
        Handler[] handlers = logger.getHandlers();

        for (Handler h : handlers)
            logger.removeHandler(h);

        logger.addHandler(handler);
    }

    public static void log(Throwable t) {
        log(Level.WARNING, t.getLocalizedMessage(), t);
    }

    public static void log(Level level, Throwable t) {
        log(level, t.getLocalizedMessage(), t);
    }

    public static void log(String message, Throwable t) {
        log(Level.WARNING, message, t);
    }

    public static void log(Level level, String message, Throwable t) {
        LogRecord record = new LogRecord(level, message);
        record.setThrown(t);
        logger.log(record);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }
}