package model;

public interface World {

    void loadLevels(String path);

    Level getCurrentLevel();

    void changeLevel();

    GameStruct getGameStruct();
}