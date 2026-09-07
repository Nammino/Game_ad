package model;

import java.util.ArrayList;

public interface CollisionManager {
    void checkTileCollisions(Player player, ArrayList<String> map);
}