package view;

import java.awt.Dimension;
import javax.swing.JFrame;

public class GameFrame extends JFrame {

    private final GamePanel gamePanel;

    public GameFrame() {
        this.gamePanel = new GamePanel();
        
        this.setTitle("Hello Kitty Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(gamePanel);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public GameFrame(GamePanel panel) {
        this.gamePanel = panel;
        
        this.setTitle("Hello Kitty Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(gamePanel);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void display() {
        this.setVisible(true);
    }

    public void setWindowSize(int width, int height) {
        gamePanel.setPreferredSize(new Dimension(width, height));
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public void setFullScreen(boolean fullScreen) {
        this.dispose();
        this.setUndecorated(fullScreen);
        
        if (fullScreen) {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            this.setExtendedState(JFrame.NORMAL);
            this.pack();
            this.setLocationRelativeTo(null);
        }
        
        this.setVisible(true);
    }
}