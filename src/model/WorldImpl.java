package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class WorldImpl implements World {

    private final GameStruct gameStruct;
    private final ArrayList<Level> levels;
    private int currentLevelIndex;

    public WorldImpl(GameStruct gameStruct, String levelsFilePath) {
        this.gameStruct = gameStruct;
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        
        loadLevels(levelsFilePath);
    }

    @Override
    public void loadLevels(String path) {
        levels.clear();
        if (path == null || path.isEmpty()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("//")) {
                    levels.add(new LevelImpl(this, line));
                }
            }
        } catch (IOException e) {
            System.err.println("Errore di caricamento dei livelli dal file: " + path);
            e.printStackTrace();
        }
    }

    @Override
    public Level getCurrentLevel() {
        if (levels.isEmpty()) {
            return null;
        }
        return levels.get(currentLevelIndex);
    }

    @Override
    public void changeLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
        } else {
            System.out.println("Tutti i livelli di questo mondo sono stati completati.");
        }
    }

    @Override
    public GameStruct getGameStruct() {
        return gameStruct;
    }
}