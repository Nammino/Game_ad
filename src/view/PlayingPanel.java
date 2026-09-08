package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import model.Collectible;
import model.Enemy;
import model.Entity;
import model.Goal;
import model.GameStruct;
import model.Level;
import model.NeutralObject;
import model.Player;
import model.Projectile;
import model.ShootingEnemy;
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
            panel.restartCurrentLevel(); // <-- Usa il reset completo anche alla morte
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

        // --- DISEGNO E GESTIONE DELLE ENTITÀ ---
        if (level.getEntities() != null) {
            
            // 1. Oggetti Neutri
            for (Entity entity : level.getEntities()) {
                if (entity instanceof NeutralObject neutral) {
                    int nx = (int) Math.round(neutral.getPosition().getX());
                    int ny = (int) Math.round(neutral.getPosition().getY());

                    g2.setColor(new Color(120, 120, 120, 160));
                    g2.fillRect(nx, ny, tileSize, tileSize);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawRect(nx, ny, tileSize, tileSize);
                }
            }

            // 2. Collezionabili
            level.getEntities().removeIf(entity -> {
                if (entity instanceof Collectible col) {
                    col.update(player); 
                    
                    if (!col.isCollected()) {
                        int cx = (int) Math.round(col.getPosition().getX());
                        int cy = (int) Math.round(col.getPosition().getY());

                        if (col.getItemType().equals("POTION")) {
                            g2.setColor(Color.PINK);
                            g2.fillRect(cx, cy, tileSize, tileSize);
                        } else if (col.getItemType().equals("SWORD")) {
                            g2.setColor(Color.LIGHT_GRAY);
                            g2.fillRect(cx, cy, tileSize, tileSize);
                        } else if (col.getItemType().equals("GUN")) {
                            g2.setColor(Color.BLUE);
                            g2.fillRect(cx, cy, tileSize, tileSize);
                        } else if (col.getItemType().equals("COIN")) {
                            g2.setColor(Color.YELLOW);
                            g2.fillOval(cx + 8, cy + 8, tileSize - 16, tileSize - 16);
                            g2.setColor(new Color(218, 165, 32));
                            g2.drawOval(cx + 8, cy + 8, tileSize - 16, tileSize - 16);
                            return col.isCollected();
                        }

                        g2.setColor(Color.BLACK);
                        g2.drawRect(cx, cy, tileSize, tileSize);
                    }
                    return col.isCollected(); 
                }
                return false;
            });

            // 3. Goal e Nemici
            for (Entity entity : level.getEntities()) {
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
                else if (entity instanceof Enemy enemy) {
                    int ex = (int) Math.round(enemy.getPosition().getX());
                    int ey = (int) Math.round(enemy.getPosition().getY());

                    g2.setColor(new Color(150, 0, 150));
                    g2.fillRect(ex, ey, tileSize, tileSize);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    g2.drawString("E", ex + 10, ey + 24);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(ex, ey, tileSize, tileSize);

                    if (player.getBoundingBox().intersects(enemy.getBoundingBox())) {
                        player.setHealth(player.getHealth() - 1); 
                    }
                }
                else if (entity instanceof ShootingEnemy shootingEnemy) {
                    int sx = (int) Math.round(shootingEnemy.getPosition().getX());
                    int sy = (int) Math.round(shootingEnemy.getPosition().getY());

                    g2.setColor(new Color(200, 80, 0));
                    g2.fillRect(sx, sy, tileSize, tileSize);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    g2.drawString("S", sx + 10, sy + 24);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(sx, sy, tileSize, tileSize);

                    g2.setColor(Color.YELLOW);
                    for (Projectile p : shootingEnemy.getActiveProjectiles()) {
                        int px = (int) Math.round(p.getPosition().getX());
                        int py = (int) Math.round(p.getPosition().getY());
                        g2.fillOval(px, py, 12, 12);
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
        g2.drawString("A/D: Muovi | SPAZIO: Salta | Rotella: Seleziona Slot | Click SX: Usa Oggetto", 20, 25);

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

        // --- MINI INVENTARIO IN BASSO A SINISTRA (5 slot totali: 4 oggetti + 1 vuoto di sicurezza) ---
        List<String> allItems = player.getInventory().getCollectedItems();
        List<String> usableItems = new ArrayList<>();
        
        for (String item : allItems) {
            if (!item.equals("COIN")) {
                usableItems.add(item);
                if (usableItems.size() == 4) break; 
            }
        }

        int slotSize = 40;
        int slotSpacing = 10;
        int miniInvX = 20;
        int miniInvY = panelHeight - 70; 

        java.awt.Stroke originalStroke = g2.getStroke();

        g2.setFont(new Font("Arial", Font.BOLD, 10));
        for (int i = 0; i < 5; i++) { // Disegniamo 5 slot fissi (il 5° è sempre vuoto)
            int currentX = miniInvX + i * (slotSize + slotSpacing);
            
            boolean isSelected = (i == panel.getSelectedSlot());

            if (isSelected) {
                g2.setColor(new Color(70, 70, 100, 230)); 
            } else {
                g2.setColor(new Color(40, 40, 50, 200)); 
            }
            g2.fillRect(currentX, miniInvY, slotSize, slotSize);

            if (isSelected) {
                g2.setColor(new Color(255, 215, 0)); 
                g2.setStroke(new BasicStroke(3.0f)); 
            } else {
                g2.setColor(new Color(120, 120, 150)); 
                g2.setStroke(new BasicStroke(1.0f)); 
            }
            g2.drawRect(currentX, miniInvY, slotSize, slotSize);

            g2.setStroke(originalStroke);

            // Disegna l'oggetto solo se rientra nei primi 4 slot ed esiste nell'inventario
            if (i < 4 && i < usableItems.size()) {
                String itemType = usableItems.get(i);
                
                if (itemType.equals("POTION")) {
                    g2.setColor(Color.PINK);
                } else if (itemType.equals("SWORD")) {
                    g2.setColor(Color.LIGHT_GRAY);
                } else if (itemType.equals("GUN")) {
                    g2.setColor(Color.BLUE);
                }
                
                g2.fillRect(currentX + 8, miniInvY + 8, slotSize - 16, slotSize - 16);
                g2.setColor(Color.BLACK);
                g2.drawRect(currentX + 8, miniInvY + 8, slotSize - 16, slotSize - 16);
            }
            
            // Numero dello slot (da 1 a 5)
            g2.setColor(Color.WHITE);
            g2.drawString("" + (i + 1), currentX + 4, miniInvY + 12);
        }

        // --- SCHERMATA / SCRITTA DI VITTORIA SE IL LIVELLO È COMPLETATO ---
        if (level.isCompleted()) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, panelWidth, panelHeight);

            g2.setColor(new Color(0, 255, 120));
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            String msg = "LIVELLO COMPLETATO!";
            int msgWidth = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (panelWidth - msgWidth) / 2, panelHeight / 2 - 20);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            String subMsg = "Premi INVIO per tornare alla selezione livelli";
            int subWidth = g2.getFontMetrics().stringWidth(subMsg);
            g2.drawString(subMsg, (panelWidth - subWidth) / 2, panelHeight / 2 + 25);
        }
    }
}