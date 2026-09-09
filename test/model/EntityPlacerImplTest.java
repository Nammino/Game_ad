package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EntityPlacerImplTest {

    @TempDir
    File tempDir;

    @Test
    void testPlaceValidEntities() throws IOException {

        File mapFile = new File(tempDir, "entities_map.txt");
        try (FileWriter writer = new FileWriter(mapFile)) {
            writer.write("GE\n");
            writer.write("C \n");
        }

        EntityPlacer entityPlacer = new EntityPlacerImpl();
        ArrayList<Entity> entities = entityPlacer.place(mapFile.getAbsolutePath());

        assertNotNull(entities);
        assertEquals(3, entities.size(), "Dovrebbe trovare esattamente 3 entità");

        assertTrue(entities.get(0) instanceof Goal);
        assertEquals(0.0, entities.get(0).getPosition().getX());
        assertEquals(0.0, entities.get(0).getPosition().getY());

        assertTrue(entities.get(1) instanceof Enemy);
        assertEquals(GameStruct.TILE_SIZE, entities.get(1).getPosition().getX());
        assertEquals(0.0, entities.get(1).getPosition().getY());

        assertTrue(entities.get(2) instanceof Collectible);
        assertEquals(0.0, entities.get(2).getPosition().getX());
        assertEquals(GameStruct.TILE_SIZE, entities.get(2).getPosition().getY());
    }

    @Test
    void testPlaceWithNullOrEmptyPath() {
        EntityPlacer entityPlacer = new EntityPlacerImpl();

        ArrayList<Entity> entitiesNull = entityPlacer.place(null);
        assertNotNull(entitiesNull);
        assertTrue(entitiesNull.isEmpty());

        ArrayList<Entity> entitiesEmpty = entityPlacer.place("");
        assertNotNull(entitiesEmpty);
        assertTrue(entitiesEmpty.isEmpty());
    }

    @Test
    void testPlaceNonExistentFile() {
        EntityPlacer entityPlacer = new EntityPlacerImpl();
        ArrayList<Entity> entities = entityPlacer.place("path/inesistente/file.txt");

        assertNotNull(entities);
        assertTrue(entities.isEmpty());
    }
}