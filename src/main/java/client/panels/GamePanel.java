package client.panels;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private JLabel hangmanLabel;
    private JLabel wordLabel;
    private JPanel letterGrid;

    public GamePanel() {
        setLayout(new BorderLayout());

        hangmanLabel = new JLabel("Hangman State", SwingConstants.CENTER);
        wordLabel = new JLabel("Word: _ _ _ _", SwingConstants.CENTER);
        letterGrid = new JPanel(new GridLayout(2, 13));

        for (char c = 'A'; c <= 'Z'; c++) {
            JButton letterButton = new JButton(String.valueOf(c));
            letterButton.addActionListener(e -> {
                // Send guess to server
                // Disable button after click
                letterButton.setEnabled(false);
            });
            letterGrid.add(letterButton);
        }

 add(hangmanLabel, BorderLayout.NORTH);
add(wordLabel, BorderLayout.CENTER);
add(letterGrid, BorderLayout.SOUTH);
}

public void updateGameState(String message) {
// Example parsing logic
// [GAME] HANGMAN_STATE|WORD|A_B__C|image_path
String[] parts = message.split("\\|");
if (parts.length >= 3) {
    wordLabel.setText("Word: " + parts[2]);
    // You can update hangmanLabel with image if you want to show visuals
    // Example: hangmanLabel.setIcon(new ImageIcon(parts[3]));
}
}
}
