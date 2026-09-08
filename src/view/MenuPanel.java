package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class MenuPanel {

    private final String[] menuOptions = {"Nuova Partita", "Continua Partita", "Impostazioni", "Esci"};

    public String[] getMenuOptions() {
        return menuOptions;
    }

    public void draw(Graphics2D g2, GamePanel panel) {
        g2.setFont(new Font("Arial", Font.BOLD, 42));
        g2.setColor(Color.YELLOW);
        String title = "HELLO KITTY GAME";
        g2.drawString(title, getCenteredX(g2, title, panel.getWidth()), 130);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        int startY = 260;
        int currentOptionIndex = panel.getCurrentOptionIndex();

        for (int i = 0; i < menuOptions.length; i++) {
            String text = menuOptions[i];
            int y = startY + (i * 55);

            if (i == currentOptionIndex) {
                g2.setColor(Color.RED);
                String selectedText = "> " + text + " <";
                g2.drawString(selectedText, getCenteredX(g2, selectedText, panel.getWidth()), y);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(text, getCenteredX(g2, text, panel.getWidth()), y);
            }
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.GRAY);
        String hint = "Usa le FRECCE per spostarsi e PREMI ENTER per selezionare";
        g2.drawString(hint, getCenteredX(g2, hint, panel.getWidth()), 530);
    }

    private int getCenteredX(Graphics2D g2, String text, int panelWidth) {
        FontMetrics metrics = g2.getFontMetrics();
        return (panelWidth - metrics.stringWidth(text)) / 2;
    }
}