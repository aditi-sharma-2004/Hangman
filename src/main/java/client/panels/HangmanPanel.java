package client.panels;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class HangmanPanel extends JPanel {
    private int attemptsLeft;
    private final int MAX_ATTEMPTS = 6;
    
    public HangmanPanel() {
        setPreferredSize(new Dimension(200, 250));
        setBackground(Color.WHITE);
        attemptsLeft = MAX_ATTEMPTS;
    }
    
    public void setAttemptsLeft(int attemptsLeft) {
        this.attemptsLeft = attemptsLeft;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Set rendering hints for smoother lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2));
        
        // Draw gallows
        g2d.setColor(new Color(139, 69, 19)); // Brown
        
        // Base
        g2d.drawLine(40, 200, 160, 200);
        
        // Vertical pole
        g2d.drawLine(60, 200, 60, 50);
        
        // Top horizontal pole
        g2d.drawLine(60, 50, 120, 50);
        
        // Support
        g2d.drawLine(60, 70, 80, 50);
        
        // Rope
        g2d.drawLine(120, 50, 120, 70);
        
        // Draw hangman based on attempts left
        g2d.setColor(Color.BLACK);
        
        if (attemptsLeft < MAX_ATTEMPTS) { // Head
            g2d.drawOval(110, 70, 20, 20);
        }
        
        if (attemptsLeft < MAX_ATTEMPTS - 1) { // Body
            g2d.drawLine(120, 90, 120, 140);
        }
        
        if (attemptsLeft < MAX_ATTEMPTS - 2) { // Left arm
            g2d.drawLine(120, 100, 100, 120);
        }
        
        if (attemptsLeft < MAX_ATTEMPTS - 3) { // Right arm
            g2d.drawLine(120, 100, 140, 120);
        }
        
        if (attemptsLeft < MAX_ATTEMPTS - 4) { // Left leg
            g2d.drawLine(120, 140, 100, 170);
        }
        
        if (attemptsLeft < MAX_ATTEMPTS - 5) { // Right leg
            g2d.drawLine(120, 140, 140, 170);
        }
    }
}