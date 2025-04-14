package payload;
import java.util.HashMap;
import java.util.Map;

public class PointsPayload extends Payload {
    // Fields
    private Map<String, Integer> playerPoints;
    
    // Constructors
    public PointsPayload() {
        super();
        setType(PayloadType.POINTS);
        playerPoints = new HashMap<>();
    }
    
    public PointsPayload(String clientId, Map<String, Integer> playerPoints) {
        super(clientId, "Points Update", PayloadType.POINTS);
        this.playerPoints = playerPoints;
    }
    
    // Getters and setters
    public Map<String, Integer> getPlayerPoints() {
        return playerPoints;
    }
    
    public void setPlayerPoints(Map<String, Integer> playerPoints) {
        this.playerPoints = playerPoints;
    }
    
    // Add points for a specific player
    public void addPlayerPoints(String playerName, int points) {
        playerPoints.put(playerName, points);
    }
    
    // Get points for a specific player
    public int getPointsForPlayer(String playerName) {
        return playerPoints.getOrDefault(playerName, 0);
    }
    
    @Override
    public String toString() {
        return "PointsPayload [playerPoints=" + playerPoints + ", base=" + super.toString() + "]";
    }
}