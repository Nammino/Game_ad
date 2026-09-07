package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import model.GameStruct;
import model.Level;
import model.World;

public class LevelSelectionPanel {

    public void draw(Graphics2D g2, GamePanel panel, GameStruct model) {
        World world = (model != null) ? model.getCurrentWorld() : null;

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(Color.WHITE);
        String title = (world != null) ? "MONDO " + world.getId() + ": " + world.getName() : "MONDO";
        g2.drawString(title, getCenteredX(g2, title, panel.getWidth()), 120);

        if (world != null && !world.getLevels().isEmpty()) {
            Level selectedLevel = world.getLevels().get(panel.getSelectedLevelIndex());

            String levelHeader = "<  Livello " + world.getId() + "-" + (panel.getSelectedLevelIndex() + 1) + "  >";
            g2.setFont(new Font("Arial", Font.BOLD, 38));
            g2.setColor(Color.CYAN);
            g2.drawString(levelHeader, getCenteredX(g2, levelHeader, panel.getWidth()), 260);

            String levelFile = "Mappa: " + selectedLevel.getPath();
            g2.setFont(new Font("Arial", Font.PLAIN, 22));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(levelFile, getCenteredX(g2, levelFile, panel.getWidth()), 320);
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.GRAY);
        String hint = "Usa SX/DX per scegliere | INVIO per GIOCARE | ESC indietro";
        g2.drawString(hint, getCenteredX(g2, hint, panel.getWidth()), 500);
    }

    private int getCenteredX(Graphics2D g2, String text, int panelWidth) {
        FontMetrics metrics = g2.getFontMetrics();
        return (panelWidth - metrics.stringWidth(text)) / 2;
    }
}