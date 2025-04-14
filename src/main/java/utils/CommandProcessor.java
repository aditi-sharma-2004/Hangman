package utils;

import java.util.*;

public class CommandProcessor {
    private  Map<String, Runnable> commandHandlers;

    public CommandProcessor() {
        commandHandlers = new HashMap<>();
    }

    public void registerCommand(String cmd, Runnable handler) {
        commandHandlers.put(cmd.toLowerCase(), handler);
    }

    public void process(String input) {
        if (!input.startsWith("/")) return;

        String[] parts = input.split(" ");
        String command = parts[0].toLowerCase();

        Runnable handler = commandHandlers.get(command);
        if (handler != null) {
            handler.run();
        } else {
            Logger.log("Unknown command: " + command);
        }
    }
}
