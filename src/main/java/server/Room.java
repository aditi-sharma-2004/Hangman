package server;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {
    protected final Server server;
    protected final String roomName;
    protected final List<ServerThread> clients;
    
    public Room(Server server, String roomName) {
        this.server = server;
        this.roomName = roomName;
        this.clients = new CopyOnWriteArrayList<>();
    }
    
    public String getRoomName() {
        return roomName;
    }
    
    public List<ServerThread> getClients() {
        return new ArrayList<>(clients);
    }
    
    public int getClientCount() {
        return clients.size();
    }
    
    public synchronized boolean addClient(ServerThread client) {
        if (!clients.contains(client)) {
            clients.add(client);
            client.setCurrentRoom(this);
            broadcastMessage("SERVER", client.getUsername() + " has joined the room.");
            sendRoomInfo(client);
            return true;
        }
        return false;
    }
    
    public synchronized boolean removeClient(ServerThread client) {
        if (clients.remove(client)) {
            broadcastMessage("SERVER", client.getUsername() + " has left the room.");
            return true;
        }
        return false;
    }
    
    public void sendMessage(String sender, String message) {
        broadcastMessage(sender, message);
    }
    
    protected void broadcastMessage(String sender, String message) {
        for (ServerThread client : clients) {
            client.sendMessage(sender, message);
        }
    }
    
    protected void sendMessageToClient(ServerThread client, String sender, String message) {
        client.sendMessage(sender, message);
    }
    
    protected void sendRoomInfo(ServerThread client) {
        StringBuilder sb = new StringBuilder();
        sb.append("Room: ").append(roomName).append("\n");
        sb.append("Users: ");
        
        List<String> usernames = new ArrayList<>();
        for (ServerThread c : clients) {
            usernames.add(c.getUsername());
        }
        sb.append(String.join(", ", usernames));
        
        client.sendMessage("SERVER", sb.toString());
    }
    
    public void handleCommand(ServerThread client, String command, String[] args) {
        // Base room only handles a few commands
        switch (command.toLowerCase()) {
            case "users" -> sendRoomInfo(client);
            case "help" -> sendHelpMenu(client);
            default -> // Just treat it as a regular message
                broadcastMessage(client.getUsername(), command + " " + String.join(" ", args));
        }
    }
    
    protected void sendHelpMenu(ServerThread client) {
        StringBuilder help = new StringBuilder();
        help.append("Available commands:\n");
        help.append("/users - List users in the current room\n");
        help.append("/help - Show this help menu");
        
        client.sendMessage("SERVER", help.toString());
    }
    
    public void closeRoom(String reason) {
        broadcastMessage("SERVER", "Room closing: " + reason);
        
        // Move all clients to the lobby
        Lobby lobby = server.getLobby();
        for (ServerThread client : clients) {
            client.setCurrentRoom(null);
            lobby.addClient(client);
        }
        
        clients.clear();
    }
}