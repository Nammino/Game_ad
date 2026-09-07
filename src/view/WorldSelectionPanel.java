package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class WorldSelectionPanel {

    private final String[] worlds = {"Mondo 1: Prato Verde", "Mondo 2: Isola Dolce", "Mondo 3: Castello Rosa"};
    private int selectedWorldIndex = 0;

    private final Rectangle[] worldBounds = new Rectangle[worlds.length];
    private final Rectangle backButtonBounds = new Rectangle();

    public WorldSelectionPanel() {
        for (int i = 0; i < worldBounds.length; i++) {
            worldBounds[i] = new Rectangle();
        }
    }

    public int getSelectedWorldIndex() { return selectedWorldIndex; }

    public void navigateVertical(int direction) {
        int total = worlds.length + 1; // Mondi + Tasto Torna indietro
        selectedWorldIndex = (selectedWorldIndex + direction + total) % total;
    }

    public void draw(Graphics2D g2, GamePanel panel) {
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.YELLOW);
        String title = "SELEZIONE MONDO";
        g2.drawString(title, getCenteredX(g2, title, panel.getWidth()), 100);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics metrics = g2.getFontMetrics();

        // Disegno lista mondi
        for (int i = 0; i < worlds.length; i++) {
            String text = worlds[i];
            int textWidth = metrics.stringWidth(text);
            int x = (panel.getWidth() - textWidth) / 2;
            int y = 220 + (i * 60);

            worldBounds[i].setBounds(x - 20, y - metrics.getAscent(), textWidth + 40, metrics.getHeight() + 10);

            if (i == selectedWorldIndex) {
                g2.setColor(Color.GREEN);
                g2.drawString("> " + text + " <", x - 25, y);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);
            }
        }

        // Tasto Torna al Menu
        String backText = "< Torna al Menu Principale >";
        int backX = getCenteredX(g2, backText, panel.getWidth());
        int backY = 480;
        backButtonBounds.setBounds(backX, backY - metrics.getAscent(), metrics.stringWidth(backText), metrics.getHeight());

        if (selectedWorldIndex == worlds.length) {
            g2.setColor(Color.CYAN);
            g2.drawString("> " + backText + " <", backX - 20, backY);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(backText, backX, backY);
        }
    }


    public boolean handleMouseClick(Point mousePoint, GamePanel panel) {
        if (backButtonBounds.contains(mousePoint)) {
            panel.setCurrentState(GameState.MENU);
            return true;
        }
        for (int i = 0; i < worlds.length; i++) {
            if (worldBounds[i].contains(mousePoint)) {
                selectedWorldIndex = i;
                panel.setSelectedLevelIndex(0); // Resetta l'indice del livello
                panel.setCurrentState(GameState.LEVEL_SELECTION); // <--- PASSA ALLA SELEZIONE LIVELLO
                return true;
            }
        }
        return false;
    }

    private int getCenteredX(Graphics2D g2, String text, int panelWidth) {
        FontMetrics metrics = g2.getFontMetrics();
        return (panelWidth - metrics.stringWidth(text)) / 2;
    }
}