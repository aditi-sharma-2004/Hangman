package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int DEFAULT_PORT = 5000;
    private int port;
    private ServerSocket serverSocket;
    private boolean running;
    private final Map<String, Room> rooms;
    private final Lobby lobby;
    
    public Server() {
        this(DEFAULT_PORT);
    }
    
    public Server(int port) {
        this.port = port;
        this.rooms = new ConcurrentHashMap<>();
        this.lobby = new Lobby(this);
        this.rooms.put("lobby", lobby);
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Server started on port " + port);
            
            // Accept client connections
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());
                    
                    ServerThread serverThread = new ServerThread(this, clientSocket);
                    serverThread.start();
                    
                    // Add the client to the lobby
                    lobby.addClient(serverThread);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }
    
    public void shutdown() {
        running = false;
        System.out.println("Shutting down server...");
        
        // Close all rooms
        for (Room room : rooms.values()) {
            room.closeRoom("Server shutting down");
        }
        
        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }
    
    public Lobby getLobby() {
        return lobby;
    }
    
    public Room getRoom(String roomName) {
        return rooms.get(roomName);
    }
    
    // In Server class
public boolean createGameRoom(String roomName) {
    if (rooms.containsKey(roomName)) {
        return false;
    }
    
    // Create a GameRoom instead of a Room
    GameRoom room = new GameRoom(this, roomName);
    rooms.put(roomName, room);
    return true;
}
    
    public boolean removeRoom(String roomName) {
        if (roomName.equals("lobby") || !rooms.containsKey(roomName)) {
            return false;
        }
        
        Room room = rooms.get(roomName);
        room.closeRoom("Room closed by server");
        rooms.remove(roomName);
        return true;
    }
    
    public List<String> getRoomList() {
        return new ArrayList<>(rooms.keySet());
    }
    
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default: " + DEFAULT_PORT);
            }
        }
        
        Server server = new Server(port);
        server.start();
    }
}