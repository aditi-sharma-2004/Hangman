package game;
public class Player {
    private String name;
    private int points;
    private String status; // "ready", "away", "spectator"
    private boolean isTurn;

    public Player(String name) {
        this.name = name;
        this.points = 0;
        this.status = "ready";
        this.isTurn = false;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int p) {
        points += p;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
        this.status = s;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean t) {
        isTurn = t;
    }

    @Override
    public String toString() {
        return name + ":" + status + " (" + points + " pts)";
    }
}
