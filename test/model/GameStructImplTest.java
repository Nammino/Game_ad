package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStructImplTest {

    @Test
    void testGameStructInitialization() {
        GameStruct gameStruct = new GameStructImpl();

        assertNotNull(gameStruct.getPlayer(), "Il Player non deve essere nullo");
        assertNotNull(gameStruct.getBuilder(), "Il LevelBuilder non deve essere nullo");
        assertNotNull(gameStruct.getEntityPlacer(), "L'EntityPlacer non deve essere nullo");
        assertNotNull(gameStruct.getWorlds(), "La lista dei mondi non deve essere nulla");
    }

    @Test
    void testChangeWorldOutOfBounds() {
        GameStruct gameStruct = new GameStructImpl();
        World initialWorld = gameStruct.getCurrentWorld();
        
        gameStruct.changeWorld(-99);
        assertEquals(initialWorld, gameStruct.getCurrentWorld(), "Il mondo corrente non dovrebbe cambiare se l'indice non è valido");

        gameStruct.changeWorld(9999);
        assertEquals(initialWorld, gameStruct.getCurrentWorld(), "Il mondo corrente non dovrebbe cambiare fuori dai limiti");
    }
}