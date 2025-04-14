package client.panels;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import client.Client;

public class ChatPanel extends JPanel {
    private final JTextArea chatArea;
    private JTextField inputField;
    private Client client;

    public ChatPanel(Client client) {
        this.client = client;
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        inputField = new JTextField();
        inputField.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                client.sendMessage("[CHAT] " + text);
                inputField.setText("");
            }
        });

        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
    }

    public void appendMessage(String message) {
        String cleanMessage = message.replace("[CHAT] ", "");
        chatArea.append(cleanMessage + "\n");
    }
}
