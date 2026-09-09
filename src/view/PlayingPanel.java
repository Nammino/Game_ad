package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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
    
    private BufferedImage boyRightImg;
    private BufferedImage boyLeftImg;
    
    private BufferedImage orcRightImg;
    private BufferedImage orcLeftImg;
    private BufferedImage rockImg;
    
    private BufferedImage swordRightImg;
    private BufferedImage swordLeftImg;
    private BufferedImage bowRightImg;
    private BufferedImage bowLeftImg;
    private BufferedImage arrowRightImg;
    private BufferedImage arrowLeftImg;

    private BufferedImage treeImg;
    private BufferedImage neutralRockImg;
    private BufferedImage bushImg;

    private BufferedImage goalImg;

    public PlayingPanel() {
        try {
            java.io.InputStream isPotion = getClass().getResourceAsStream("/sprite/potion_red.png");
            if (isPotion != null) potionImg = javax.imageio.ImageIO.read(isPotion);

            java.io.InputStream isCoin = getClass().getResourceAsStream("/sprite/coin_bronze.png");
            if (isCoin != null) coinImg = javax.imageio.ImageIO.read(isCoin);

            java.io.InputStream isBoyR = getClass().getResourceAsStream("/sprite/boy_right_1.png");
            if (isBoyR != null) boyRightImg = javax.imageio.ImageIO.read(isBoyR);

            java.io.InputStream isBoyL = getClass().getResourceAsStream("/sprite/boy_left_1.png");
            if (isBoyL != null) boyLeftImg = javax.imageio.ImageIO.read(isBoyL);
            
            java.io.InputStream isOrcR = getClass().getResourceAsStream("/sprite/orc_right_1.png");
            if (isOrcR != null) orcRightImg = javax.imageio.ImageIO.read(isOrcR);

            java.io.InputStream isOrcL = getClass().getResourceAsStream("/sprite/orc_left_1.png");
            if (isOrcL != null) orcLeftImg = javax.imageio.ImageIO.read(isOrcL);

            java.io.InputStream isRock = getClass().getResourceAsStream("/sprite/rock_down_1.png");
            if (isRock != null) rockImg = javax.imageio.ImageIO.read(isRock);
            
            java.io.InputStream isSwordR = getClass().getResourceAsStream("/sprite/sword_right.png");
            if (isSwordR != null) swordRightImg = javax.imageio.ImageIO.read(isSwordR);

            java.io.InputStream isSwordL = getClass().getResourceAsStream("/sprite/sword_left.png");
            if (isSwordL != null) swordLeftImg = javax.imageio.ImageIO.read(isSwordL);

            java.io.InputStream isBowR = getClass().getResourceAsStream("/sprite/bow_right.png");
            if (isBowR != null) bowRightImg = javax.imageio.ImageIO.read(isBowR);

            java.io.InputStream isBowL = getClass().getResourceAsStream("/sprite/bow_left.png");
            if (isBowL != null) bowLeftImg = javax.imageio.ImageIO.read(isBowL);

            java.io.InputStream isArrowR = getClass().getResourceAsStream("/sprite/arrow_right.png");
            if (isArrowR != null) arrowRightImg = javax.imageio.ImageIO.read(isArrowR);

            java.io.InputStream isArrowL = getClass().getResourceAsStream("/sprite/arrow_left.png");
            if (isArrowL != null) arrowLeftImg = javax.imageio.ImageIO.read(isArrowL);

            java.io.InputStream isTree = getClass().getResourceAsStream("/sprite/tree.png");
            if (isTree != null) treeImg = javax.imageio.ImageIO.read(isTree);

            java.io.InputStream isNeutralRock = getClass().getResourceAsStream("/sprite/rock.png");
            if (isNeutralRock != null) neutralRockImg = javax.imageio.ImageIO.read(isNeutralRock);

            java.io.InputStream isBush = getClass().getResourceAsStream("/sprite/bush.png");
            if (isBush != null) bushImg = javax.imageio.ImageIO.read(isBush);

            java.io.InputStream isGoal = getClass().getResourceAsStream("/sprite/door.png");
            if (isGoal != null) goalImg = javax.imageio.ImageIO.read(isGoal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, GamePanel panel, GameStruct model) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        g2.setColor(new Color(107, 140, 255));
        g2.fillRect(0, 0, panelWidth, panelHeight);

        if (model == null) return;
        World world = model.getCurrentWorld();
        if (world == null || world.getLevels().isEmpty()) return;

        Level level = world.getLevels().get(panel.getSelectedLevelIndex());
        ArrayList<String> map = level.getMap();
        Player player = model.getPlayer();

        if (player == null || map == null || map.isEmpty()) return;

        if (player.getHealth() <= 0) {
            panel.restartCurrentLevel(); 
            return;
        }

        int tileSize = GameStruct.TILE_SIZE;

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

        int triggerY = cameraY + panelHeight + 50; 
        if (player.getPosition().getY() > triggerY) {
            panel.restartCurrentLevel();
            return;
        }

        g2.translate(-cameraX, -cameraY);

        List<model.ItemInstance> allItems = player.getInventory().getItems();
        List<model.ItemInstance> usableItems = new ArrayList<>();
        
        for (model.ItemInstance item : allItems) {
            if (!item.getType().equals("COIN")) {
                usableItems.add(item);
                if (usableItems.size() == 4) break; 
            }
        }

        int selectedSlot = panel.getSelectedSlot();
        String activeItemType = "";
        if (selectedSlot >= 0 && selectedSlot < usableItems.size()) {
            activeItemType = usableItems.get(selectedSlot).getType();
        }

        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            if (gp.getSwordAnimationFrames() > 0 && activeItemType.equals("SWORD")) {
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

        if (level.getEntities() != null) {
            
            for (Entity entity : level.getEntities()) {
                if (entity instanceof NeutralObject neutral) {
                    int nx = (int) Math.round(neutral.getPosition().getX());
                    int ny = (int) Math.round(neutral.getPosition().getY());

                    BufferedImage neutralImg = null;
                    String nType = neutral.getType();
                    
                    int drawWidth = tileSize;
                    int drawHeight = tileSize;
                    int drawX = nx;
                    int drawY = ny;

                    if ("TREE".equals(nType)) {
                        neutralImg = treeImg;
                        drawWidth = tileSize * 4;
                        drawHeight = tileSize * 4;
                        drawX = nx - tileSize * 3 / 2; 
                        drawY = ny - tileSize * 3;     
                    } else if ("ROCK".equals(nType)) {
                        neutralImg = neutralRockImg;
                        drawWidth = (int) (tileSize * 0.5);
                        drawHeight = (int) (tileSize * 0.5);
                        drawX = nx + (tileSize - drawWidth) / 2;
                        drawY = ny + (tileSize - drawHeight);
                    } else if ("BUSH".equals(nType)) {
                        neutralImg = bushImg;
                        drawWidth = tileSize * 2;
                        drawHeight = tileSize * 2;
                        drawX = nx - tileSize / 2;
                        drawY = ny - tileSize;
                    }

                    if (neutralImg != null) {
                        g2.drawImage(neutralImg, drawX, drawY, drawWidth, drawHeight, null);
                    } else {
                        g2.setColor(new Color(120, 120, 120, 160));
                        g2.fillRect(nx, ny, tileSize, tileSize);
                        g2.setColor(Color.DARK_GRAY);
                        g2.drawRect(nx, ny, tileSize, tileSize);
                    }
                }
            }

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
                            itemImg = bowRightImg; 
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

            for (Entity entity : level.getEntities()) {
                if (entity instanceof Goal goal) {
                    int gx = (int) Math.round(goal.getPosition().getX());
                    int gy = (int) Math.round(goal.getPosition().getY());

                    int goalWidth = tileSize * 2;
                    int goalHeight = tileSize * 2;
                    int drawX = gx - (tileSize / 2);
                    int drawY = gy - tileSize;

                    if (goalImg != null) {
                        g2.drawImage(goalImg, drawX, drawY, goalWidth, goalHeight, null);
                    } else {
                        g2.setColor(new Color(0, 200, 100));
                        g2.fillRect(drawX, drawY, goalWidth, goalHeight);
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Arial", Font.BOLD, 24));
                        g2.drawString("D", drawX + goalWidth / 3, drawY + goalHeight / 2);
                        g2.setColor(Color.BLACK);
                        g2.drawRect(drawX, drawY, goalWidth, goalHeight);
                    }

                    int shrinkW = goalWidth / 2; 
                    int shrinkH = goalHeight / 2; 
                    java.awt.Rectangle centerGoalBox = new java.awt.Rectangle(
                        drawX + (goalWidth - shrinkW) / 2, 
                        drawY + (goalHeight - shrinkH) / 2, 
                        shrinkW, 
                        shrinkH
                    );

                    if (player.getBoundingBox().intersects(centerGoalBox)) {
                        if (!level.isCompleted()) {
                            level.setCompleted(true);
                            model.saveProgress();
                        }
                        
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
                        int prx = (int) Math.round(p.getPosition().getX());
                        int pry = (int) Math.round(p.getPosition().getY());
                        
                        if (rockImg != null) {
                            g2.drawImage(rockImg, prx, pry, 12, 12, null);
                        } else {
                            g2.setColor(Color.YELLOW);
                            g2.fillOval(prx, pry, 12, 12);
                        }
                    }
                }
            }
        }

        int px = (int) Math.round(player.getPosition().getX());
        int py = (int) Math.round(player.getPosition().getY());
        
        BufferedImage currentPlayerImg = player.isFacingRight() ? boyRightImg : boyLeftImg;

        if (currentPlayerImg != null) {
            g2.drawImage(currentPlayerImg, px, py, tileSize, tileSize, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(px, py, tileSize, tileSize);
        }

        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            if (gp.getSwordAnimationFrames() > 0 && activeItemType.equals("SWORD")) {
                int swordW = tileSize;
                int swordH = tileSize;
                
                BufferedImage currentSwordImg = player.isFacingRight() ? swordRightImg : swordLeftImg;
                int swordX = player.isFacingRight() ? 
                    (int) player.getPosition().getX() + tileSize : 
                    (int) player.getPosition().getX() - swordW;
                int swordY = (int) player.getPosition().getY();
                
                if (currentSwordImg != null) {
                    g2.drawImage(currentSwordImg, swordX, swordY, swordW, swordH, null);
                }
            }
        }

        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            if (gp.getBowAnimationFrames() > 0 && activeItemType.equals("GUN")) {
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

        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            if (gp.getPlayerProjectiles() != null) {
                List<Projectile> toRemove = new ArrayList<>();
                for (Projectile p : gp.getPlayerProjectiles()) {
                    p.getPosition().setX(p.getPosition().getX() + p.getSpeedX());
                    
                    int prx = (int) Math.round(p.getPosition().getX());
                    int pry = (int) Math.round(p.getPosition().getY());
                    
                    BufferedImage currentArrowImg = (p.getSpeedX() >= 0) ? arrowRightImg : arrowLeftImg;
                    
                    if (currentArrowImg != null) {
                        g2.drawImage(currentArrowImg, prx, pry, 24, 12, null);
                    } else {
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

        g2.translate(cameraX, cameraY);

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
                    slotImg = bowRightImg; 
                }

                if (slotImg != null) {
                    g2.drawImage(slotImg, currentX + 4, miniInvY + 4, slotSize - 8, slotSize - 8, null);
                }
                
                if (!itemType.equals("POTION")) {
                    g2.setColor(Color.YELLOW);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.drawString("x" + itemInst.getUsesLeft(), currentX + 3, miniInvY + slotSize - 4);
                }
            }
            
            // Stampiamo il numero solo per i primi 4 slot
            if (i < 4) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.drawString("" + (i + 1), currentX + 4, miniInvY + 12);
            }
        }

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