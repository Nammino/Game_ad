package controller;

import java.awt.event.KeyListener;

public interface GameController extends KeyListener {
    void startGame();
    void stopGame();
}