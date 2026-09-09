package model;

import java.util.ArrayList;

public interface GameStruct {
    public static final int ORIGINAL_TILE_SIZE = 16;
    public static final int SCALE = 2;
    public static final int TILE_SIZE = 36;
    
    Player getPlayer();
    void loadWorlds(String path);
    void changeWorld(int worldID);
    World getCurrentWorld();
    ArrayList<World> getWorlds(); 
    LevelBuilder getBuilder();
    EntityPlacer getEntityPlacer();
    void saveProgress();
}