package server;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameRoom extends Room {
    private static final String[] WORD_LIST = {
        "PROGRAMMING", "JAVA", "NETWORK", "COMPUTER", "ALGORITHM", 
        "HANGMAN", "SOCKET", "SERVER", "CLIENT", "THREAD",
        "DATABASE", "SECURITY", "INTERFACE", "PROTOCOL", "ENCRYPTION"
    };
    
    private enum GameState {
        WAITING,
        PLAYING,
        GAME_OVER
    }
    
    private enum PlayerStatus {
        READY,
        PLAYING,
        AWAY,
        SPECTATOR
    }
    
    private GameState gameState;
    private String currentWord;
    private char[] revealedLetters;
    private Set<Character> guessedLetters;
    private int remainingGuesses;
    private ServerThread currentTurn;
    private final Map<ServerThread, PlayerStatus> playerStatuses;
    private final Map<ServerThread, Integer> scores;
    private final AtomicInteger turnIndex;
    private final Random random;
    
    public GameRoom(Server server, String roomName) {
        super(server, roomName);
        this.gameState = GameState.WAITING;
        this.playerStatuses = new ConcurrentHashMap<>();
        this.scores = new ConcurrentHashMap<>();
        this.turnIndex = new AtomicInteger(0);
        this.random = new Random();
        this.guessedLetters = new HashSet<>();
    }
    
    @Override
    public synchronized boolean addClient(ServerThread client) {
        boolean added = super.addClient(client);
        if (added) {
            // New players are spectators by default
            playerStatuses.put(client, PlayerStatus.SPECTATOR);
            scores.put(client, 0);
            
            // Inform the new player of the game state
            sendGameState(client);
        }
        return added;
    }
    
    @Override
    public synchronized boolean removeClient(ServerThread client) {
        boolean removed = super.removeClient(client);
        if (removed) {
            playerStatuses.remove(client);
            scores.remove(client);
            
            // Check if this was the player whose turn it was
            if (client.equals(currentTurn)) {
                nextTurn();
            }
            
            // Check if we need to end the game
            if (gameState == GameState.PLAYING && getActivePlayers().size() < 2) {
                endGame("Not enough players");
            }
        }
        return removed;
    }
    
    @Override
    public void handleCommand(ServerThread client, String command, String[] args) {
        switch (command.toLowerCase()) {
            case "ready":
                setPlayerReady(client);
                break;
            case "start":
                startGame(client);
                break;
            case "guess":
                makeGuess(client, args);
                break;
            case "away":
                setPlayerAway(client);
                break;
            case "back":
                setPlayerBack(client);
                break;
            case "spectate":
                setPlayerSpectate(client);
                break;
            case "play":
                setPlayerPlay(client);
                break;
            case "score":
                showScores(client);
                break;
            default:
                super.handleCommand(client, command, args);
                break;
        }
    }
    
    private void setPlayerReady(ServerThread client) {
        PlayerStatus status = playerStatuses.get(client);
        
        if (status == PlayerStatus.SPECTATOR) {
            client.sendMessage("SERVER", "You must join the game first with /play");
            return;
        }
        
        if (gameState != GameState.WAITING) {
            client.sendMessage("SERVER", "Cannot ready up while a game is in progress");
            return;
        }
        
        playerStatuses.put(client, PlayerStatus.READY);
        broadcastMessage("SERVER", client.getUsername() + " is ready to play!");
        
        // Check if all players are ready
        checkAllReady();
    }
    
    private void checkAllReady() {
        List<ServerThread> activePlayers = getActivePlayers();
        if (activePlayers.size() >= 2) {
            boolean allReady = true;
            for (ServerThread player : activePlayers) {
                if (playerStatuses.get(player) != PlayerStatus.READY) {
                    allReady = false;
                    break;
                }
            }
            
            if (allReady) {
                startGame(null);
            }
        }
    }
    
    private void startGame(ServerThread client) {
        if (gameState != GameState.WAITING) {
            if (client != null) {
                client.sendMessage("SERVER", "Game is already in progress");
            }
            return;
        }
        
        List<ServerThread> activePlayers = getActivePlayers();
        if (activePlayers.size() < 2) {
            if (client != null) {
                client.sendMessage("SERVER", "Need at least 2 players to start");
            }
            return;
        }
        
        // Reset game state
        gameState = GameState.PLAYING;
        guessedLetters.clear();
        remainingGuesses = 10;
        
        // Select a random word
        selectRandomWord();
        
        // Set all active players to PLAYING status
        for (ServerThread player : activePlayers) {
            playerStatuses.put(player, PlayerStatus.PLAYING);
        }
        
        // Choose random player to start
        turnIndex.set(random.nextInt(activePlayers.size()));
        updateCurrentTurn();
        
        broadcastMessage("SERVER", "Game started! Word has " + currentWord.length() + " letters.");
        broadcastGameState();
    }
    
    private void selectRandomWord() {
        currentWord = WORD_LIST[random.nextInt(WORD_LIST.length)];
        revealedLetters = new char[currentWord.length()];
        for (int i = 0; i < revealedLetters.length; i++) {
            revealedLetters[i] = '_';
        }
    }
    
    private void makeGuess(ServerThread client, String[] args) {
        if (gameState != GameState.PLAYING) {
            client.sendMessage("SERVER", "No game in progress");
            return;
        }
        
        if (!client.equals(currentTurn)) {
            client.sendMessage("SERVER", "It's not your turn");
            return;
        }
        
        if (args.length == 0) {
            client.sendMessage("SERVER", "Guess what? Please provide a letter or word.");
            return;
        }
        
        String guess = args[0].toUpperCase();
        
        // Handle word guess
        if (guess.length() > 1) {
            if (guess.equals(currentWord)) {
                // Correct word guess
                for (int i = 0; i < currentWord.length(); i++) {
                    revealedLetters[i] = currentWord.charAt(i);
                }
                
                // Award points
                int currentScore = scores.getOrDefault(client, 0);
                scores.put(client, currentScore + 10);
                
                broadcastMessage("SERVER", client.getUsername() + " correctly guessed the word: " + currentWord);
                endRound(true);
            } else {
                // Wrong word guess
                broadcastMessage("SERVER", client.getUsername() + " guessed " + guess + " - Incorrect!");
                remainingGuesses--;
                checkGameOver();
                nextTurn();
            }
            return;
        }
        
        // Handle letter guess
        char letter = guess.charAt(0);
        if (guessedLetters.contains(letter)) {
            client.sendMessage("SERVER", "Letter " + letter + " has already been guessed");
            return;
        }
        
        guessedLetters.add(letter);
        
        boolean found = false;
        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == letter) {
                revealedLetters[i] = letter;
                found = true;
            }
        }
        
        if (found) {
            // Award points for correct letter
            int currentScore = scores.getOrDefault(client, 0);
            scores.put(client, currentScore + 1);
            
            broadcastMessage("SERVER", client.getUsername() + " guessed " + letter + " - Correct!");
            
            // Check if word is complete
            boolean complete = true;
            for (char c : revealedLetters) {
                if (c == '_') {
                    complete = false;
                    break;
                }
            }
            
            if (complete) {
                // Award bonus points for completing word
                currentScore = scores.getOrDefault(client, 0);
                scores.put(client, currentScore + 5);
                
                broadcastMessage("SERVER", client.getUsername() + " completed the word: " + currentWord);
                endRound(true);
            } else {
                broadcastGameState();
            }
        } else {
            broadcastMessage("SERVER", client.getUsername() + " guessed " + letter + " - Incorrect!");
            remainingGuesses--;
            checkGameOver();
            nextTurn();
        }
    }
    
    private void checkGameOver() {
        if (remainingGuesses <= 0) {
            broadcastMessage("SERVER", "Game over! The word was: " + currentWord);
            endRound(false);
        }
    }
    
    private void endRound(boolean wordGuessed) {
        gameState = GameState.WAITING;
        broadcastMessage("SERVER", "Round ended. Type /ready to play again.");
        showScores(null);
        
        // Reset player statuses
        for (ServerThread client : clients) {
            PlayerStatus status = playerStatuses.get(client);
            if (status == PlayerStatus.PLAYING) {
                playerStatuses.put(client, PlayerStatus.SPECTATOR);
            }
        }
    }
    
    private void endGame(String reason) {
        gameState = GameState.GAME_OVER;
        broadcastMessage("SERVER", "Game ended: " + reason);
        
        // Reset scores and states
        scores.clear();
        for (ServerThread client : clients) {
            scores.put(client, 0);
            playerStatuses.put(client, PlayerStatus.SPECTATOR);
        }
        
        gameState = GameState.WAITING;
    }
    
    private void nextTurn() {
        List<ServerThread> activePlayers = getActivePlayers();
        if (activePlayers.isEmpty()) {
            return;
        }
        
        // Find next active player
        int nextIndex = (turnIndex.incrementAndGet()) % activePlayers.size();
        turnIndex.set(nextIndex);
        updateCurrentTurn();
        
        broadcastMessage("SERVER", "It's " + currentTurn.getUsername() + "'s turn.");
        broadcastGameState();
    }
    
    private void updateCurrentTurn() {
        List<ServerThread> activePlayers = getActivePlayers();
        if (!activePlayers.isEmpty()) {
            int idx = turnIndex.get() % activePlayers.size();
            currentTurn = activePlayers.get(idx);
        } else {
            currentTurn = null;
        }
    }
    
    private List<ServerThread> getActivePlayers() {
        List<ServerThread> active = new ArrayList<>();
        for (ServerThread client : clients) {
            PlayerStatus status = playerStatuses.get(client);
            if (status == PlayerStatus.PLAYING || status == PlayerStatus.READY) {
                active.add(client);
            }
        }
        return active;
    }
    
    private void setPlayerAway(ServerThread client) {
        PlayerStatus status = playerStatuses.get(client);
        if (status == PlayerStatus.PLAYING) {
            playerStatuses.put(client, PlayerStatus.AWAY);
            broadcastMessage("SERVER", client.getUsername() + " is now away");
            
            // If it was their turn, move to next player
            if (client.equals(currentTurn)) {
                nextTurn();
            }
        }
    }
    
    private void setPlayerBack(ServerThread client) {
        PlayerStatus status = playerStatuses.get(client);
        if (status == PlayerStatus.AWAY) {
            playerStatuses.put(client, PlayerStatus.PLAYING);
            broadcastMessage("SERVER", client.getUsername() + " is back");
        }
    }
    
    private void setPlayerSpectate(ServerThread client) {
        playerStatuses.put(client, PlayerStatus.SPECTATOR);
        broadcastMessage("SERVER", client.getUsername() + " is now spectating");
        
        // If it was their turn, move to next player
        if (client.equals(currentTurn)) {
            nextTurn();
        }
    }
    
    private void setPlayerPlay(ServerThread client) {
        PlayerStatus status = playerStatuses.get(client);
        if (status == PlayerStatus.SPECTATOR) {
            if (gameState == GameState.PLAYING) {
                playerStatuses.put(client, PlayerStatus.PLAYING);
                broadcastMessage("SERVER", client.getUsername() + " joined the game");
            } else {
                playerStatuses.put(client, PlayerStatus.READY);
                broadcastMessage("SERVER", client.getUsername() + " is ready to play");
                checkAllReady();
            }
        }
    }
    
    private void showScores(ServerThread client) {
        StringBuilder sb = new StringBuilder("SCORES:\n");
        List<Map.Entry<ServerThread, Integer>> sortedScores = new ArrayList<>(scores.entrySet());
        sortedScores.sort(Map.Entry.<ServerThread, Integer>comparingByValue().reversed());
        
        for (Map.Entry<ServerThread, Integer> entry : sortedScores) {
            sb.append(entry.getKey().getUsername()).append(": ").append(entry.getValue()).append("\n");
        }
        
        if (client == null) {
            broadcastMessage("SERVER", sb.toString());
        } else {
            client.sendMessage("SERVER", sb.toString());
        }
    }
    
    private void broadcastGameState() {
        for (ServerThread client : clients) {
            sendGameState(client);
        }
    }
    
    private void sendGameState(ServerThread client) {
        if (gameState == GameState.PLAYING) {
            StringBuilder sb = new StringBuilder();
            sb.append("Word: ");
            for (char c : revealedLetters) {
                sb.append(c).append(" ");
            }
            sb.append("\n");
            
            sb.append("Guessed: ");
            for (Character c : guessedLetters) {
                sb.append(c).append(" ");
            }
            sb.append("\n");
            
            sb.append("Guesses remaining: ").append(remainingGuesses).append("\n");
            
            if (currentTurn != null) {
                sb.append("Current turn: ").append(currentTurn.getUsername());
                if (client.equals(currentTurn)) {
                    sb.append(" (YOUR TURN)");
                }
            }
            
            client.sendMessage("GAME", sb.toString());
        } else {
            client.sendMessage("GAME", "Waiting for players to get ready...");
        }
    }
    
    @Override
    protected void sendHelpMenu(ServerThread client) {
        StringBuilder help = new StringBuilder();
        help.append("Game commands:\n");
        help.append("/ready - Mark yourself as ready to play\n");
        help.append("/start - Start the game (if enough players are ready)\n");
        help.append("/guess [letter or word] - Make a guess\n");
        help.append("/away - Mark yourself as away\n");
        help.append("/back - Return from away status\n");
        help.append("/spectate - Become a spectator\n");
        help.append("/play - Join the game as a player\n");
        help.append("/score - Show current scores\n");
        help.append("/users - List users in the room\n");
        help.append("/help - Show this help menu");
        
        client.sendMessage("SERVER", help.toString());
    }
}