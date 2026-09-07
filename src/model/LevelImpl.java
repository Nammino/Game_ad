package model;

import java.util.ArrayList;

public class LevelImpl implements Level {

    private final World world;
    private final String path;
    private ArrayList<String> map;
    private ArrayList<Entity> entities;

    public LevelImpl(World world, String path) {
        this.world = world;
        this.path = path;
        this.map = new ArrayList<>();
        this.entities = new ArrayList<>();
        build();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void build() {
        LevelBuilder builder = world.getGameStruct().getBuilder();
        EntityPlacer placer = world.getGameStruct().getEntityPlacer();

        if (builder != null) {
            this.map = builder.build(path);
        }

        if (placer != null && path.endsWith(".txt")) {
            String entitiesPath = path.replace(".txt", "_entities.txt");
            this.entities = placer.place(entitiesPath);
        }
    }

    @Override
    public ArrayList<String> getMap() {
        return map;
    }

    @Override
    public ArrayList<Entity> getEntities() {
        return entities;
    }
}