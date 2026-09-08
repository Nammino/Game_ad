package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import model.GameStruct;
import model.Player;
import view.GamePanel;
import view.GameState;
import view.PausePanel;
import view.SettingsPanel;
import view.WorldSelectionPanel;

public class GameControllerImpl extends KeyAdapter implements GameController {

    private final GameStruct model;
    private final GamePanel panel;
    private GameState previousState = GameState.MENU;

    public GameControllerImpl(GameStruct model, GamePanel panel) {
        this.model = model;
        this.panel = panel;
        this.panel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        GameState state = panel.getCurrentState();

        // Se siamo nelle impostazioni e stiamo aspettando un tasto per il rebind
        if (state == GameState.SETTINGS) {
            SettingsPanel settings = panel.getSettingsPanel();
            if (settings.isWaitingForKey()) {
                settings.handleKeyRebind(code);
                panel.repaint();
                return;
            }
        }

        if (state == GameState.PLAYING) {
            model.Level currentLevel = model.getCurrentWorld().getLevels().get(panel.getSelectedLevelIndex());

            if (code == KeyEvent.VK_R) {
                currentLevel.setCompleted(false);
                panel.restartCurrentLevel();
                panel.repaint();
                return;
            }

            if (currentLevel.isCompleted()) {
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                    panel.resetPlayerPosition();
                    panel.setCurrentState(GameState.LEVEL_SELECTION);
                    panel.repaint();
                } else if (code == KeyEvent.VK_R) {
                    currentLevel.setCompleted(false);
                    panel.restartCurrentLevel();
                    panel.repaint();
                }
                return; 
            }

            Player player = model.getPlayer();
            if (player != null) {
                SettingsPanel settings = panel.getSettingsPanel();
                
                // Usiamo ESCLUSIVAMENTE i tasti salvati (applied)
                int jumpKey = settings.getAppliedJumpKey();
                int leftKey = settings.getAppliedLeftKey();
                int rightKey = settings.getAppliedRightKey();

                if (code == leftKey) {
                    player.setLeft(true);
                } else if (code == rightKey) {
                    player.setRight(true);
                } else if (code == jumpKey) {
                    player.setJumpRequested(true);
                } else if (code == KeyEvent.VK_I) { 
                    player.setLeft(false);
                    player.setRight(false);
                    panel.setCurrentState(GameState.INVENTORY);
                    panel.repaint();
                } else if (code == KeyEvent.VK_ESCAPE) {
                    player.setLeft(false);
                    player.setRight(false);
                    panel.setCurrentState(GameState.PAUSE);
                    panel.repaint();
                }
            }
            return;
        }

        // Gestisce la chiusura dell'inventario con 'I' o 'ESC'
        if (state == GameState.INVENTORY) {
            if (code == KeyEvent.VK_I || code == KeyEvent.VK_ESCAPE) {
                panel.setCurrentState(GameState.PLAYING);
                panel.repaint();
            }
            return;
        }

