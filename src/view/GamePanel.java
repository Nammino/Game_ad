package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import model.GameStruct;
import model.Level;
import model.Player;
import model.World;

public class GamePanel extends JPanel {

    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;

    private GameState currentState = GameState.MENU;
    private GameStruct model;

    // Menu Principale
    private final String[] menuOptions = {"Nuova partita", "Continua partita", "Impostazioni", "Esci"};
    private int currentOptionIndex = 0;

    // Selezione Mondi e Livelli
    private int selectedWorldIndex = 0;
    private int selectedLevelIndex = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    // --- GETTER & SETTER UTILIZZATI DAL CONTROLLER ---
    public void setModel(GameStruct model) { this.model = model; }
    public GameState getCurrentState() { return currentState; }
    public void setCurrentState(GameState currentState) { this.currentState = currentState; }
    public int getCurrentOptionIndex() { return currentOptionIndex; }
    public int getSelectedWorldIndex() { return selectedWorldIndex; }
    public void setSelectedWorldIndex(int selectedWorldIndex) { this.selectedWorldIndex = selectedWorldIndex; }
    public int getSelectedLevelIndex() { return selectedLevelIndex; }
    public void setSelectedLevelIndex(int selectedLevelIndex) { this.selectedLevelIndex = selectedLevelIndex; }

    public void navigateMenu(int direction) {
        currentOptionIndex = (currentOptionIndex + direction + menuOptions.length) % menuOptions.length;
    }

    // --- RENDERING GRAFICO ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (currentState) {
            case MENU -> drawMenuScreen(g2);
            case WORLD_SELECTION -> drawWorldSelectionScreen(g2);
            case LEVEL_SELECTION -> drawLevelSelectionScreen(g2);
            case PLAYING -> drawPlayingScreen(g2);
            default -> drawPlaceholderScreen(g2, "Schermata non disponibile");
        }

        g2.dispose();
    }

    private void drawMenuScreen(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.YELLOW);
        String title = "SUPER MARIO GAME";
        g2.drawString(title, getCenteredX(g2, title), 150);

        g2.setFont(new Font("Arial", Font.BOLD, 28));
        int startY = 280;
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == currentOptionIndex) {
                g2.setColor(Color.RED);
                String text = "> " + menuOptions[i] + " <";
                g2.drawString(text, getCenteredX(g2, text), startY + (i * 50));
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(menuOptions[i], getCenteredX(g2, menuOptions[i]), startY + (i * 50));
            }
        }
    }

    private void drawWorldSelectionScreen(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        String title = "SELEZIONA IL MONDO";
        g2.drawString(title, getCenteredX(g2, title), 120);

        if (model != null && !model.getWorlds().isEmpty()) {
            World currentWorld = model.getWorlds().get(selectedWorldIndex);

            String worldHeader = "<  Mondo " + currentWorld.getId() + "  >";
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.setColor(Color.YELLOW);
            g2.drawString(worldHeader, getCenteredX(g2, worldHeader), 260);

            String worldName = currentWorld.getName();
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.setColor(Color.GREEN);
            g2.drawString(worldName, getCenteredX(g2, worldName), 320);
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.GRAY);
        String hint = "Usa SX/DX per scorrere | INVIO per confermare | ESC per il menu";
        g2.drawString(hint, getCenteredX(g2, hint), 500);
    }

    private void drawLevelSelectionScreen(Graphics2D g2) {
        World world = model.getCurrentWorld();

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(Color.WHITE);
        String title = (world != null) ? "MONDO " + world.getId() + ": " + world.getName() : "MONDO";
        g2.drawString(title, getCenteredX(g2, title), 120);

        if (world != null && !world.getLevels().isEmpty()) {
            Level selectedLevel = world.getLevels().get(selectedLevelIndex);

            String levelHeader = "<  Livello " + world.getId() + "-" + (selectedLevelIndex + 1) + "  >";
            g2.setFont(new Font("Arial", Font.BOLD, 38));
            g2.setColor(Color.CYAN);
            g2.drawString(levelHeader, getCenteredX(g2, levelHeader), 260);

            String levelFile = "Mappa: " + selectedLevel.getPath();
            g2.setFont(new Font("Arial", Font.PLAIN, 22));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(levelFile, getCenteredX(g2, levelFile), 320);
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.GRAY);
        String hint = "Usa SX/DX per scegliere | INVIO per GIOCARE | ESC indietro";
        g2.drawString(hint, getCenteredX(g2, hint), 500);
    }

    private void drawPlayingScreen(Graphics2D g2) {
        g2.setColor(new Color(107, 140, 255));
        g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        if (model == null) return;
        World world = model.getCurrentWorld();
        if (world == null || world.getLevels().isEmpty()) return;

        Level level = world.getLevels().get(selectedLevelIndex);
        ArrayList<String> map = level.getMap();

        int tileSize = GameStruct.TILE_SIZE;

        if (map != null && !map.isEmpty()) {
            for (int row = 0; row < map.size(); row++) {
                String line = map.get(row);
                for (int col = 0; col < line.length(); col++) {
                    char tileChar = line.charAt(col);
                    int x = col * tileSize;
                    int y = row * tileSize;

                    switch (tileChar) {
                        case '#' -> {
                            g2.setColor(new Color(184, 50, 0));
                            g2.fillRect(x, y, tileSize, tileSize);
                            g2.setColor(Color.BLACK);
                            g2.drawRect(x, y, tileSize, tileSize);
                        }
                        case '?' -> {
                            g2.setColor(Color.ORANGE);
                            g2.fillRect(x, y, tileSize, tileSize);
                            g2.setColor(Color.WHITE);
                            g2.setFont(new Font("Arial", Font.BOLD, 18));
                            g2.drawString("?", x + 10, y + 25);
                            g2.setColor(Color.BLACK);
                            g2.drawRect(x, y, tileSize, tileSize);
                        }
                    }
                }
            }
        }

        if (model.getPlayer() != null) {
            Player player = model.getPlayer();
            g2.setColor(Color.RED);
            g2.fillRect(
                (int) player.getPosition().getX(),
                (int) player.getPosition().getY(),
                tileSize,
                tileSize
            );
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Usa A/D per Muoverti, SPAZIO per Saltare | ESC per Uscire", 20, 30);
    }

    private void drawPlaceholderScreen(Graphics2D g2, String text) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.drawString(text, getCenteredX(g2, text), PANEL_HEIGHT / 2);
    }

    private int getCenteredX(Graphics2D g2, String text) {
        FontMetrics metrics = g2.getFontMetrics();
        return (PANEL_WIDTH - metrics.stringWidth(text)) / 2;
    }
}