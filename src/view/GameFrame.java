package view;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    private final GamePanel gamePanel;

    public GameFrame() {
        super("Hello Kitty");

        this.gamePanel = new GamePanel();
        this.add(this.gamePanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); 
        this.setLocationRelativeTo(null); 
        this.setVisible(true);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void display() {
        this.setVisible(true);
    }
}