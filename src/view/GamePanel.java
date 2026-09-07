package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import model.GameStruct;

public class GamePanel extends JPanel {

    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;

    private GameState currentState = GameState.MENU;
    private GameStruct model;

    // Riferimenti alle componenti delle sotto-schermate
    private final MenuPanel menuPanel;
    private final WorldSelectionPanel worldSelectionPanel;
    private final LevelSelectionPanel levelSelectionPanel;
    private final PlayingPanel playingPanel;

    // Indici di stato
    private int currentOptionIndex = 0;
    private int selectedWorldIndex = 0;
    private int selectedLevelIndex = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        // Inizializzazione delle classi Panel
        this.menuPanel = new MenuPanel();
        this.worldSelectionPanel = new WorldSelectionPanel();
        this.levelSelectionPanel = new LevelSelectionPanel();
        this.playingPanel = new PlayingPanel();
    }

    // --- GETTER & SETTER ---
    public void setModel(GameStruct model) { this.model = model; }
    public GameState getCurrentState() { return currentState; }
    public void setCurrentState(GameState currentState) { this.currentState = currentState; }
    public int getCurrentOptionIndex() { return currentOptionIndex; }
    public int getSelectedWorldIndex() { return selectedWorldIndex; }
    public void setSelectedWorldIndex(int selectedWorldIndex) { this.selectedWorldIndex = selectedWorldIndex; }
    public int getSelectedLevelIndex() { return selectedLevelIndex; }
    public void setSelectedLevelIndex(int selectedLevelIndex) { this.selectedLevelIndex = selectedLevelIndex; }

    public void navigateMenu(int direction) {
        int optionsCount = menuPanel.getMenuOptions().length;
        currentOptionIndex = (currentOptionIndex + direction + optionsCount) % optionsCount;
    }

    // --- RENDERING PRINCIPALE ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (currentState) {
            case MENU -> menuPanel.draw(g2, this);
            case WORLD_SELECTION -> worldSelectionPanel.draw(g2, this, model);
            case LEVEL_SELECTION -> levelSelectionPanel.draw(g2, this, model);
            case PLAYING -> playingPanel.draw(g2, this, model);
            default -> drawPlaceholderScreen(g2, "Schermata non disponibile");
        }

        g2.dispose();
    }

    private void drawPlaceholderScreen(Graphics2D g2, String text) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics metrics = g2.getFontMetrics();
        int x = (PANEL_WIDTH - metrics.stringWidth(text)) / 2;
        g2.drawString(text, x, PANEL_HEIGHT / 2);
    }
}