        if (state == GameState.PAUSE) {
            PausePanel pause = panel.getPausePanel();
            switch (code) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> { pause.navigateVertical(-1); panel.repaint(); }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> { pause.navigateVertical(1); panel.repaint(); }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    int option = pause.getSelectedIndex();
                    switch (option) {
                        case 0 -> { 
                            panel.setCurrentState(GameState.PLAYING);
                            panel.requestFocusInWindow();
                        }
                        case 1 -> { 
                            panel.restartCurrentLevel(); 
                            panel.setCurrentState(GameState.PLAYING); 
                            panel.requestFocusInWindow();
                        }
                        case 2 -> { previousState = GameState.PAUSE; panel.setCurrentState(GameState.SETTINGS); }
                        case 3 -> panel.setCurrentState(GameState.LEVEL_SELECTION);
                    }
                    panel.repaint();
                }
                case KeyEvent.VK_ESCAPE -> { panel.setCurrentState(GameState.PLAYING); panel.repaint(); }
            }
            return;
        }

        if (state == GameState.LEVEL_SELECTION) {
            switch (code) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> { panel.navigateLevel(-1); panel.repaint(); }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> { panel.navigateLevel(1); panel.repaint(); }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> { 
                    model.Level currentLevel = model.getCurrentWorld().getLevels().get(panel.getSelectedLevelIndex());
                    currentLevel.setCompleted(false);
                    panel.restartCurrentLevel();
                    
                    panel.resetPlayerPosition(); 
                    panel.setCurrentState(GameState.PLAYING); 
                    panel.requestFocusInWindow();
                    panel.repaint(); 
                }
                case KeyEvent.VK_ESCAPE -> { panel.setCurrentState(GameState.WORLD_SELECTION); panel.repaint(); }
            }
            return;
        }

        if (state == GameState.WORLD_SELECTION) {
            WorldSelectionPanel selectionPanel = panel.getWorldSelectionPanel();
            switch (code) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> { 
                    selectionPanel.navigateVertical(-1, model); 
                    panel.repaint(); 
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> { 
                    selectionPanel.navigateVertical(1, model); 
                    panel.repaint(); 
                }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    int selectedIndex = selectionPanel.getSelectedWorldIndex();
                    int totalWorlds = model.getWorlds().size();

                    if (selectedIndex == totalWorlds) {
                        panel.setCurrentState(GameState.MENU);
                    } else {
                        model.changeWorld(selectedIndex);
                        panel.setSelectedLevelIndex(0);
                        panel.setCurrentState(GameState.LEVEL_SELECTION);
                    }
                    panel.repaint();
                }
                case KeyEvent.VK_ESCAPE -> { panel.setCurrentState(GameState.MENU); panel.repaint(); }
            }
            return;
        }

        if (state == GameState.SETTINGS) {
            SettingsPanel settings = panel.getSettingsPanel();
            switch (code) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> { settings.navigateVertical(-1); panel.repaint(); }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> { settings.navigateVertical(1); panel.repaint(); }
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> { settings.navigateHorizontal(-1); panel.repaint(); }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> { settings.navigateHorizontal(1); panel.repaint(); }
                case KeyEvent.VK_TAB -> { settings.switchFocusArea(); panel.repaint(); }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    if (settings.getSelectedOptionIndex() == settings.getSettingsOptionsCount()) { 
                        settings.applyCurrentTabSettings(panel);
                    } else {
                        settings.navigateHorizontal(1);
                    }
                    panel.repaint();
                }
                case KeyEvent.VK_ESCAPE -> { panel.setCurrentState(previousState); panel.repaint(); }
            }
            return;
        }

        if (state == GameState.MENU) {
            switch (code) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> { panel.navigateMenu(-1); panel.repaint(); }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> { panel.navigateMenu(1); panel.repaint(); }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    int option = panel.getCurrentOptionIndex();
                    switch (option) {
                        case 0 -> panel.setCurrentState(GameState.WORLD_SELECTION);
                        case 1 -> {
                            panel.setCurrentState(GameState.PLAYING);
                            panel.requestFocusInWindow();
                        }
                        case 2 -> { previousState = GameState.MENU; panel.setCurrentState(GameState.SETTINGS); }
                        case 3 -> System.exit(0);
                    }
                    panel.repaint();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (panel.getCurrentState() == GameState.PLAYING) {
            Player player = model.getPlayer();
            if (player != null) {
                SettingsPanel settings = panel.getSettingsPanel();
                
                // Rilascio basato esclusivamente sui tasti applicati
                int leftKey = settings.getAppliedLeftKey();
                int rightKey = settings.getAppliedRightKey();
                int jumpKey = settings.getAppliedJumpKey();

                if (code == leftKey) {
                    player.setLeft(false);
                } else if (code == rightKey) {
                    player.setRight(false);
                } else if (code == jumpKey) {
                    player.setJumpRequested(false);
                }
            }
        }
    }

    @Override
    public void startGame() {}

    @Override
    public void stopGame() {}
}