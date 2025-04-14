package payload;
/**
 * Base payload class for message transport between server and clients
 */
public class Payload {
    // Fields
    private String clientId;
    private String message;
    private PayloadType type;
    
    // Enum for different payload types
    public enum PayloadType {
        MESSAGE,    // Regular chat message
        COMMAND,    // Command from client to server
        GAME_STATE, // Game state update
        POINTS,     // Points update
        HANGMAN,    // Hangman/strikes update
        ERROR       // Error message
    }
    
    // Constructors
    public Payload() {
        // Default constructor
    }
    
    public Payload(String clientId, String message, PayloadType type) {
        this.clientId = clientId;
        this.message = message;
        this.type = type;
    }
    
    // Getters and setters
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public PayloadType getType() {
        return type;
    }
    
    public void setType(PayloadType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return "Payload [clientId=" + clientId + ", message=" + message + ", type=" + type + "]";
    }
}

/**
 * Specialized payload for syncing player points
 */


/**
 * Specialized payload for syncing game state information
 */


/**
 * Specialized payload for syncing hangman/strikes state
 */
