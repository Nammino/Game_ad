package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameStructImpl implements GameStruct {

    private Player player;
    private LevelBuilder levelBuilder;
    private EntityPlacer entityPlacer;
    private ArrayList<World> worlds;
    private int currentWorldIndex;
    
    // File in cui memorizziamo i livelli completati
    private static final String PROGRESS_FILE = "worlds/progress.txt";

    public GameStructImpl() {
        this.player = new Player();
        this.levelBuilder = new LevelBuilderImpl();
        this.entityPlacer = new EntityPlacerImpl();
        this.worlds = new ArrayList<>();
        this.currentWorldIndex = 0;
        
        loadWorlds("worlds");
        loadProgress(); // Carica i progressi salvati all'avvio
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
                    String folderName = folder.getName();
                    
                    // Pulisce il nome rimuovendo prefissi numerici (es. "01_") e sostituendo "_" con spazi
                    String formattedName = folderName.replaceAll("^\\d+_", "").replace("_", " ");
                    
                    // Rende la prima lettera maiuscola usando le stringhe (evita conflitti con model.Character)
                    if (!formattedName.isEmpty()) {
                        formattedName = formattedName.substring(0, 1).toUpperCase() + formattedName.substring(1);
                    }
                    
                    World world = new WorldImpl(this, worldId, formattedName, folder.getPath());
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

    // --- METODI PER LA GESTIONE DEL SALVATAGGIO DEI LIVELLI COMPLETATI ---

    @Override
    public void saveProgress() {
        Set<String> completedPaths = new HashSet<>();
        
        for (World world : worlds) {
            if (world.getLevels() != null) {
                for (Level level : world.getLevels()) {
                    if (level.isCompleted()) {
                        completedPaths.add(level.getPath());
                    }
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRESS_FILE))) {
            for (String path : completedPaths) {
                writer.write(path);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio dei progressi: " + e.getMessage());
        }
    }

    public void loadProgress() {
        File file = new File(PROGRESS_FILE);
        if (!file.exists()) return;

        Set<String> completedPaths = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    completedPaths.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura dei progressi: " + e.getMessage());
        }

        // Applica lo stato completato ai livelli corrispondenti
        for (World world : worlds) {
            if (world.getLevels() != null) {
                for (Level level : world.getLevels()) {
                    if (completedPaths.contains(level.getPath())) {
                        level.setCompleted(true);
                    }
                }
            }
        }
    }
}