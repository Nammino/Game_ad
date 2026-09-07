package model;

public class NeutralObject extends Object {

    public NeutralObject(double x, double y) {
        super();
        getPosition().setX(x);
        getPosition().setY(y);
        getBoundingBox().setBounds((int) x, (int) y, GameStruct.TILE_SIZE, GameStruct.TILE_SIZE);
    }

    @Override
    public void update(Player player) {
        // Oggetto neutro: nessun effetto sulla collisione o sul player
    }
}