package model;

public class NeutralObject extends Object {

    private String type; // E.g., "TREE", "ROCK", "BUSH"

    public NeutralObject(double x, double y, String type) {
        super();
        getPosition().setX(x);
        getPosition().setY(y);
        this.type = type;
        getBoundingBox().setBounds((int) x, (int) y, GameStruct.TILE_SIZE, GameStruct.TILE_SIZE);
    }

    public String getType() {
        return type;
    }

    @Override
    public void update(Player player) {
        // Nessun effetto sul player
    }
}