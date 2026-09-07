package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import model.Collectible;
import model.Enemy;
import model.Entity;
import model.Goal;
import model.GameStruct;
import model.Level;
import model.NeutralObject;
import model.Player;
import model.World;

public class PlayingPanel {

    public void draw(Graphics2D g2, GamePanel panel, GameStruct model) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // --- SFONDO ---
        g2.setColor(new Color(107, 140, 255));
        g2.fillRect(0, 0, panelWidth, panelHeight);

        if (model == null) return;
        World world = model.getCurrentWorld();
        if (world == null || world.getLevels().isEmpty()) return;

        Level level = world.getLevels().get(panel.getSelectedLevelIndex());
        ArrayList<String> map = level.getMap();
        Player player = model.getPlayer();

        if (player == null || map == null || map.isEmpty()) return;

        // --- CONTROLLO MORTE / RESET ---
        if (player.getHealth() <= 0) {
            panel.resetPlayerPosition();
            player.resetHealth();
            return;
        }

        int tileSize = GameStruct.TILE_SIZE;

     // --- CAMERA ---
        int playerX = (int) Math.round(player.getPosition().getX());
        int playerY = (int) Math.round(player.getPosition().getY()); 
        
        int cameraX = (int) Math.round(playerX - (panelWidth / 2.0) + (tileSize / 2.0));
        int cameraY = (int) Math.round(playerY - (panelHeight / 2.0) + (tileSize / 2.0));

        // Limiti orizzontali
        if (cameraX < 0) cameraX = 0;
        int maxMapWidth = map.get(0).length() * tileSize;
        if (cameraX > maxMapWidth - panelWidth) {
            cameraX = Math.max(0, maxMapWidth - panelWidth);
        }

        // --- LIMITI VERTICALI STABILI ---
        int maxMapHeight = map.size() * tileSize;
        
        // Se la mappa è più piccola o uguale all'altezza del pannello, 
        // blocchiamo la camera fissa a 0 ed evitiamo qualsiasi movimento verticale superfluo!
        if (maxMapHeight <= panelHeight) {
            cameraY = 0;
        } else {
            if (cameraY < 0) cameraY = 0;
            if (cameraY > maxMapHeight - panelHeight) {
                cameraY = maxMapHeight - panelHeight;
            }
        }

        g2.translate(-cameraX, -cameraY);

        // Disegno Mappa
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

     // --- DISEGNO E GESTIONE DELLE ENTITÀ (Neutri, Collezionabili, Goal e Nemici) ---
        if (level.getEntities() != null) {
            
            // 1. Prima passata: Disegno gli Oggetti Neutri (Sfondo / Decorativi)
            for (Entity entity : level.getEntities()) {
                if (entity instanceof NeutralObject neutral) {
                    int nx = (int) Math.round(neutral.getPosition().getX());
                    int ny = (int) Math.round(neutral.getPosition().getY());

                    // Disegno l'oggetto neutro di secondo piano (es. Grigio semitrasarente)
                    g2.setColor(new Color(120, 120, 120, 160));
                    g2.fillRect(nx, ny, tileSize, tileSize);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawRect(nx, ny, tileSize, tileSize);
                }
            }

            // 2. Seconda passata: Gestione e rimozione dei Collezionabili
            level.getEntities().removeIf(entity -> {
                if (entity instanceof Collectible col) {
                    col.update(player); // Aggiorna lo stato di raccolta
                    
                    int cx = (int) Math.round(col.getPosition().getX());
                    int cy = (int) Math.round(col.getPosition().getY());

                    if (!col.isCollected()) {
                        // Disegna il collezionabile (es. Giallo brillante con una 'C')
                        g2.setColor(Color.YELLOW);
                        g2.fillRect(cx, cy, tileSize, tileSize);
                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("Arial", Font.BOLD, 14));
                        g2.drawString("C", cx + 10, cy + 22);
                        g2.drawRect(cx, cy, tileSize, tileSize);
                    }
                    return col.isCollected(); // Se è raccolto, viene rimosso dalla lista
                }
                return false;
            });

            // 3. Terza passata: Gestione di Goal e Nemici
            for (Entity entity : level.getEntities()) {
                
                // Traguardo (Goal)
                if (entity instanceof Goal goal) {
                    int gx = (int) Math.round(goal.getPosition().getX());
                    int gy = (int) Math.round(goal.getPosition().getY());

                    g2.setColor(new Color(0, 200, 100));
                    g2.fillRect(gx, gy, tileSize, tileSize);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    g2.drawString("D", gx + 11, gy + 24);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(gx, gy, tileSize, tileSize);

                    if (player.getBoundingBox().intersects(goal.getBoundingBox())) {
                        level.setCompleted(true);
                    }
                }
                
                // Nemico (Enemy)
                else if (entity instanceof Enemy enemy) {
                    int ex = (int) Math.round(enemy.getPosition().getX());
                    int ey = (int) Math.round(enemy.getPosition().getY());

                    // Disegno il nemico (Viola/Magenta con una 'E')
                    g2.setColor(new Color(150, 0, 150));
                    g2.fillRect(ex, ey, tileSize, tileSize);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    g2.drawString("E", ex + 10, ey + 24);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(ex, ey, tileSize, tileSize);

                    // Se il giocatore tocca il nemico, gli toglie vita
                    if (player.getBoundingBox().intersects(enemy.getBoundingBox())) {
                        player.setHealth(player.getHealth() - 1); 
                    }
                }
            }
        }

        // Disegno Giocatore
        g2.setColor(Color.RED);
        g2.fillRect(
            (int) Math.round(player.getPosition().getX()),
            (int) Math.round(player.getPosition().getY()),
            tileSize,
            tileSize
        );

        // --- RIPRISTINO CAMERA PER L'HUD E I MESSAGGI FISSI ---
        g2.translate(cameraX, cameraY);

        // --- HUD / BARRA VITA ---
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Usa A/D per Muoverti, SPAZIO per Saltare | ESC per Uscire", 20, 25);

        int barX = 20;
        int barY = 35;
        int barWidth = 180;
        int barHeight = 18;

        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRect(barX, barY, barWidth, barHeight);

        g2.setColor(Color.RED);
        int currentHealthWidth = (int) (barWidth * ((double) player.getHealth() / player.getMaxHealth()));
        g2.fillRect(barX, barY, Math.max(0, currentHealthWidth), barHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("HP: " + player.getHealth() + " / " + player.getMaxHealth(), barX + 50, barY + 14);

        // --- SCHERMATA / SCRITTA DI VITTORIA SE IL LIVELLO È COMPLETATO ---
        if (level.isCompleted()) {
            // Sfondo semi-trasparente scuro al centro
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, panelWidth, panelHeight);

            // Scritta principale di vittoria
            g2.setColor(new Color(0, 255, 120));
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            String msg = "LIVELLO COMPLETATO!";
            int msgWidth = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (panelWidth - msgWidth) / 2, panelHeight / 2 - 20);

            // Sottotitolo con istruzioni per tornare al menu
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            String subMsg = "Premi INVIO per tornare alla selezione livelli";
            int subWidth = g2.getFontMetrics().stringWidth(subMsg);
            g2.drawString(subMsg, (panelWidth - subWidth) / 2, panelHeight / 2 + 25);
        }
    }
}