package model;

import java.awt.Rectangle;

public class Collectible extends Object {
    private boolean collected = false;
    private String itemType; // TIPO DI OGGETTO (es. "POTION", "SWORD", "GUN")

    public Collectible(double x, double y, String itemType) {
        super();
        getPosition().setX(x);
        getPosition().setY(y);
        getBoundingBox().setBounds((int) x, (int) y, GameStruct.TILE_SIZE, GameStruct.TILE_SIZE);
        this.itemType = itemType;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public String getItemType() {
        return itemType;
    }

    @Override
    public void update(Player player) {
        if (!collected && player != null && getBoundingBox().intersects(player.getBoundingBox())) {
            collected = true;
            player.getInventory().addItem(itemType); // Aggiunge lo specifico tipo di oggetto all'inventario
        }
    }
}