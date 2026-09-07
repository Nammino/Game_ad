package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
        
        loadWorlds("worlds.txt");
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void loadWorlds(String path) {
        worlds.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("/");
                if (parts.length >= 3) {
                    int worldId = Integer.parseInt(parts[0].trim());
                    String worldName = parts[1].trim();
                    String worldPath = parts[2].trim();
                    
                    World world = new WorldImpl(this, worldId, worldName, worldPath);
                    worlds.add(world);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore di caricamento dei mondi dal file: " + path);
        } catch (NumberFormatException e) {
            System.err.println("Formato ID non valido nel file mondi: " + path);
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

    public ArrayList<World> getWorlds() {
        return worlds;
    }
}