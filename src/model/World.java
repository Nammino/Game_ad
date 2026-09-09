package model;

import java.util.ArrayList;

public interface World {

    int getId();

    String getName();

    void loadLevels(String path);

    Level getCurrentLevel();

    void changeLevel();

    GameStruct getGameStruct();
    
    ArrayList<Level> getLevels();
}