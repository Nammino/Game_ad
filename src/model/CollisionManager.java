package model;

import java.util.ArrayList;

public interface CollisionManager {
    boolean checkTileCollisions(Player player, ArrayList<String> map);
}