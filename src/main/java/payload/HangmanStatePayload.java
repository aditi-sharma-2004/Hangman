package payload;
public class HangmanStatePayload extends Payload {
    // Fields
    private int remainingGuesses;
    private int maxGuesses;
    private String lastGuess;
    private boolean lastGuessCorrect;
    
    // Constructors
    public HangmanStatePayload() {
        super();
        setType(PayloadType.HANGMAN);
    }
    
    public HangmanStatePayload(String clientId, int remainingGuesses, 
                              int maxGuesses, String lastGuess, 
                              boolean lastGuessCorrect) {
        super(clientId, "Hangman State Update", PayloadType.HANGMAN);
        this.remainingGuesses = remainingGuesses;
        this.maxGuesses = maxGuesses;
        this.lastGuess = lastGuess;
        this.lastGuessCorrect = lastGuessCorrect;
    }
    
    // Getters and setters
    public int getRemainingGuesses() {
        return remainingGuesses;
    }
    
    public void setRemainingGuesses(int remainingGuesses) {
        this.remainingGuesses = remainingGuesses;
    }
    
    public int getMaxGuesses() {
        return maxGuesses;
    }
    
    public void setMaxGuesses(int maxGuesses) {
        this.maxGuesses = maxGuesses;
    }
    
    public String getLastGuess() {
        return lastGuess;
    }
    
    public void setLastGuess(String lastGuess) {
        this.lastGuess = lastGuess;
    }
    
    public boolean isLastGuessCorrect() {
        return lastGuessCorrect;
    }
    
    public void setLastGuessCorrect(boolean lastGuessCorrect) {
        this.lastGuessCorrect = lastGuessCorrect;
    }
    
    // Calculate the number of strikes/incorrect guesses
    public int getStrikes() {
        return maxGuesses - remainingGuesses;
    }
    
    // Check if the game is lost due to too many incorrect guesses
    public boolean isGameLost() {
        return remainingGuesses <= 0;
    }
    
    // Get percentage of guesses remaining
    public double getRemainingGuessesPercentage() {
        return (double) remainingGuesses / maxGuesses;
    }
    
    @Override
    public String toString() {
        return "HangmanStatePayload [remainingGuesses=" + remainingGuesses + 
               ", maxGuesses=" + maxGuesses + ", lastGuess=" + lastGuess + 
               ", lastGuessCorrect=" + lastGuessCorrect + ", base=" + super.toString() + "]";
    }
}