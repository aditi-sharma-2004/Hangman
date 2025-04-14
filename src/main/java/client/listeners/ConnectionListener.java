package client.listeners;
public interface ConnectionListener {
    void onConnect();
    void onDisconnect();
    void onConnectionError(String errorMessage);
}
