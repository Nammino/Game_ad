package model;

import java.awt.Rectangle;

public class Collectible extends Object {
    private boolean collected = false;

    public Collectible(double x, double y) {
        super();
        getPosition().setX(x);
        getPosition().setY(y);
        getBoundingBox().setBounds((int) x, (int) y, GameStruct.TILE_SIZE, GameStruct.TILE_SIZE);
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public void update(Player player) {
        if (!collected && player != null && getBoundingBox().intersects(player.getBoundingBox())) {
            collected = true;
            player.getInventory().addItem("Collezionabile"); // Aggiunge l'oggetto all'inventario
        }
    }
}