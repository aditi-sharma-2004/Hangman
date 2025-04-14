package payload;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameStatePayload extends Payload {
    // Fields
    private char[] revealedLetters;
    private Set<Character> guessedLetters;
    private String currentTurn;
    private String gameState; // WAITING, PLAYING, GAME_OVER
    private List<String> players;
    private Map<String, String> playerStatuses; // Map of player names to their status
    
    // Constructors
    public GameStatePayload() {
        super();
        setType(PayloadType.GAME_STATE);
        guessedLetters = new HashSet<>();
        players = new ArrayList<>();
        playerStatuses = new HashMap<>();
    }
    
    public GameStatePayload(String clientId, char[] revealedLetters, 
                           Set<Character> guessedLetters, String currentTurn, 
                           String gameState) {
        super(clientId, "Game State Update", PayloadType.GAME_STATE);
        this.revealedLetters = revealedLetters;
        this.guessedLetters = guessedLetters;
        this.currentTurn = currentTurn;
        this.gameState = gameState;
        this.players = new ArrayList<>();
        this.playerStatuses = new HashMap<>();
    }
    
    // Getters and setters
    public char[] getRevealedLetters() {
        return revealedLetters;
    }
    
    public void setRevealedLetters(char[] revealedLetters) {
        this.revealedLetters = revealedLetters;
    }
    
    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }
    
    public void setGuessedLetters(Set<Character> guessedLetters) {
        this.guessedLetters = guessedLetters;
    }
    
    public String getCurrentTurn() {
        return currentTurn;
    }
    
    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }
    
    public String getGameState() {
        return gameState;
    }
    
    public void setGameState(String gameState) {
        this.gameState = gameState;
    }
    
    public List<String> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<String> players) {
        this.players = players;
    }
    
    public Map<String, String> getPlayerStatuses() {
        return playerStatuses;
    }
    
    public void setPlayerStatuses(Map<String, String> playerStatuses) {
        this.playerStatuses = playerStatuses;
    }
    
    // Add a player
    public void addPlayer(String playerName, String status) {
        if (!players.contains(playerName)) {
            players.add(playerName);
        }
        playerStatuses.put(playerName, status);
    }
    
    // Check if a letter has been guessed
    public boolean isLetterGuessed(char letter) {
        return guessedLetters.contains(letter);
    }
    
    // Convert revealed letters to display string
    public String getDisplayWord() {
        StringBuilder displayWord = new StringBuilder();
        for (char c : revealedLetters) {
            displayWord.append(c == '\0' ? '_' : c).append(' ');
        }
        return displayWord.toString().trim();
    }
    
    @Override
    public String toString() {
        return "GameStatePayload [revealedLetters=" + Arrays.toString(revealedLetters) + 
               ", guessedLetters=" + guessedLetters + ", currentTurn=" + currentTurn + 
               ", gameState=" + gameState + ", players=" + players + 
               ", playerStatuses=" + playerStatuses + ", base=" + super.toString() + "]";
    }
}