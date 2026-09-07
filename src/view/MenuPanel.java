package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class MenuPanel {

    private final String[] menuOptions = {"Nuova partita", "Continua partita", "Impostazioni", "Esci"};

    public String[] getMenuOptions() {
        return menuOptions;
    }

    public void draw(Graphics2D g2, GamePanel panel) {
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.YELLOW);
        String title = "SUPER MARIO GAME";
        g2.drawString(title, getCenteredX(g2, title, panel.getWidth()), 150);

        g2.setFont(new Font("Arial", Font.BOLD, 28));
        int startY = 280;
        int currentOptionIndex = panel.getCurrentOptionIndex();

        for (int i = 0; i < menuOptions.length; i++) {
            if (i == currentOptionIndex) {
                g2.setColor(Color.RED);
                String text = "> " + menuOptions[i] + " <";
                g2.drawString(text, getCenteredX(g2, text, panel.getWidth()), startY + (i * 50));
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(menuOptions[i], getCenteredX(g2, menuOptions[i], panel.getWidth()), startY + (i * 50));
            }
        }
    }

    private int getCenteredX(Graphics2D g2, String text, int panelWidth) {
        FontMetrics metrics = g2.getFontMetrics();
        return (panelWidth - metrics.stringWidth(text)) / 2;
    }
}