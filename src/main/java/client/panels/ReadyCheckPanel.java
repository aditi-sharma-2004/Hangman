package client.panels;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import client.Client;
public class ReadyCheckPanel extends JPanel {
    private JCheckBox readyBox, spectatorBox, awayBox;
    private JButton startButton;
    private Client client;

    public ReadyCheckPanel(Client client) {
        this.client = client;
        setLayout(new FlowLayout());

        readyBox = new JCheckBox("Ready");
        spectatorBox = new JCheckBox("Spectator");
        awayBox = new JCheckBox("Away");
        startButton = new JButton("Start Game");

        readyBox.addActionListener(e -> client.sendMessage("/ready"));
        spectatorBox.addActionListener(e -> client.sendMessage("/spectate"));
        awayBox.addActionListener(e -> client.sendMessage("/away"));
        startButton.addActionListener(e -> client.sendMessage("/start"));

        add(readyBox);
        add(spectatorBox);
        add(awayBox);
        add(startButton);
    }
}
