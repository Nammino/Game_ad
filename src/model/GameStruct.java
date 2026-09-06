package model;

public interface GameStruct {
	//costanti
	public static final int ORIGINAL_TILE_SIZE = 16; //16x16 px
	public static final int SCALE = 2;
	public static final int TILE_SIZE = 36; //Tile size effettiva 
	
	
	public Player getPlayer();
	public void loadWorlds(String path);
	public void changeWorld(int worldID);
	public World getCurrentWorld();
	public LevelBuilder getBuilder();
	public EntityPlacer getEntityPlacer();
	
}
