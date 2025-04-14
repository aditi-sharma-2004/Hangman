package game;
import java.util.*;

public class GameState {
    private String currentWord;
    private Set<Character> guessedLetters;
    private int maxStrikes;
    private int strikes;

    public GameState(String word, int maxStrikes) {
        this.currentWord = word.toUpperCase();
        this.maxStrikes = maxStrikes;
        this.strikes = 0;
        this.guessedLetters = new HashSet<>();
    }

    public boolean guessLetter(char c) {
        c = Character.toUpperCase(c);
        if (!guessedLetters.contains(c)) {
            guessedLetters.add(c);
            if (!currentWord.contains(String.valueOf(c))) {
                strikes++;
                return false;
            }
            return true;
        }
        return false; // already guessed
    }

    public boolean isGameOver() {
        return strikes >= maxStrikes || isWordGuessed();
    }

    public boolean isWordGuessed() {
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(c)) return false;
        }
        return true;
    }

    public String getDisplayWord() {
        StringBuilder sb = new StringBuilder();
        for (char c : currentWord.toCharArray()) {
            sb.append(guessedLetters.contains(c) ? c : "_").append(" ");
        }
        return sb.toString().trim();
    }

    public int getStrikes() {
        return strikes;
    }

    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public String getWord() {
        return currentWord;
    }
}
