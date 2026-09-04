package model;

public interface GameStruct {
		
	public Player getPlayer();
	public void loadWorlds(String path);
	public void changeWorld(int worldID);
	public World getCurrentWorld();
	public LevelBuilder getBuilder();
	public EntityPlacer getEntityPlacer();
	
}
