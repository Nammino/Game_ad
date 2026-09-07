package controller;

import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import model.CollisionManager;
import model.CollisionManagerImpl;
import model.GameStruct;
import model.Level;
import model.Player;
import model.World;
import view.GamePanel;
import view.GameState;

public class GameControllerImpl implements GameController, Runnable {

    private final GameStruct model;
    private final GamePanel view;
    private final CollisionManager collisionManager;

    private Thread gameThread;
    private boolean running = false;
    private final int FPS = 60;

    private boolean leftPressed, rightPressed;

    public GameControllerImpl(GameStruct model, GamePanel view) {
        this.model = model;
        this.view = view;
        this.collisionManager = new CollisionManagerImpl();

        // Collega i riferimenti Model e View
        this.view.setModel(model);

        // Imposta i Key Bindings nativi di Swing per garantire la lettura dei tasti
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        InputMap im = view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = view.getActionMap();

        // Tasti Navigazione / Movimento (Pressione)
        bindKey(im, am, "UP", KeyEvent.VK_UP, () -> handleActionPressed(KeyEvent.VK_UP));
        bindKey(im, am, "W", KeyEvent.VK_W, () -> handleActionPressed(KeyEvent.VK_W));
        bindKey(im, am, "DOWN", KeyEvent.VK_DOWN, () -> handleActionPressed(KeyEvent.VK_DOWN));
        bindKey(im, am, "S", KeyEvent.VK_S, () -> handleActionPressed(KeyEvent.VK_S));
        bindKey(im, am, "LEFT", KeyEvent.VK_LEFT, () -> handleActionPressed(KeyEvent.VK_LEFT));
        bindKey(im, am, "A", KeyEvent.VK_A, () -> handleActionPressed(KeyEvent.VK_A));
        bindKey(im, am, "RIGHT", KeyEvent.VK_RIGHT, () -> handleActionPressed(KeyEvent.VK_RIGHT));
        bindKey(im, am, "D", KeyEvent.VK_D, () -> handleActionPressed(KeyEvent.VK_D));
        bindKey(im, am, "ENTER", KeyEvent.VK_ENTER, () -> handleActionPressed(KeyEvent.VK_ENTER));
        bindKey(im, am, "SPACE", KeyEvent.VK_SPACE, () -> handleActionPressed(KeyEvent.VK_SPACE));
        bindKey(im, am, "ESCAPE", KeyEvent.VK_ESCAPE, () -> handleActionPressed(KeyEvent.VK_ESCAPE));

        // Tasti Rilascio (per il movimento fluido in-game)
        bindKeyRelease(im, am, "LEFT_REL", KeyEvent.VK_LEFT, () -> handleActionReleased(KeyEvent.VK_LEFT));
        bindKeyRelease(im, am, "A_REL", KeyEvent.VK_A, () -> handleActionReleased(KeyEvent.VK_A));
        bindKeyRelease(im, am, "RIGHT_REL", KeyEvent.VK_RIGHT, () -> handleActionReleased(KeyEvent.VK_RIGHT));
        bindKeyRelease(im, am, "D_REL", KeyEvent.VK_D, () -> handleActionReleased(KeyEvent.VK_D));
    }

    private void bindKey(InputMap im, ActionMap am, String name, int keyCode, Runnable action) {
        im.put(KeyStroke.getKeyStroke(keyCode, 0, false), name);
        am.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                action.run();
            }
        });
    }

    private void bindKeyRelease(InputMap im, ActionMap am, String name, int keyCode, Runnable action) {
        im.put(KeyStroke.getKeyStroke(keyCode, 0, true), name);
        am.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                action.run();
            }
        });
    }

    @Override
    public synchronized void startGame() {
        if (!running) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public synchronized void stopGame() {
        running = false;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                view.repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (view.getCurrentState() == GameState.PLAYING) {
            Player player = model.getPlayer();
            World world = model.getCurrentWorld();

            if (player != null && world != null && !world.getLevels().isEmpty()) {
                Level currentLevel = world.getLevels().get(view.getSelectedLevelIndex());

                if (leftPressed) player.moveLeft();
                else if (rightPressed) player.moveRight();
                else player.stop();

                player.update();
                collisionManager.checkTileCollisions(player, currentLevel.getMap());
            }
        }
    }

    // --- DISPATCHER DEGLI INPUT ---
    private void handleActionPressed(int code) {
        GameState state = view.getCurrentState();

        switch (state) {
            case MENU -> handleMenuInput(code);
            case WORLD_SELECTION -> handleWorldInput(code);
            case LEVEL_SELECTION -> handleLevelInput(code);
            case PLAYING -> handlePlayingInputPressed(code);
            default -> {
                if (code == KeyEvent.VK_ESCAPE) view.setCurrentState(GameState.MENU);
            }
        }
    }

    private void handleActionReleased(int code) {
        if (view.getCurrentState() == GameState.PLAYING) {
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = false;
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
        }
    }

    private void handleMenuInput(int code) {
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            view.navigateMenu(-1);
        } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            view.navigateMenu(1);
        } else if (code == KeyEvent.VK_ENTER) {
            switch (view.getCurrentOptionIndex()) {
                case 0 -> view.setCurrentState(GameState.WORLD_SELECTION);
                case 1 -> view.setCurrentState(GameState.CONTINUE);
                case 2 -> view.setCurrentState(GameState.SETTINGS);
                case 3 -> System.exit(0);
            }
        }
    }

    private void handleWorldInput(int code) {
        int totalWorlds = model.getWorlds().size();
        if (totalWorlds == 0) return;

        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            view.setSelectedWorldIndex((view.getSelectedWorldIndex() - 1 + totalWorlds) % totalWorlds);
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            view.setSelectedWorldIndex((view.getSelectedWorldIndex() + 1) % totalWorlds);
        } else if (code == KeyEvent.VK_ENTER) {
            model.changeWorld(view.getSelectedWorldIndex());
            view.setSelectedLevelIndex(0);
            view.setCurrentState(GameState.LEVEL_SELECTION);
        } else if (code == KeyEvent.VK_ESCAPE) {
            view.setCurrentState(GameState.MENU);
        }
    }

    private void handleLevelInput(int code) {
        World world = model.getCurrentWorld();
        if (world == null || world.getLevels().isEmpty()) return;

        int totalLevels = world.getLevels().size();

        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            view.setSelectedLevelIndex((view.getSelectedLevelIndex() - 1 + totalLevels) % totalLevels);
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            view.setSelectedLevelIndex((view.getSelectedLevelIndex() + 1) % totalLevels);
        } else if (code == KeyEvent.VK_ENTER) {
            view.setCurrentState(GameState.PLAYING);
        } else if (code == KeyEvent.VK_ESCAPE) {
            view.setCurrentState(GameState.WORLD_SELECTION);
        }
    }

    private void handlePlayingInputPressed(int code) {
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
            if (model.getPlayer() != null) model.getPlayer().jump();
        }
        if (code == KeyEvent.VK_ESCAPE) {
            leftPressed = false;
            rightPressed = false;
            view.setCurrentState(GameState.MENU);
        }
    }

    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}