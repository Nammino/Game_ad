package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class PausePanel {

    private final String[] pauseOptions = {
        "Continua",
        "Ricomincia Partita",
        "Impostazioni",
        "Torna ai Livelli"
    };
    private int selectedIndex = 0;

    public int getSelectedIndex() { return selectedIndex; }

    public void navigateVertical(int direction) {
        selectedIndex = (selectedIndex + direction + pauseOptions.length) % pauseOptions.length;
    }

    public void draw(Graphics2D g2, GamePanel panel) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT);

        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.YELLOW);
        String title = "PAUSA";
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(title, (GamePanel.PANEL_WIDTH - metrics.stringWidth(title)) / 2, 160);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics optionMetrics = g2.getFontMetrics();

        for (int i = 0; i < pauseOptions.length; i++) {
            String text = pauseOptions[i];
            int x = (GamePanel.PANEL_WIDTH - optionMetrics.stringWidth(text)) / 2;
            int y = 260 + (i * 50);

            if (i == selectedIndex) {
                g2.setColor(Color.CYAN);
                g2.drawString("> " + text + " <", x - 25, y);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);
            }
        }
    }
}