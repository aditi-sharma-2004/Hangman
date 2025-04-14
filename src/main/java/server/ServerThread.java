package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerThread extends Thread {
    private static final AtomicInteger userCounter = new AtomicInteger(1);
    
    private final Server server;
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Room currentRoom;
    private boolean running;
    
    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.clientSocket = socket;
        this.username = "User" + userCounter.getAndIncrement();
        this.running = true;
    }
    
    @Override
    public void run() {
        try {
            // Set up input and output streams
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            // Send welcome message
            sendMessage("SERVER", "Welcome to the Word Game Server!");
            sendMessage("SERVER", "Your username is " + username + ". You can change it with /username [new_name]");
            sendMessage("SERVER", "Type /help for a list of commands");
            
            
            // Process messages from client
            String inputLine;
            while (running && (inputLine = in.readLine()) != null) {
                processMessage(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Error with client " + username + ": " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    private void processMessage(String message) {
        // Check if this is a command (starts with /)
        if (message.startsWith("/")) {
            String[] parts = message.substring(1).split("\\s+", 2);
            String command = parts[0];
            String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];
            
            handleCommand(command, args);
        } else {
            // Regular message to the current room
            if (currentRoom != null) {
                currentRoom.sendMessage(username, message);
            } else {
                sendMessage("SERVER", "You are not in a room. Join the lobby first.");
            }
        }
    }
    
    // In the ServerThread class, modify the handleCommand method to add game-specific commands
    private void handleCommand(String command, String[] args) {
        System.out.println("DEBUG: Handling command: " + command); // Add debug logging
        
        switch (command.toLowerCase()) {
            case "username" -> changeUsername(args);
            case "join" -> joinRoom(args);
            case "create" -> createRoom(args);
            case "rooms" -> listRooms();
            case "help" -> sendHelpMenu();
            case "quit" -> disconnect();
            case "starthangman" -> {
                if (currentRoom != null) {
                    System.out.println("DEBUG: Forwarding starthangman command to room");
                    // For GameRoom, use different command
                    if (currentRoom instanceof GameRoom) {
                        currentRoom.handleCommand(this, "start", new String[0]);
                    } else {
                        // For standard rooms, just forward the command
                        currentRoom.handleCommand(this, command, args);
                    }
                } else {
                    sendMessage("SERVER", "You must join a game room first. Use /join [room_name]");
                }
            }
            case "guess" -> {
                if (currentRoom != null) {
                    System.out.println("DEBUG: Forwarding guess command to room");
                    currentRoom.handleCommand(this, command, args);
                } else {
                    sendMessage("SERVER", "You must join a game room first.");
                }
            }
            default -> {
                if (currentRoom != null) {
                    currentRoom.handleCommand(this, command, args);
                } else {
                    sendMessage("SERVER", "Unknown command: " + command + ". Type /help for assistance.");
                }
            }
        }
    }
    
    
    private void changeUsername(String[] args) {
        if (args.length < 1) {
            sendMessage("SERVER", "Please provide a new username");
            return;
        }
        
        String newUsername = args[0];
        if (newUsername.length() < 3 || newUsername.length() > 12) {
            sendMessage("SERVER", "Username must be between 3 and 12 characters");
            return;
        }
        
        // Notify the current room about username change
        if (currentRoom != null) {
            currentRoom.broadcastMessage("SERVER", username + " is now known as " + newUsername);
        }
        
        // Update username
        
        username = newUsername;
        
        sendMessage("SERVER", "You are now known as " + username);
    }
    
    private void joinRoom(String[] args) {
        if (args.length < 1) {
            sendMessage("SERVER", "Please specify a room name");
            return;
        }
        
        String roomName = args[0];
        Room room = server.getRoom(roomName);
        
        if (room == null) {
            sendMessage("SERVER", "Room '" + roomName + "' does not exist");
            return;
        }
        
        // Leave current room if in one
        if (currentRoom != null) {
            currentRoom.removeClient(this);
        }
        
        // Join new room
        room.addClient(this);
        sendMessage("SERVER", "You have joined room: " + roomName);
    }
    
    private void createRoom(String[] args) {
        if (args.length < 1) {
            sendMessage("SERVER", "Please specify a room name");
            return;
        }
        
        String roomName = args[0];
        if (server.createGameRoom(roomName)) {
            sendMessage("SERVER", "Created game room: " + roomName);
            
            // Auto-join the new room
            Room room = server.getRoom(roomName);
            
            // Leave current room if in one
            if (currentRoom != null) {
                currentRoom.removeClient(this);
            }
            
            // Join new room
            room.addClient(this);
        } else {
            sendMessage("SERVER", "Room '" + roomName + "' already exists");
        }
    }
    
    private void listRooms() {
        StringBuilder sb = new StringBuilder("Available rooms:\n");
        for (String roomName : server.getRoomList()) {
            Room room = server.getRoom(roomName);
            sb.append("- ").append(roomName)
              .append(" (").append(room.getClientCount()).append(" users)\n");
        }
        
        sendMessage("SERVER", sb.toString());
    }
    
    private void sendHelpMenu() {
        StringBuilder help = new StringBuilder();
        help.append("Available commands:\n");
        help.append("/username [name] - Change your username\n");
        help.append("/join [room] - Join a room\n");
        help.append("/create [room] - Create a new game room\n");
        help.append("/rooms - List available rooms\n");
        help.append("/quit - Disconnect from server\n");
        help.append("/help - Show this help menu\n\n");
        
        help.append("Once in a room, you can use room-specific commands.\n");
        help.append("Type /help in a room for more information.");
        
        sendMessage("SERVER", help.toString());
    }
    
    public void disconnect() {
        running = false;
        System.out.println("Client disconnected: " + username);
        
        // Leave current room
        if (currentRoom != null) {
            currentRoom.removeClient(this);
            currentRoom = null;
        }
        
        // Close resources
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client resources: " + e.getMessage());
        }
    }
    
    public void sendMessage(String sender, String message) {
        if (out != null && !clientSocket.isClosed()) {
            out.println("[" + sender + "] " + message);
        }
    }
    
    public String getUsername() {
        return username;
    }
    
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }
}