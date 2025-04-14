package client.panels;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class UserListPanel extends JPanel {
    private JTextArea userListArea;

    public UserListPanel() {
        setLayout(new BorderLayout());
        userListArea = new JTextArea();
        userListArea.setEditable(false);
        add(new JScrollPane(userListArea), BorderLayout.CENTER);
        setPreferredSize(new Dimension(150, 0));
    }

    public void updateUserList(String message) {
        // Example message: [USER] Alice:Ready, Bob:Spectator, Carol:InGame
        String users = message.replace("[USER] ", "").trim();
        userListArea.setText("");
        for (String user : users.split(",")) {
            userListArea.append(user.trim() + "\n");
        }
    }
}
