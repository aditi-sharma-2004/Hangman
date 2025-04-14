package utils;
public class Constants {

    // Server Config
    public static final int DEFAULT_PORT = 12345;
    public static final String DEFAULT_HOST = "localhost";

    // Game Settings
    public static final int MAX_STRIKES = 6;
    public static final int TURN_TIME_SECONDS = 30;

    // Message Types
    public static final String MSG_CHAT = "CHAT";
    public static final String MSG_COMMAND = "COMMAND";
    public static final String MSG_EVENT = "EVENT";
    public static final String MSG_SYSTEM = "SYSTEM";

    // Commands
    public static final String CMD_READY = "/ready";
    public static final String CMD_START = "/start";
    public static final String CMD_GUESS = "/guess";
    public static final String CMD_LEAVE = "/leave";
    public static final String CMD_SPECTATE = "/spectate";
    public static final String CMD_HELP = "/help";

    // Events
    public static final String EVENT_GAME_STARTED = "GAME_STARTED";
    public static final String EVENT_GAME_ENDED = "GAME_ENDED";
    public static final String EVENT_PLAYER_GUESSED = "PLAYER_GUESSED";
    public static final String EVENT_NEXT_TURN = "NEXT_TURN";
}
