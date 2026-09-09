package model;

import java.util.ArrayList;

public interface CollisionManager {
    boolean checkTileCollisions(Entity entity, ArrayList<String> map);
}