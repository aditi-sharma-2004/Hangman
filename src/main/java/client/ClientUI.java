package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientUI {
    private final Client client;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    // Gameplay-specific components for Hangman
    private JPanel gamePanel;
    private JButton startGameBtn;
    private JButton submitGuessBtn;
    private JTextField guessField;
    private JLabel gameStatusLabel;
    private JLabel wordProgressLabel;
    private JLabel incorrectGuessesLabel;
    private int remainingGuesses;
    private String wordToGuess;

    public ClientUI(Client client) {
        this.client = client;
    }

    public void showConnectionPanel() {
        JTextField usernameField = new JTextField();
        JTextField hostField = new JTextField("localhost");
        JTextField portField = new JTextField("5000");

        Object[] message = {
            "Username:", usernameField,
            "Host:", hostField,
            "Port:", portField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Connect to Server", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String host = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());

            client.sendMessage("/username " + username);
            buildChatUI(username);
        } else {
            System.exit(0);
        }
    }

    private void buildChatUI(String username) {
        frame = new JFrame("Hangman - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout(10, 10)); // spacing between components
    
        // Chat area in center
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
    
        // Input and send at bottom
        inputField = new JTextField();
        sendButton = new JButton("Send");
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
    
        // Room buttons at top
        JPanel actionPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton createBtn = new JButton("➕ Create Room");
        JButton joinBtn = new JButton("✅ Join Room");
        JButton leaveBtn = new JButton("🚪 Leave Room");
        JButton quitBtn = new JButton("❌ Quit");
        actionPanel.add(createBtn);
        actionPanel.add(joinBtn);
        actionPanel.add(leaveBtn);
        actionPanel.add(quitBtn);
    
        // --- Gameplay Panel ---
        gamePanel = new JPanel(new GridLayout(6, 1, 5, 5));
        gamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hangman Game"));
    
        startGameBtn = new JButton("Start Game");
        wordProgressLabel = new JLabel("Word: _ _ _ _");
        guessField = new JTextField();
        submitGuessBtn = new JButton("Submit Guess");
        gameStatusLabel = new JLabel("Guesses Remaining: 6");
        incorrectGuessesLabel = new JLabel("Incorrect Guesses: ");
    
        gamePanel.add(startGameBtn);
        gamePanel.add(wordProgressLabel);
        gamePanel.add(guessField);
        gamePanel.add(submitGuessBtn);
        gamePanel.add(gameStatusLabel);
        gamePanel.add(incorrectGuessesLabel);
    
        gamePanel.setVisible(false); // hidden until room is joined/created
    
        // Add all panels to frame
        frame.add(actionPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(gamePanel, BorderLayout.EAST); // game panel on right
    
        frame.setVisible(true);
    
        // --- Button Actions ---
        sendButton.addActionListener(e -> send());
        inputField.addActionListener(e -> send());
    
        createBtn.addActionListener(e -> {
            String roomName = JOptionPane.showInputDialog(frame, "Enter room name to create:");
            if (roomName != null && !roomName.isBlank()) {
                client.sendMessage("/create " + roomName);
                gamePanel.setVisible(true);
                frame.revalidate(); frame.repaint(); // refresh UI
            }
        });
    
        joinBtn.addActionListener(e -> {
            String roomName = JOptionPane.showInputDialog(frame, "Enter room name to join:");
            if (roomName != null && !roomName.isBlank()) {
                client.sendMessage("/join " + roomName);
                gamePanel.setVisible(true);
                frame.revalidate(); frame.repaint(); // refresh UI
            }
        });
    
        leaveBtn.addActionListener(e -> client.sendMessage("/leave"));
    
        quitBtn.addActionListener(e -> {
            client.sendMessage("/quit");
            frame.dispose();
            System.exit(0);
        });
    
        startGameBtn.addActionListener(e -> startGame());
        submitGuessBtn.addActionListener(e -> submitGuess());
    }
    

    private void send() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            client.sendMessage(text);
            inputField.setText("");
        }
    }

    private void startGame() {
        // Server will send the word to guess
        client.sendMessage("/starthangman");

        // Make the game panel visible when the game starts
        gamePanel.setVisible(true);

        // Optionally, hide the "Start Game" button
        startGameBtn.setVisible(false);
    }

    private void submitGuess() {
        String guess = guessField.getText().trim().toUpperCase();
        if (guess.length() == 1) {
            client.sendMessage("/guess " + guess);
            guessField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter a valid letter!");
        }
    }

    public void handleServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            // Example message format: "Word: _ _ _", "Incorrect guesses: A, B", "Guesses remaining: 5"
            if (message.startsWith("Word:")) {
                wordToGuess = message.substring(6);  // Extract word (e.g., "_ _ _ _")
                wordProgressLabel.setText("Word: " + wordToGuess);
            } else if (message.startsWith("Incorrect guesses:")) {
                incorrectGuessesLabel.setText("Incorrect Guesses: " + message.substring(18));
            } else if (message.startsWith("Guesses Remaining:")) {
                remainingGuesses = Integer.parseInt(message.substring(19));
                gameStatusLabel.setText("Guesses Remaining: " + remainingGuesses);
            } else if (message.equals("Game Over")) {
                JOptionPane.showMessageDialog(frame, "Game Over! You lost.");
                resetGame();
            } else if (message.equals("You Win!")) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You won.");
                resetGame();
            } else {
                chatArea.append(message + "\n");
            }
        });
    }

    private void resetGame() {
        wordProgressLabel.setText("Word: _ _ _ _");
        incorrectGuessesLabel.setText("Incorrect Guesses: ");
        gameStatusLabel.setText("Guesses Remaining: 6");
        guessField.setText("");
    }
}
