package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GameStructImpl implements GameStruct {

    private Player player;
    private LevelBuilder levelBuilder;
    private EntityPlacer entityPlacer;
    private ArrayList<World> worlds;
    private int currentWorldIndex;

    public GameStructImpl() {
        this.player = new Player();
        this.levelBuilder = new LevelBuilderImpl();
        this.entityPlacer = new EntityPlacerImpl();
        this.worlds = new ArrayList<>();
        this.currentWorldIndex = 0;
        
        loadWorlds("worlds");
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void loadWorlds(String folderPath) {
        worlds.clear();
        File worldsDir = new File(folderPath);

        if (worldsDir.exists() && worldsDir.isDirectory()) {
            File[] worldFolders = worldsDir.listFiles(File::isDirectory);
            
            if (worldFolders != null) {
                Arrays.sort(worldFolders, (f1, f2) -> f1.getName().compareTo(f2.getName()));

                int worldId = 1;
                for (File folder : worldFolders) {
                    String worldName = folder.getName();
                    
                    World world = new WorldImpl(this, worldId, worldName, folder.getPath());
                    worlds.add(world);
                    worldId++;
                }
            }
        } else {
            System.err.println("Cartella mondi non trovata: " + folderPath);
        }
    }

    @Override
    public void changeWorld(int worldID) {
        if (worldID >= 0 && worldID < worlds.size()) {
            this.currentWorldIndex = worldID;
        }
    }

    @Override
    public World getCurrentWorld() {
        if (worlds.isEmpty()) {
            return null;
        }
        return worlds.get(currentWorldIndex);
    }

    @Override
    public LevelBuilder getBuilder() {
        return levelBuilder;
    }

    @Override
    public EntityPlacer getEntityPlacer() {
        return entityPlacer;
    }

    @Override
    public ArrayList<World> getWorlds() {
        return worlds;
    }
}