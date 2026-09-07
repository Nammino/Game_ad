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
    private GameState previousState = GameState.MENU; // Traccia lo stato precedente per le impostazioni

    public GameControllerImpl(GameStruct model, GamePanel panel) {
        this.model = model;
        this.panel = panel;
        this.panel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        GameState state = panel.getCurrentState();

        // CONTROLLI DURANTE IL GIOCO
        if (state == GameState.PLAYING) {
            Player player = model.getPlayer();
            if (player != null) {
                switch (code) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> player.moveLeft();
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> player.moveRight();
                    case KeyEvent.VK_SPACE, KeyEvent.VK_W, KeyEvent.VK_UP -> player.jump();
                    case KeyEvent.VK_ESCAPE -> {
                        player.stop(); // Ferma il movimento residuo
                        panel.setCurrentState(GameState.PAUSE); // Apre Menu Pausa
                        panel.repaint();
                    }
                }
            }
            return;
        }

        // GESTIONE MENU DI PAUSA
        if (state == GameState.PAUSE) {
            PausePanel pause = panel.getPausePanel();

            switch (code) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    pause.navigateVertical(-1);
                    panel.repaint();
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    pause.navigateVertical(1);
                    panel.repaint();
                }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    int option = pause.getSelectedIndex();
                    switch (option) {
                        case 0 -> panel.setCurrentState(GameState.PLAYING); // Continua
                        case 1 -> { // Ricomincia partita
                            panel.resetPlayerPosition();
                            panel.setCurrentState(GameState.PLAYING);
                        }
                        case 2 -> { // Apri impostazioni
                            previousState = GameState.PAUSE;
                            panel.setCurrentState(GameState.SETTINGS);
                        }
                        case 3 -> panel.setCurrentState(GameState.LEVEL_SELECTION); // Torna ai livelli
                    }
                    panel.repaint();
                }
                case KeyEvent.VK_ESCAPE -> {
                    panel.setCurrentState(GameState.PLAYING); // ESC un'altra volta chiude la pausa
                    panel.repaint();
                }
            }
            return;
        }

        // GESTIONE SCHERMATA SELEZIONE LIVELLI
        if (state == GameState.LEVEL_SELECTION) {
            switch (code) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                    panel.navigateLevel(-1);
                    panel.repaint();
                }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                    panel.navigateLevel(1);
                    panel.repaint();
                }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    panel.resetPlayerPosition();
                    panel.setCurrentState(GameState.PLAYING);
                    panel.repaint();
                }
                case KeyEvent.VK_ESCAPE -> {
                    panel.setCurrentState(GameState.WORLD_SELECTION);
                    panel.repaint();
                }
            }
            return;
        }

        // GESTIONE SCHERMATA SELEZIONE MONDO
        if (state == GameState.WORLD_SELECTION) {
            WorldSelectionPanel selectionPanel = panel.getWorldSelectionPanel();

            switch (code) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    selectionPanel.navigateVertical(-1);
                    panel.repaint();
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    selectionPanel.navigateVertical(1);
                    panel.repaint();
                }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    if (selectionPanel.getSelectedWorldIndex() == 3) {
                        panel.setCurrentState(GameState.MENU);
                    } else {
                        panel.setSelectedLevelIndex(0);
                        panel.setCurrentState(GameState.LEVEL_SELECTION);
                    }
                    panel.repaint();
                }
                case KeyEvent.VK_ESCAPE -> {
                    panel.setCurrentState(GameState.MENU);
                    panel.repaint();
                }
            }
            return;
        }

        // GESTIONE SCHERMATA IMPOSTAZIONI
        if (state == GameState.SETTINGS) {
            SettingsPanel settings = panel.getSettingsPanel();

            switch (code) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    settings.navigateVertical(-1);
                    panel.repaint();
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    settings.navigateVertical(1);
                    panel.repaint();
                }
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                    settings.navigateHorizontal(-1);
                    panel.repaint();
                }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                    settings.navigateHorizontal(1);
                    panel.repaint();
                }
                case KeyEvent.VK_TAB -> {
                    settings.switchFocusArea();
                    panel.repaint();
                }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    if (settings.getSelectedOptionIndex() == settings.getSettingsOptionsCount()) { 
                        settings.applyCurrentTabSettings(panel);
                    } else {
                        settings.navigateHorizontal(1);
                    }
                    panel.repaint();
                }
                case KeyEvent.VK_ESCAPE -> {
                    panel.setCurrentState(previousState); // Ritorna al menu da cui si era arrivati (MENU o PAUSE)
                    panel.repaint();
                }
            }
            return;
        }

        // GESTIONE MENU PRINCIPALE
        if (state == GameState.MENU) {
            switch (code) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    panel.navigateMenu(-1);
                    panel.repaint();
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    panel.navigateMenu(1);
                    panel.repaint();
                }
                case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                    int option = panel.getCurrentOptionIndex();
                    switch (option) {
                        case 0 -> panel.setCurrentState(GameState.WORLD_SELECTION);
                        case 1 -> panel.setCurrentState(GameState.PLAYING);
                        case 2 -> {
                            previousState = GameState.MENU;
                            panel.setCurrentState(GameState.SETTINGS);
                        }
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
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_D || 
                    code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
                    player.stop();
                }
            }
        }
    }
}