package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Client {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final ClientUI ui;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Initialize UI with this client instance for sending messages
        ui = new ClientUI(this);
        ui.showConnectionPanel(); // show username/host/port input screen
    }

    public void start() {
        // Start listening for server messages
        new Thread(this::listen).start();
    }

    private void listen() {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                System.out.println("[Server] " + line); // for debugging
                ui.handleServerMessage(line); // delegate message to UI
            }
        } catch (IOException e) {
            System.out.println("Connection closed.");
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Disconnected from server.");
                System.exit(0);
            });
        }
    }

    public void sendMessage(String message) {
        System.out.println("[Client] Sending: " + message); // for debugging
        out.println(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Client client = new Client("localhost", 5000);
                client.start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Unable to connect to server at localhost:5000");
                System.exit(1);
            }
        });
    }
}
