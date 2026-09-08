package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import model.GameStruct;
import model.ItemInstance;
import model.Level;
import model.Player;
import model.Projectile;
import model.ShootingEnemy;
import model.CollisionManager;
import model.CollisionManagerImpl;
import model.Enemy;
import model.Entity;

public class GamePanel extends JPanel {

    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;

    private GameStruct model;
    private GameState currentState = GameState.MENU;

    private final SettingsPanel settingsPanel;
    private final WorldSelectionPanel worldSelectionPanel;
    private final LevelSelectionPanel levelSelectionPanel;
    private final PlayingPanel playingPanel;
    private final PausePanel pausePanel;
    private final InventoryPanel inventoryPanel;
    
    private final CollisionManager collisionManager;
    private List<Projectile> playerProjectiles = new ArrayList<>();
    
    // Variabile per gestire la durata dell'animazione della spada (in fotogrammi)
    private int swordAnimationFrames = 0;
    
    private int selectedLevelIndex = 0;
    private int currentOptionIndex = 0;
    private int selectedSlot = 0;
    private int bowAnimationFrames = 0;
    
    private final String[] menuOptions = {"Nuova Partita", "Carica Partita", "Impostazioni", "Esci"};

    private Timer gameLoop;

    public GamePanel() {
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);

        this.settingsPanel = new SettingsPanel();
        this.worldSelectionPanel = new WorldSelectionPanel(); 
        this.levelSelectionPanel = new LevelSelectionPanel();
        this.playingPanel = new PlayingPanel();
        this.pausePanel = new PausePanel();
        this.inventoryPanel = new InventoryPanel();
        this.collisionManager = new CollisionManagerImpl();

        // GAME LOOP: blocca tutto se il livello è completato o se non siamo in PLAYING
        this.gameLoop = new Timer(16, e -> {
            if (currentState == GameState.PLAYING && model != null) {
                if (model.getCurrentWorld() != null && !model.getCurrentWorld().getLevels().isEmpty()) {
                    Level currentLevel = model.getCurrentWorld().getLevels().get(selectedLevelIndex);
                    
                    if (!currentLevel.isCompleted()) {
                        // 1. Aggiorna i calcoli della velocità del player dai comandi
                        model.getPlayer().update(currentLevel.getMap());
                        
                        // 2. Esegui le collisioni con i tile e aggiorna la posizione in modo sicuro per il player!
                        collisionManager.checkTileCollisions(model.getPlayer(), currentLevel.getMap());

                        // Decrementa il contatore dell'animazione della spada ad ogni fotogramma
                        if (swordAnimationFrames > 0) {
                            swordAnimationFrames--;
                        }
                        
                        // Aggiorna proiettili del giocatore
                        playerProjectiles.removeIf(p -> {
                            return false; 
                        });

                        // Aggiorna entità, nemici (Enemy e ShootingEnemy) applicando gravità e collisioni con i tile
                        if (currentLevel.getEntities() != null) {
                            for (Entity entity : currentLevel.getEntities()) {
                                if (entity instanceof ShootingEnemy shootingEnemy) {
                                    shootingEnemy.update(model.getPlayer(), currentLevel.getMap());
                                    collisionManager.checkTileCollisions(shootingEnemy, currentLevel.getMap());
                                } else if (entity instanceof Enemy enemy) {
                                    enemy.update(model.getPlayer()); // <-- Rimosso currentLevel.getMap() qui dentro
                                    collisionManager.checkTileCollisions(enemy, currentLevel.getMap());
                                } else {
                                    entity.update(model.getPlayer());
                                }
                            }
                        }
                    }
                }
                repaint();
            }
        });
        
