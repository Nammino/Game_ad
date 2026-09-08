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
import model.Level;
import model.Player;
import model.CollisionManager;
import model.CollisionManagerImpl;
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
    
    private final CollisionManager collisionManager = new CollisionManagerImpl();

    private int selectedLevelIndex = 0;
    private int currentOptionIndex = 0;
    private int selectedSlot = 0;
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

        // GAME LOOP: blocca tutto se il livello è completato o se non siamo in PLAYING
        this.gameLoop = new Timer(16, e -> {
            if (currentState == GameState.PLAYING && model != null) {
                if (model.getCurrentWorld() != null && !model.getCurrentWorld().getLevels().isEmpty()) {
                    Level currentLevel = model.getCurrentWorld().getLevels().get(selectedLevelIndex);
                    
                    // SE IL LIVELLO NON È COMPLETATO, AGGIORNA. SE È COMPLETATO, CONGELA TUTTO!
                    if (!currentLevel.isCompleted()) {
                        model.getPlayer().update(currentLevel.getMap());
                        
                        if (currentLevel.getEntities() != null) {
                            for (Entity entity : currentLevel.getEntities()) {
                                entity.update(model.getPlayer());
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
                    // UTILIZZO DELL'OGGETTO CON IL CLICK SINISTRO
                    if (model != null && model.getPlayer() != null) {
                        Player player = model.getPlayer();
                        List<String> allItems = player.getInventory().getCollectedItems();
                        List<String> usableItems = new ArrayList<>();
                        for (String item : allItems) {
                            if (!item.equals("COIN")) {
                                usableItems.add(item);
                                if (usableItems.size() == 4) break;
                            }
                        }

                        // Se lo slot selezionato è tra 0 e 3 e contiene un oggetto
                        if (selectedSlot < 4 && selectedSlot < usableItems.size()) {
                            String itemToUse = usableItems.get(selectedSlot);
                            if (itemToUse.equals("POTION")) {
                                if (player.getHealth() < player.getMaxHealth()) {
                                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 25));
                                    allItems.remove("POTION"); // Consuma la pozione
                                    repaint();
                                }
                            } else if (itemToUse.equals("SWORD") || itemToUse.equals("GUN")) {
                                allItems.remove(itemToUse); // Consuma/usa l'oggetto
                                repaint();
                            }
                        }
                        // Lo slot 4 è quello vuoto di sicurezza: non fa nulla!
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

        // --- AGGIUNGI QUESTO BLOCCO QUI PER LA ROTELLA DEL MOUSE ---
        this.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            selectedSlot += notches;
            
            // Ciclo tra i 5 slot (0, 1, 2, 3, 4)
            if (selectedSlot > 4) {
                selectedSlot = 0;
            } else if (selectedSlot < 0) {
                selectedSlot = 4;
            }
            repaint();
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
            
            // 1. Resetta le entità, gli oggetti e lo stato del livello corrente
            currentLevel.resetLevel(); // <-- ORA RICARICA OGGETTI E NEMICI CORRETTAMENTE!
            
            // 2. Svuota l'inventario del giocatore
            if (model.getPlayer() != null && model.getPlayer().getInventory() != null) {
                model.getPlayer().getInventory().getCollectedItems().clear();
            }
            
            // 3. Resetta posizione, velocità e vita del giocatore
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
}