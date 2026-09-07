package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import model.GameStruct;
import model.World;

public class WorldSelectionPanel {

    public void draw(Graphics2D g2, GamePanel panel, GameStruct model) {
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        String title = "SELEZIONA IL MONDO";
        g2.drawString(title, getCenteredX(g2, title, panel.getWidth()), 120);

        if (model != null && !model.getWorlds().isEmpty()) {
            World currentWorld = model.getWorlds().get(panel.getSelectedWorldIndex());

            String worldHeader = "<  Mondo " + currentWorld.getId() + "  >";
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.setColor(Color.YELLOW);
            g2.drawString(worldHeader, getCenteredX(g2, worldHeader, panel.getWidth()), 260);

            String worldName = currentWorld.getName();
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.setColor(Color.GREEN);
            g2.drawString(worldName, getCenteredX(g2, worldName, panel.getWidth()), 320);
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.GRAY);
        String hint = "Usa SX/DX per scorrere | INVIO per confermare | ESC per il menu";
        g2.drawString(hint, getCenteredX(g2, hint, panel.getWidth()), 500);
    }

    private int getCenteredX(Graphics2D g2, String text, int panelWidth) {
        FontMetrics metrics = g2.getFontMetrics();
        return (panelWidth - metrics.stringWidth(text)) / 2;
    }
}