package client.panels;
import javax.swing.*;
import java.awt.*;

public class GameEventPanel extends JPanel {
    private JTextArea eventArea;

    public GameEventPanel() {
        setLayout(new BorderLayout());
        eventArea = new JTextArea();
        eventArea.setEditable(false);
        eventArea.setLineWrap(true);
        eventArea.setWrapStyleWord(true);
        add(new JScrollPane(eventArea), BorderLayout.CENTER);
        setPreferredSize(new Dimension(0, 100));
    }

    public void appendEvent(String message) {
        // Example: [EVENT] Alice guessed A - Incorrect!
        String cleanMessage = message.replace("[EVENT] ", "");
        eventArea.append(cleanMessage + "\n");
    }
}

