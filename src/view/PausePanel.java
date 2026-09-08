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
        int width = panel.getWidth();
        int height = panel.getHeight();

        // Copre interamente lo schermo dinamico
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, width, height);

        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.YELLOW);
        String title = "PAUSA";
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(title, (width - metrics.stringWidth(title)) / 2, height / 2 - 100);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics optionMetrics = g2.getFontMetrics();

        int startY = height / 2 - 30;
        for (int i = 0; i < pauseOptions.length; i++) {
            String text = pauseOptions[i];
            int x = (width - optionMetrics.stringWidth(text)) / 2;
            int y = startY + (i * 50);

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