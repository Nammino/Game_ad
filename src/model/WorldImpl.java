package model;

import java.util.ArrayList;

public class WorldImpl implements World {

    private final GameStruct gameStruct;
    private final int id;
    private final String name;
    private final ArrayList<Level> levels;
    private int currentLevelIndex;

    // Ora levelsData contiene le mappe separate da virgola (es: "map_1_1.txt,map_1_2.txt")
    public WorldImpl(GameStruct gameStruct, int id, String name, String levelsData) {
        this.gameStruct = gameStruct;
        this.id = id;
        this.name = name;
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        
        loadLevels(levelsData);
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
    public void loadLevels(String levelsData) {
        levels.clear();
        if (levelsData == null || levelsData.trim().isEmpty()) return;

        String[] levelFiles = levelsData.split(",");
        for (String file : levelFiles) {
            String path = file.trim();
            if (!path.isEmpty()) {
                levels.add(new LevelImpl(this, path));
            }
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