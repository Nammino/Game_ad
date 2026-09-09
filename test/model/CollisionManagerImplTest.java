package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CollisionManagerImplTest {

    private CollisionManager collisionManager;
    private Player player;
    private ArrayList<String> map;

    @BeforeEach
    void setUp() {
        collisionManager = new CollisionManagerImpl();
        player = new Player();
        
        map = new ArrayList<>();
        map.add("#####");
        map.add("#   #");
        map.add("# = #");
        map.add("#####");
    }

    @Test
    void testNoCollisionInOpenSpace() {
        int tileSize = GameStruct.TILE_SIZE;
        player.getPosition().setX(tileSize * 1);
        player.getPosition().setY(tileSize * 1);
        player.getVelocity().setX(0);
        player.getVelocity().setY(0);

        boolean levelCompleted = collisionManager.checkTileCollisions(player, map);
        
        assertFalse(levelCompleted);
        assertFalse(player.isGrounded());
    }
}