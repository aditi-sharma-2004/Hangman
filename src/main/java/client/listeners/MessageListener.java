package client.listeners;
public interface MessageListener {
    void onMessageReceived(String messageType, String message);
}
