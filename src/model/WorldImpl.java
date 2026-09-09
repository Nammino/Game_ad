package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WorldImpl implements World {

    private final GameStruct gameStruct;
    private final int id;
    private final String name;
    private final ArrayList<Level> levels;
    private int currentLevelIndex;

    public WorldImpl(GameStruct gameStruct, int id, String name, String worldFolderPath) {
        this.gameStruct = gameStruct;
        this.id = id;
        this.name = name;
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        
        loadLevels(worldFolderPath);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void loadLevels(String worldFolderPath) {
        levels.clear();
        if (worldFolderPath == null || worldFolderPath.trim().isEmpty()) return;

        File folder = new File(worldFolderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] levelFolders = folder.listFiles(File::isDirectory);
            
            if (levelFolders != null) {
                Arrays.sort(levelFolders, (f1, f2) -> f1.getName().compareTo(f2.getName()));

                for (File levelFolder : levelFolders) {
                    levels.add(new LevelImpl(this, levelFolder.getPath()));
                }
            }
        } else {
            System.err.println("Cartella del mondo non trovata: " + worldFolderPath);
        }
    }

    @Override
    public ArrayList<Level> getLevels() {
        return levels;
    }

    @Override
    public Level getCurrentLevel() {
        return levels.isEmpty() ? null : levels.get(currentLevelIndex);
    }

    @Override
    public void changeLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
        }
    }

    @Override
    public GameStruct getGameStruct() {
        return gameStruct;
    }
}