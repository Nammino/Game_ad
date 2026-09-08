package model;

import java.awt.Rectangle;

public class Collectible extends Object {

    private static final int SHRINK_X = 10;

    private boolean collected = false;
    private final String itemType; 

    public Collectible(double x, double y, String itemType) {
        getPosition().setX(x);
        getPosition().setY(y);
        getBoundingBox().setBounds(calculateBoundingBox());
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
    public Rectangle getBoundingBox() {
        return calculateBoundingBox();
    }

    private Rectangle calculateBoundingBox() {
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        return new Rectangle(
            x + (SHRINK_X / 2), 
            y, 
            GameStruct.TILE_SIZE - SHRINK_X, 
            GameStruct.TILE_SIZE
        );
    }

    @Override
    public void update(Player player) {
        if (!collected && player != null && getBoundingBox().intersects(player.getBoundingBox())) {
            collected = true;
            player.getInventory().addItem(itemType); 
        }
    }
}