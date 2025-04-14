package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void log(String msg) {
        System.out.println("[" + fmt.format(LocalDateTime.now()) + "] " + msg);
    }

    public static void error(String msg) {
        System.err.println("[" + fmt.format(LocalDateTime.now()) + " ERROR] " + msg);
    }

    public static void debug(String msg) {
        if (isDebug()) {
            System.out.println("[" + fmt.format(LocalDateTime.now()) + " DEBUG] " + msg);
        }
    }

    private static boolean isDebug() {
        // Toggle debug mode here
        return true;
    }
}
