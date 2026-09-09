package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.File;
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

            File f = new File(selectedLevel.getPath());
            String rawName = f.getName().replaceFirst("\\.txt$", "");
            String levelTitle = rawName.replaceAll("^\\d+_", "").replace("_", " ");
            if (!levelTitle.isEmpty()) {
                levelTitle = levelTitle.substring(0, 1).toUpperCase() + levelTitle.substring(1);
            }

            g2.setFont(new Font("Arial", Font.ITALIC, 20));
            g2.setColor(Color.LIGHT_GRAY);

            int titleWidth = g2.getFontMetrics().stringWidth(levelTitle);
            int titleX = (panel.getWidth() - titleWidth) / 2;
            int titleY = 310; 

            g2.drawString(levelTitle, titleX, titleY);

            if (selectedLevel.isCompleted()) {
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.setColor(new Color(0, 255, 120));
                String completedMsg = "[ COMPLETATO ]";
                g2.drawString(completedMsg, getCenteredX(g2, completedMsg, panel.getWidth()), 360);
            }
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.GRAY);
    }

    private int getCenteredX(Graphics2D g2, String text, int panelWidth) {
        FontMetrics metrics = g2.getFontMetrics();
        return (panelWidth - metrics.stringWidth(text)) / 2;
    }
}