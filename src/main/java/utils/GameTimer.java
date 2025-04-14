package utils;
import java.util.TimerTask;

public class GameTimer {
    private java.util.Timer timer;
    private int seconds;
    private Runnable onTimeout;

    public GameTimer(int seconds, Runnable onTimeout) {
        this.seconds = seconds;
        this.onTimeout = onTimeout;
    }

    public void start() {
        stop();
        timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimeout.run();
            }
        }, seconds * 1000);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
