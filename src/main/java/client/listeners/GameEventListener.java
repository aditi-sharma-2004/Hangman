package client.listeners;
public interface GameEventListener {
    void onGameEvent(String eventType, Object data);
}
