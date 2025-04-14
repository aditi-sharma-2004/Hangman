package server;
public class Lobby extends Room {
    
    public Lobby(Server server) {
        super(server, "lobby");
    }
    
    @Override
    public synchronized boolean addClient(ServerThread client) {
        boolean added = super.addClient(client);
        if (added) {
            sendWelcomeMessage(client);
        }
        return added;
    }
    
    private void sendWelcomeMessage(ServerThread client) {
        StringBuilder welcome = new StringBuilder();
        welcome.append("Welcome to the Lobby!\n");
        welcome.append("This is where you can create or join game rooms.\n");
        welcome.append("Type /rooms to see available rooms.\n");
        welcome.append("Type /create [roomname] to create a new game room.\n");
        welcome.append("Type /join [roomname] to join an existing room.");
        
        client.sendMessage("SERVER", welcome.toString());
        
        // Send current room list
        listRooms(client);
    }
    
    private void listRooms(ServerThread client) {
        StringBuilder sb = new StringBuilder("Available rooms:\n");
        for (String roomName : server.getRoomList()) {
            Room room = server.getRoom(roomName);
            sb.append("- ").append(roomName)
              .append(" (").append(room.getClientCount()).append(" users)\n");
        }
        
        client.sendMessage("SERVER", sb.toString());
    }
    
    @Override
    public void handleCommand(ServerThread client, String command, String[] args) {
        switch (command.toLowerCase()) {
            case "rooms" -> listRooms(client);
            case "create" -> createRoom(client, args);
            case "join" -> joinRoom(client, args);
            default -> super.handleCommand(client, command, args);
        }
    }
    
    private void createRoom(ServerThread client, String[] args) {
        if (args.length < 1) {
            client.sendMessage("SERVER", "Please specify a room name");
            return;
        }
        
        String roomName = args[0];
        if (server.createGameRoom(roomName)) {
            client.sendMessage("SERVER", "Created game room: " + roomName);
            
            // Auto-join the new room
            Room room = server.getRoom(roomName);
            
            // Leave the lobby
            removeClient(client);
            
            // Join new room
            room.addClient(client);
        } else {
            client.sendMessage("SERVER", "Room '" + roomName + "' already exists");
        }
    }
    
    private void joinRoom(ServerThread client, String[] args) {
        if (args.length < 1) {
            client.sendMessage("SERVER", "Please specify a room name");
            return;
        }
        
        String roomName = args[0];
        Room room = server.getRoom(roomName);
        
        if (room == null) {
            client.sendMessage("SERVER", "Room '" + roomName + "' does not exist");
            return;
        }
        
        if (room == this) {
            client.sendMessage("SERVER", "You are already in the lobby");
            return;
        }
        
        // Leave the lobby
        removeClient(client);
        
        // Join new room
        room.addClient(client);
    }
    
    @Override
    protected void sendHelpMenu(ServerThread client) {
        StringBuilder help = new StringBuilder();
        help.append("Lobby commands:\n");
        help.append("/rooms - List available rooms\n");
        help.append("/create [roomname] - Create a new game room\n");
        help.append("/join [roomname] - Join an existing room\n");
        help.append("/username [name] - Change your username\n");
        help.append("/quit - Disconnect from server\n");
        help.append("/help - Show this help menu");
        
        client.sendMessage("SERVER", help.toString());
    }
    
    @Override
    public void closeRoom(String reason) {
        // The lobby can't be closed - it's a permanent room
        broadcastMessage("SERVER", "The lobby cannot be closed");
    }
}