package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import model.GameStruct;
import model.Level;
import model.Player;
import model.World;

public class PlayingPanel {

    public void draw(Graphics2D g2, GamePanel panel, GameStruct model) {
        // Sfondo del cielo
        g2.setColor(new Color(107, 140, 255));
        g2.fillRect(0, 0, GamePanel.PANEL_WIDTH, GamePanel.PANEL_HEIGHT);

        if (model == null) return;
        World world = model.getCurrentWorld();
        if (world == null || world.getLevels().isEmpty()) return;

        Level level = world.getLevels().get(panel.getSelectedLevelIndex());
        ArrayList<String> map = level.getMap();
        Player player = model.getPlayer();

        if (player == null || map == null || map.isEmpty()) return;

        int tileSize = GameStruct.TILE_SIZE;

        // --- CALCOLO POSIZIONE CAMERA (SENZA SCATTI) ---
        // Usiamo Math.round per evitare micro-scatti dovuti all'arrotondamento dei decimali di Y e X
        int playerX = (int) Math.round(player.getPosition().getX());
        int cameraX = playerX - (GamePanel.PANEL_WIDTH / 2) + (tileSize / 2);
        int cameraY = 0; // Camera fissa in verticale per stabilità

        // Blocco camera al bordo sinistro
        if (cameraX < 0) cameraX = 0;

        // Blocco camera al bordo destro
        int maxMapWidth = map.get(0).length() * tileSize;
        if (cameraX > maxMapWidth - GamePanel.PANEL_WIDTH) {
            cameraX = Math.max(0, maxMapWidth - GamePanel.PANEL_WIDTH);
        }

        // --- TRASLAZIONE CAMERA ---
        g2.translate(-cameraX, -cameraY);

        // Disegno Mappa
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

        // Disegno Giocatore (Arrotondato con precisione al pixel)
        g2.setColor(Color.RED);
        g2.fillRect(
            (int) Math.round(player.getPosition().getX()),
            (int) Math.round(player.getPosition().getY()),
            tileSize,
            tileSize
        );

        // --- RIPRISTINO TRASLAZIONE PER L'HUD ---
        g2.translate(cameraX, cameraY);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Usa A/D per Muoverti, SPAZIO per Saltare | ESC per Uscire", 20, 30);
    }
}