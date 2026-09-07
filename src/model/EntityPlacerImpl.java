package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EntityPlacerImpl implements EntityPlacer {

    @Override
    public ArrayList<Entity> place(String path) {
        ArrayList<Entity> entities = new ArrayList<>();

        if (path == null || path.isEmpty()) {
            return entities;
        }

        int tileSize = GameStruct.TILE_SIZE;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char tileChar = line.charAt(col);
                    
                    double x = col * tileSize;
                    double y = row * tileSize;

                    switch (tileChar) {
                        case 'G', 'D' -> entities.add(new Goal(x, y));

                    }
                }
                row++;
            }
        } catch (IOException e) {
            // File degli oggetti opzionale: se non esiste, non blocca il gioco
        }

        return entities;
    }
}