        this.gameLoop.start();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState == GameState.SETTINGS) {
                    if (settingsPanel.handleMouseClick(e.getPoint(), GamePanel.this)) {
                        repaint();
                    }
                } else if (currentState == GameState.WORLD_SELECTION) {
                    if (worldSelectionPanel.handleMouseClick(e.getPoint(), GamePanel.this, model)) {
                        repaint();
                    }
                } else if (currentState == GameState.PLAYING && e.getButton() == MouseEvent.BUTTON1) {
                    if (model != null && model.getPlayer() != null) {
                        Player player = model.getPlayer();
                        Level currentLevel = model.getCurrentWorld().getLevels().get(selectedLevelIndex);
                        List<ItemInstance> allItems = player.getInventory().getItems();
                        List<ItemInstance> usableItems = new ArrayList<>();
                        
                        for (ItemInstance item : allItems) {
                            if (!item.getType().equals("COIN")) {
                                usableItems.add(item);
                                if (usableItems.size() == 4) break;
                            }
                        }

                        if (selectedSlot < 4 && selectedSlot < usableItems.size()) {
                            ItemInstance activeItem = usableItems.get(selectedSlot);
                            
                            if (activeItem.getType().equals("POTION")) {
                                if (player.getHealth() < player.getMaxHealth()) {
                                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 25));
                                    allItems.remove(activeItem);
                                    repaint();
                                }
                            } else if (activeItem.getType().equals("SWORD")) {
                                activeItem.use(); // Scala un utilizzo
                                
                                // Attiva l'animazione della spada per 10 fotogrammi
                                swordAnimationFrames = 10; 
                                
                                // EFFETTO SPADA DIREZIONALE: Controlla i nemici sul lato corretto
                                if (currentLevel.getEntities() != null) {
                                    currentLevel.getEntities().removeIf(entity -> {
                                        if (entity instanceof Enemy || entity instanceof ShootingEnemy) {
                                            int slashWidth = 30;  
                                            int slashHeight = 12; 
                                            
                                            // Se guarda a destra spawna a destra, se guarda a sinistra spawna a sinistra
                                            int slashX = player.isFacingRight() ? 
                                                (int) player.getPosition().getX() + GameStruct.TILE_SIZE : 
                                                (int) player.getPosition().getX() - slashWidth;
                                                
                                            int slashY = (int) player.getPosition().getY() + (GameStruct.TILE_SIZE / 2) - (slashHeight / 2);
                                            
                                            java.awt.Rectangle swordRange = new java.awt.Rectangle(slashX, slashY, slashWidth, slashHeight);
                                            return swordRange.intersects(entity.getBoundingBox());
                                        }
                                        return false;
                                    });
                                }
                                
                                if (activeItem.isBroken()) {
                                    allItems.remove(activeItem);
                                }
                                repaint();
                                
                            } else if (activeItem.getType().equals("GUN")) {
                                activeItem.use(); 
                                bowAnimationFrames = 10; // <-- Attiva l'animazione dell'arco per 10 frame
                                
                                double pX = player.getPosition().getX();
                                double pY = player.getPosition().getY();
                                
                                double targetX = player.isFacingRight() ? pX + 200 : pX - 200;
                                double startBulletX = player.isFacingRight() ? pX + GameStruct.TILE_SIZE : pX - 12;
                                double bulletY = pY + (GameStruct.TILE_SIZE / 4.0);
                                
                                Projectile bullet = new Projectile(
                                        startBulletX, 
                                        bulletY, 
                                        targetX, 
                                        bulletY, 
                                        8.0 
                                    );  
                                    playerProjectiles.add(bullet);
                                    
                                    if (activeItem.isBroken()) {
                                        allItems.remove(activeItem);
                                    }
                                    repaint();
                            }
                        }
                    }
                }
            }
        });
        
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentState == GameState.SETTINGS) {
                    settingsPanel.handleMouseMove(e.getPoint());
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentState == GameState.SETTINGS) {
                    settingsPanel.handleMouseDrag(e.getPoint());
                    repaint();
                }
            }
        });

        // GESTIONE DELLA ROTELLINA DEL MOUSE (Aggiornata per l'inventario)
        this.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            
            if (currentState == GameState.INVENTORY) {
                // Scorre l'inventario verticalmente in pixel
                inventoryPanel.handleWheel(notches * 20);
                repaint();
            } else {
                // Comportamento standard per la selezione degli slot rapidi
                selectedSlot += notches;
                if (selectedSlot > 4) {
                    selectedSlot = 0;
                } else if (selectedSlot < 0) {
                    selectedSlot = 4;
                }
                repaint();
            }
        });
    }

    public void setModel(GameStruct model) {
        this.model = model;
    }

    public GameStruct getModel() { return model; }

    public GameState getCurrentState() { return currentState; }
    public void setCurrentState(GameState state) { this.currentState = state; }

    public SettingsPanel getSettingsPanel() { return settingsPanel; }
    public WorldSelectionPanel getWorldSelectionPanel() { return worldSelectionPanel; }
    public LevelSelectionPanel getLevelSelectionPanel() { return levelSelectionPanel; }
    public PlayingPanel getPlayingPanel() { return playingPanel; }
    public PausePanel getPausePanel() { return pausePanel; }

    public int getSelectedLevelIndex() { return selectedLevelIndex; }
    public void setSelectedLevelIndex(int index) { this.selectedLevelIndex = index; }

    public void navigateLevel(int direction) {
        if (model != null && model.getCurrentWorld() != null && !model.getCurrentWorld().getLevels().isEmpty()) {
            int maxLevels = model.getCurrentWorld().getLevels().size();
            selectedLevelIndex = (selectedLevelIndex + direction + maxLevels) % maxLevels;
        }
    }

    public int getCurrentOptionIndex() { return currentOptionIndex; }

    public void navigateMenu(int direction) {
        currentOptionIndex = (currentOptionIndex + direction + menuOptions.length) % menuOptions.length;
    }

    public void resetPlayerPosition() {
        if (model != null && model.getPlayer() != null) {
            model.getPlayer().getPosition().setX(50);
            model.getPlayer().getPosition().setY(100);
            model.getPlayer().getVelocity().setX(0);
            model.getPlayer().getVelocity().setY(0);
            model.getPlayer().resetHealth();
        }
    }
    
    public void restartCurrentLevel() {
        if (model != null && model.getCurrentWorld() != null && !model.getCurrentWorld().getLevels().isEmpty()) {
            Level currentLevel = model.getCurrentWorld().getLevels().get(selectedLevelIndex);
            currentLevel.resetLevel(); 

            if (model.getPlayer() != null && model.getPlayer().getInventory() != null) {
                // Svuota completamente l'inventario (oggetti e istanze)
                model.getPlayer().getInventory().clear();
            }
            resetPlayerPosition();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch (currentState) {
            case MENU -> drawMenu(g2);
            case SETTINGS -> settingsPanel.draw(g2, this);
            case WORLD_SELECTION -> worldSelectionPanel.draw(g2, this, model);
            case LEVEL_SELECTION -> levelSelectionPanel.draw(g2, this, model);
            case PLAYING -> playingPanel.draw(g2, this, model);
            case PAUSE -> {
                playingPanel.draw(g2, this, model);
                pausePanel.draw(g2, this);
            }
            case INVENTORY -> inventoryPanel.draw(g2, this, model);
        }
    }

    private void drawMenu(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 38));
        g2.setColor(Color.YELLOW);
        String title = "HELLO KITTY GAME";
        FontMetrics titleMetrics = g2.getFontMetrics();
        g2.drawString(title, (getWidth() - titleMetrics.stringWidth(title)) / 2, 120);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics optionMetrics = g2.getFontMetrics();

        for (int i = 0; i < menuOptions.length; i++) {
            String text = menuOptions[i];
            int x = (getWidth() - optionMetrics.stringWidth(text)) / 2;
            int y = 250 + (i * 50);

            if (i == currentOptionIndex) {
                g2.setColor(Color.RED);
                g2.drawString("> " + text + " <", x - 25, y);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);
            }
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.GRAY);
        String hint = "Usa le FRECCE per spostarsi e PREMI ENTER per selezionare";
        g2.drawString(hint, (getWidth() - g2.getFontMetrics().stringWidth(hint)) / 2, 530);
    }
    
    public int getSelectedSlot() { 
        return selectedSlot; 
    }
    
    public List<Projectile> getPlayerProjectiles() {
        return playerProjectiles;
    }
    
    public int getSwordAnimationFrames() {
        return swordAnimationFrames;
    }
    
    public int getBowAnimationFrames() {
        return bowAnimationFrames;
    }
}