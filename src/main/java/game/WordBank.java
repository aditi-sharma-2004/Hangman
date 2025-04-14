package game;
import java.io.*;
import java.util.*;

public class WordBank {
    private List<String> words;
    private Set<String> usedWords;
    private Random rand;

    public WordBank(String filePath) {
        words = new ArrayList<>();
        usedWords = new HashSet<>();
        rand = new Random();
        loadWords(filePath);
    }

    private void loadWords(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String word;
            while ((word = br.readLine()) != null) {
                words.add(word.trim().toUpperCase());
            }
        } catch (IOException e) {
            System.out.println("Failed to load word bank: " + e.getMessage());
        }
    }

    public String getNextWord() {
        if (words.isEmpty()) return null;

        while (true) {
            String word = words.get(rand.nextInt(words.size()));
            if (!usedWords.contains(word)) {
                usedWords.add(word);
                return word;
            }
            if (usedWords.size() == words.size()) break;
        }
        return null; // All words used
    }

    public void reset() {
        usedWords.clear();
    }
}
