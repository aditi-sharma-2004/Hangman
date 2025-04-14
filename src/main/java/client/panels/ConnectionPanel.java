package client.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.Client;

public class ConnectionPanel extends JPanel {
    private final JTextField usernameField;
    private final JTextField hostField;
    private final JTextField portField;
    private final JButton connectButton;

    public ConnectionPanel(Client client) {
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField("player1");
        add(usernameField);

        add(new JLabel("Host:"));
        hostField = new JTextField("localhost");
        add(hostField);

        add(new JLabel("Port:"));
        portField = new JTextField("5000");
        add(portField);

        connectButton = new JButton("Connect");
        connectButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String host = hostField.getText();
            int port = Integer.parseInt(portField.getText());

            // Send connect message to server
            client.sendMessage("[CONNECT]" + username);
        });

        add(connectButton);
    }
}
