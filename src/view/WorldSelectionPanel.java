package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import model.GameStruct;
import model.World;

public class WorldSelectionPanel {

    private int selectedWorldIndex = 0;
    private final ArrayList<Rectangle> worldBounds = new ArrayList<>();
    private final Rectangle backButtonBounds = new Rectangle();

    public WorldSelectionPanel() {}

    public int getSelectedWorldIndex() { return selectedWorldIndex; }

    public void navigateVertical(int direction, GameStruct model) {
        if (model == null) return;
        int totalWorlds = model.getWorlds().size();
        int total = totalWorlds + 1;
        if (total == 1) return;
        selectedWorldIndex = (selectedWorldIndex + direction + total) % total;
    }

    public void draw(Graphics2D g2, GamePanel panel, GameStruct model) {
        if (model == null) return;
        ArrayList<World> worlds = model.getWorlds();

        while (worldBounds.size() < worlds.size()) {
            worldBounds.add(new Rectangle());
        }

        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.YELLOW);
        String title = "SELEZIONE MONDO";
        g2.drawString(title, getCenteredX(g2, title, panel.getWidth()), 100);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics metrics = g2.getFontMetrics();

        for (int i = 0; i < worlds.size(); i++) {
            String text = worlds.get(i).getName();
            int textWidth = metrics.stringWidth(text);
            int x = (panel.getWidth() - textWidth) / 2;
            int y = 220 + (i * 60);

            worldBounds.get(i).setBounds(x - 20, y - metrics.getAscent(), textWidth + 40, metrics.getHeight() + 10);

            if (i == selectedWorldIndex) {
                g2.setColor(Color.GREEN);
                g2.drawString("> " + text + " <", x - 25, y);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);
            }
        }

        String backText = "< Torna al Menu Principale >";
        int backX = getCenteredX(g2, backText, panel.getWidth());
        int backY = 220 + (worlds.size() * 60) + 40;
        backButtonBounds.setBounds(backX, backY - metrics.getAscent(), metrics.stringWidth(backText), metrics.getHeight());

        if (selectedWorldIndex == worlds.size()) {
            g2.setColor(Color.CYAN);
            g2.drawString("> " + backText + " <", backX - 20, backY);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(backText, backX, backY);
        }
    }

    public boolean handleMouseClick(Point mousePoint, GamePanel panel, GameStruct model) {
        if (model == null) return false;
        ArrayList<World> worlds = model.getWorlds();

        if (backButtonBounds.contains(mousePoint)) {
            panel.setCurrentState(GameState.MENU);
            return true;
        }

        for (int i = 0; i < worlds.size(); i++) {
            if (worldBounds.get(i).contains(mousePoint)) {
                selectedWorldIndex = i;
                model.changeWorld(i);
                panel.setSelectedLevelIndex(0);
                panel.setCurrentState(GameState.LEVEL_SELECTION);
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