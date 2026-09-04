package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GameStructImpl implements GameStruct{

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
		loadWorlds("");
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
			WorldImpl currentWorlds = null;
			int lastWorldId = -1;
			
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				
				String[] p = line.split("#");
				int worldId = Integer.parseInt(p[0]);
				String worldPath = p[1];
				
				if(worldId != lastWorldId) {
					currentWorlds = new WorldImpl(this, worldPath);
					worlds.add(currentWorlds);
					lastWorldId = worldId;
				}
				
			}
		} catch(IOException e) {
			System.err.println("Errore di caricamento dei mondi dal file: " + path);
			e.printStackTrace();
		} catch(NumberFormatException e) {
			System.err.println("Formato dell'ID del mondo non valido: " + path);
		}
	}

	@Override
	public void changeWorld(int worldID) {
		
	}

	@Override
	public World getCurrentWorld() {
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
	
	
	
}
