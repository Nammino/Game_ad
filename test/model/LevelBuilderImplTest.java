package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LevelBuilderImplTest {

    @TempDir
    File tempDir;

    @Test
    void testBuildValidFile() throws IOException {

    	File mapFile = new File(tempDir, "test_map.txt");
        try (FileWriter writer = new FileWriter(mapFile)) {
            writer.write("#####\n");
            writer.write("#P  #\n");
            writer.write("#####\n");
        }

        LevelBuilder levelBuilder = new LevelBuilderImpl();
        ArrayList<String> lines = levelBuilder.build(mapFile.getAbsolutePath());

        assertNotNull(lines);
        assertEquals(3, lines.size());
        assertEquals("#####", lines.get(0));
        assertEquals("#P  #", lines.get(1));
    }

    @Test
    void testBuildNonExistentFile() {
        LevelBuilder levelBuilder = new LevelBuilderImpl();
        ArrayList<String> lines = levelBuilder.build("path/inesistente/mappa.txt");
        
        assertNotNull(lines);
        assertTrue(lines.isEmpty());
    }
}