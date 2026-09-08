package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
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

    private BufferedImage potionImg;
    private BufferedImage coinImg;
    
    // Dichiarazione delle due immagini per il giocatore (Destra e Sinistra)
    private BufferedImage boyRightImg;
    private BufferedImage boyLeftImg;
    
    // Sprite dell'orco e della roccia
    private BufferedImage orcRightImg;
    private BufferedImage orcLeftImg;
    private BufferedImage rockImg;
    
    // Sprite per la spada, l'arco e le frecce direzionali
    private BufferedImage swordRightImg;
    private BufferedImage swordLeftImg;
    private BufferedImage bowRightImg;
    private BufferedImage bowLeftImg;
    private BufferedImage arrowRightImg;
    private BufferedImage arrowLeftImg;

    public PlayingPanel() {
        try {
            java.io.InputStream isPotion = getClass().getResourceAsStream("/sprite/potion_red.png");
            if (isPotion != null) potionImg = javax.imageio.ImageIO.read(isPotion);

            java.io.InputStream isCoin = getClass().getResourceAsStream("/sprite/coin_bronze.png");
            if (isCoin != null) coinImg = javax.imageio.ImageIO.read(isCoin);

            // Caricamento sprite Player verso DESTRA
            java.io.InputStream isBoyR = getClass().getResourceAsStream("/sprite/boy_right_1.png");
            if (isBoyR != null) boyRightImg = javax.imageio.ImageIO.read(isBoyR);
            else System.err.println("ATTENZIONE: Impossibile trovare boy_right_1.png");

            // Caricamento sprite Player verso SINISTRA
            java.io.InputStream isBoyL = getClass().getResourceAsStream("/sprite/boy_left_1.png");
            if (isBoyL != null) boyLeftImg = javax.imageio.ImageIO.read(isBoyL);
            else System.err.println("ATTENZIONE: Impossibile trovare boy_left_1.png");
            
            // Caricamento sprite Orco
            java.io.InputStream isOrcR = getClass().getResourceAsStream("/sprite/orc_right_1.png");
            if (isOrcR != null) orcRightImg = javax.imageio.ImageIO.read(isOrcR);

            java.io.InputStream isOrcL = getClass().getResourceAsStream("/sprite/orc_left_1.png");
            if (isOrcL != null) orcLeftImg = javax.imageio.ImageIO.read(isOrcL);

            java.io.InputStream isRock = getClass().getResourceAsStream("/sprite/rock_down_1.png");
            if (isRock != null) rockImg = javax.imageio.ImageIO.read(isRock);
            
            // Caricamento sprite Spada verso DESTRA
            java.io.InputStream isSwordR = getClass().getResourceAsStream("/sprite/sword_right.png");
            if (isSwordR != null) swordRightImg = javax.imageio.ImageIO.read(isSwordR);

            // Caricamento sprite Spada verso SINISTRA
            java.io.InputStream isSwordL = getClass().getResourceAsStream("/sprite/sword_left.png");
            if (isSwordL != null) swordLeftImg = javax.imageio.ImageIO.read(isSwordL);

            // Caricamento sprite Arco verso DESTRA
            java.io.InputStream isBowR = getClass().getResourceAsStream("/sprite/bow_right.png");
            if (isBowR != null) bowRightImg = javax.imageio.ImageIO.read(isBowR);
            else System.err.println("ATTENZIONE: Impossibile trovare bow_right.png");

            // Caricamento sprite Arco verso SINISTRA
            java.io.InputStream isBowL = getClass().getResourceAsStream("/sprite/bow_left.png");
            if (isBowL != null) bowLeftImg = javax.imageio.ImageIO.read(isBowL);
            else System.err.println("ATTENZIONE: Impossibile trovare bow_left.png");

            // Caricamento sprite Freccia verso DESTRA
            java.io.InputStream isArrowR = getClass().getResourceAsStream("/sprite/arrow_right.png");
            if (isArrowR != null) arrowRightImg = javax.imageio.ImageIO.read(isArrowR);

            // Caricamento sprite Freccia verso SINISTRA
            java.io.InputStream isArrowL = getClass().getResourceAsStream("/sprite/arrow_left.png");
            if (isArrowL != null) arrowLeftImg = javax.imageio.ImageIO.read(isArrowL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            panel.restartCurrentLevel(); 
            return;
        }

        int tileSize = GameStruct.TILE_SIZE;

        // --- CAMERA ---
        int playerX = (int) Math.round(player.getPosition().getX());
        int playerY = (int) Math.round(player.getPosition().getY()); 
        
        int cameraX = (int) Math.round(playerX - (panelWidth / 2.0) + (tileSize / 2.0));
        int cameraY = (int) Math.round(playerY - (panelHeight / 2.0) + (tileSize / 2.0));

        if (cameraX < 0) cameraX = 0;
        int maxMapWidth = map.get(0).length() * tileSize;
        if (cameraX > maxMapWidth - panelWidth) {
            cameraX = Math.max(0, maxMapWidth - panelWidth);
        }

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

        // --- CONTROLLO COLLISIONE CONTINUA ANIMAZIONE SPADA ---
        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            if (gp.getSwordAnimationFrames() > 0) {
                if (level.getEntities() != null) {
                    level.getEntities().removeIf(entity -> {
                        if (entity instanceof Enemy || entity instanceof ShootingEnemy) {
                            int slashWidth = 30;
                            int slashHeight = 12;
                            int slashX = player.isFacingRight() ? 
                                (int) player.getPosition().getX() + tileSize : 
                                (int) player.getPosition().getX() - slashWidth;
                            int slashY = (int) player.getPosition().getY() + (tileSize / 2) - (slashHeight / 2);
                            
                            java.awt.Rectangle swordRange = new java.awt.Rectangle(slashX, slashY, slashWidth, slashHeight);
                            return swordRange.intersects(entity.getBoundingBox());
                        }
                        return false;
                    });
                }
            }
        }

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

                        BufferedImage itemImg = null;
                        if (col.getItemType().equals("POTION")) {
                            itemImg = potionImg;
                        } else if (col.getItemType().equals("COIN")) {
                            itemImg = coinImg;
                        } else if (col.getItemType().equals("SWORD")) {
                            itemImg = swordRightImg;
                        } else if (col.getItemType().equals("GUN")) {
                            itemImg = bowRightImg; // Mostra l'arco a terra
                        }

                        if (itemImg != null) {
                            g2.drawImage(itemImg, cx, cy, tileSize, tileSize, null);
                        } else {
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
                            }
                        }
                        return col.isCollected();
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
                        if (model.getPlayer() != null && model.getPlayer().getInventory() != null) {
                            model.getPlayer().getInventory().clear();
                        }
                    }
                }
                else if (entity instanceof Enemy enemy) {
                    int ex = (int) Math.round(enemy.getPosition().getX());
                    int ey = (int) Math.round(enemy.getPosition().getY());

                    BufferedImage enemyImg = enemy.getSprite();
                    if (enemyImg != null) {
                        g2.drawImage(enemyImg, ex, ey, tileSize, tileSize, null);
                    } else {
                        g2.setColor(new Color(150, 0, 150));
                        g2.fillRect(ex, ey, tileSize, tileSize);
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Arial", Font.BOLD, 18));
                        g2.drawString("E", ex + 10, ey + 24);
                    }

                    if (player.getBoundingBox().intersects(enemy.getBoundingBox())) {
                        player.setHealth(player.getHealth() - 1); 
                    }
                }
                else if (entity instanceof ShootingEnemy shootingEnemy) {
                    int sx = (int) Math.round(shootingEnemy.getPosition().getX());
                    int sy = (int) Math.round(shootingEnemy.getPosition().getY());

                    BufferedImage currentOrcImg = shootingEnemy.isFacingRight() ? orcRightImg : orcLeftImg;

                    if (currentOrcImg != null) {
                        g2.drawImage(currentOrcImg, sx, sy, tileSize, tileSize, null);
                    } else {
                        g2.setColor(new Color(200, 80, 0));
                        g2.fillRect(sx, sy, tileSize, tileSize);
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Arial", Font.BOLD, 18));
                        g2.drawString("S", sx + 10, sy + 24);
                        g2.setColor(Color.BLACK);
                        g2.drawRect(sx, sy, tileSize, tileSize);
                    }

                    for (Projectile p : shootingEnemy.getActiveProjectiles()) {
                        int px = (int) Math.round(p.getPosition().getX());
                        int py = (int) Math.round(p.getPosition().getY());
                        
                        if (rockImg != null) {
                            g2.drawImage(rockImg, px, py, 12, 12, null);
                        } else {
                            g2.setColor(Color.YELLOW);
                            g2.fillOval(px, py, 12, 12);
                        }
                    }
                }
            }
        }

        // --- Disegno Giocatore con Sprite Direzionale Corretto ---
        int px = (int) Math.round(player.getPosition().getX());
        int py = (int) Math.round(player.getPosition().getY());
        
        BufferedImage currentPlayerImg = player.isFacingRight() ? boyRightImg : boyLeftImg;

        if (currentPlayerImg != null) {
            g2.drawImage(currentPlayerImg, px, py, tileSize, tileSize, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(px, py, tileSize, tileSize);
        }

        // --- DISEGNO ANIMAZIONE / SPRITE SPADA DIREZIONALE ---
        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            if (gp.getSwordAnimationFrames() > 0) {
                int swordW = tileSize;
                int swordH = tileSize;
                
                BufferedImage currentSwordImg = player.isFacingRight() ? swordRightImg : swordLeftImg;
                
                int swordX = player.isFacingRight() ? 
                    (int) player.getPosition().getX() + tileSize : 
                    (int) player.getPosition().getX() - swordW;
                    
                int swordY = (int) player.getPosition().getY();
                
                if (currentSwordImg != null) {
                    g2.drawImage(currentSwordImg, swordX, swordY, swordW, swordH, null);
                } else {
                    g2.setColor(new Color(255, 255, 255, 220));
                    g2.fillRect(swordX, swordY + 12, 30, 12); 
                }
            }
        }

        // --- DISEGNO ANIMAZIONE / SPRITE ARCO DIREZIONALE ---
        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            if (gp.getBowAnimationFrames() > 0) {
                int bowW = tileSize;
                int bowH = tileSize;
                
                BufferedImage currentBowImg = player.isFacingRight() ? bowRightImg : bowLeftImg;
                
                int bowX = player.isFacingRight() ? 
                    (int) player.getPosition().getX() + tileSize : 
                    (int) player.getPosition().getX() - bowW;
                    
                int bowY = (int) player.getPosition().getY();
                
                if (currentBowImg != null) {
                    g2.drawImage(currentBowImg, bowX, bowY, bowW, bowH, null);
                }
            }
        }

        // --- GESTIONE E DISEGNO FRECCE DEL GIOCATORE IN VOLO ---
        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            
            if (gp.getPlayerProjectiles() != null) {
                List<Projectile> toRemove = new ArrayList<>();
                for (Projectile p : gp.getPlayerProjectiles()) {
                    p.getPosition().setX(p.getPosition().getX() + p.getSpeedX());
                    
                    int prx = (int) Math.round(p.getPosition().getX());
                    int pry = (int) Math.round(p.getPosition().getY());
                    
                    // Disegna lo sprite della freccia in base alla direzione (evita le palline gialle)
                    BufferedImage currentArrowImg = (p.getSpeedX() > 0) ? arrowRightImg : arrowLeftImg;
                    
                    if (currentArrowImg != null) {
                        g2.drawImage(currentArrowImg, prx, pry, 24, 12, null);
                    } else {
                        // Fallback se le immagini delle frecce non sono caricate
                        g2.setColor(new Color(200, 150, 50));
                        g2.fillRect(prx, pry, 16, 6);
                    }
                    
                    int tileCol = prx / tileSize;
                    int tileRow = pry / tileSize;
                    
                    if (tileRow >= 0 && tileRow < map.size() && tileCol >= 0 && tileCol < map.get(tileRow).length()) {
                        char tileChar = map.get(tileRow).charAt(tileCol);
                        if (tileChar == '#') {
                            toRemove.add(p);
                            continue;
                        }
                    } else {
                        toRemove.add(p);
                        continue;
                    }
                    
                    if (level.getEntities() != null) {
                        level.getEntities().removeIf(entity -> {
                            if (entity instanceof Enemy || entity instanceof ShootingEnemy) {
                                if (new java.awt.Rectangle(prx, pry, 24, 12).intersects(entity.getBoundingBox())) {
                                    toRemove.add(p);
                                    return true; 
                                }
                            }
                            return false;
                        });
                    }
                }
                gp.getPlayerProjectiles().removeAll(toRemove);
            }
        }

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

        // --- MINI INVENTARIO IN BASSO A SINISTRA CON SPRITE ---
        List<model.ItemInstance> allItems = player.getInventory().getItems();
        List<model.ItemInstance> usableItems = new ArrayList<>();
        
        for (model.ItemInstance item : allItems) {
            if (!item.getType().equals("COIN")) {
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
        for (int i = 0; i < 5; i++) { 
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

            if (i < 4 && i < usableItems.size()) {
                model.ItemInstance itemInst = usableItems.get(i);
                String itemType = itemInst.getType();
                
                BufferedImage slotImg = null;
                if (itemType.equals("POTION")) {
                    slotImg = potionImg;
                } else if (itemType.equals("SWORD")) {
                    slotImg = swordRightImg;
                } else if (itemType.equals("GUN")) {
                    slotImg = bowRightImg; // Mostra l'arco nell'inventario
                }

                if (slotImg != null) {
                    g2.drawImage(slotImg, currentX + 4, miniInvY + 4, slotSize - 8, slotSize - 8, null);
                } else {
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
                
                if (!itemType.equals("POTION")) {
                    g2.setColor(Color.YELLOW);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.drawString("x" + itemInst.getUsesLeft(), currentX + 3, miniInvY + slotSize - 4);
                }
            }
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.drawString("" + (i + 1), currentX + 4, miniInvY + 12);
        }

        // --- SCHERMATA DI VITTORIA ---
        if (level.isCompleted()) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, panelWidth, panelHeight);

            g2.setColor(new Color(0, 255, 120));
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            String msg = "LIVELLO COMPLETATO!";
            int msgWidth = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (panelWidth - msgWidth) / 2, panelHeight / 2 - 40);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            
            String subMsg1 = "Premi INVIO per tornare alla selezione livelli";
            int subWidth1 = g2.getFontMetrics().stringWidth(subMsg1);
            g2.drawString(subMsg1, (panelWidth - subWidth1) / 2, panelHeight / 2 + 10);

            String subMsg2 = "Premi R per rigiocare il livello";
            int subWidth2 = g2.getFontMetrics().stringWidth(subMsg2);
            g2.drawString(subMsg2, (panelWidth - subWidth2) / 2, panelHeight / 2 + 45);
        }
    }
}