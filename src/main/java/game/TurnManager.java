package game;
import java.util.*;

public class TurnManager {
    private List<Player> players;
    private int currentIndex;

    public TurnManager(List<Player> players) {
        this.players = players;
        this.currentIndex = -1;
    }

    public Player getNextPlayer() {
        if (players.isEmpty()) return null;

        int count = 0;
        while (count < players.size()) {
            currentIndex = (currentIndex + 1) % players.size();
            Player p = players.get(currentIndex);
            if (p.getStatus().equalsIgnoreCase("ready")) {
                return p;
            }
            count++;
        }
        return null; // no ready players
    }

    public void resetTurns() {
        currentIndex = -1;
    }

    public void removePlayer(Player p) {
        players.remove(p);
        if (currentIndex >= players.size()) currentIndex = 0;
    }

    public List<Player> getAllPlayers() {
        return players;
    }
}
