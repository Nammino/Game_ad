package model;

import java.io.File;
import java.util.ArrayList;

public class LevelImpl implements Level {

    private String path; // Ora rappresenta la cartella del livello
    private ArrayList<String> map;
    private ArrayList<Entity> entities;
    private World world;
    private boolean completed = false;

    public LevelImpl(World world, String folderPath) {
        this.world = world;
        this.path = folderPath;
        this.map = new ArrayList<>();
        this.entities = new ArrayList<>();
        build();
    }

    public LevelImpl(ArrayList<String> map, ArrayList<Entity> entities) {
        this.map = map != null ? map : new ArrayList<>();
        this.entities = entities != null ? entities : new ArrayList<>();
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void build() {
        if (path != null) {
            File levelFolder = new File(path);
            if (levelFolder.exists() && levelFolder.isDirectory()) {
                
                File mapFile = null;
                File objectsFile = null;

                // Cerca i file all'interno della cartella del livello
                File[] files = levelFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
                if (files != null) {
                    for (File f : files) {
                        String name = f.getName().toLowerCase();
                        if (name.contains("object")) {
                            objectsFile = f;
                        } else {
                            mapFile = f; // Il file principale della mappa (es. level.txt o map.txt)
                        }
                    }
                }

                // 1. Carica la mappa
                if (mapFile != null && mapFile.exists()) {
                    LevelBuilder builder = new LevelBuilderImpl();
                    this.map = builder.build(mapFile.getPath());
                }

                // 2. Carica gli oggetti/entità
                if (objectsFile != null && objectsFile.exists()) {
                    EntityPlacer placer = new EntityPlacerImpl();
                    this.entities = placer.place(objectsFile.getPath());
                } else {
                    this.entities = new ArrayList<>(); // Nessun oggetto trovato
                }
            }
        }
    }

    @Override
    public ArrayList<String> getMap() {
        return this.map;
    }

    @Override
    public ArrayList<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    @Override
    public void resetLevel() {
        this.completed = false;
        if (this.entities != null) {
            this.entities.clear();
        }
        // Richiama il build per rileggere i file e ricreare le entità/oggetti da zero
        build();
    }
}