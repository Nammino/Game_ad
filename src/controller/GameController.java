package controller;

import java.awt.event.KeyListener;
import view.GamePanel;

public interface GameController extends KeyListener {

    void initController();

    void startGame();

    void stopGame();
}