package model;

import java.awt.Rectangle;

public class Collectible extends Object {
    private boolean collected = false;
    private String itemType; 

    public Collectible(double x, double y, String itemType) {
        super();
        getPosition().setX(x);
        getPosition().setY(y);
        
        // Impostiamo l'hitbox iniziale con una leggera riduzione orizzontale
        int shrinkX = 10;
        int w = GameStruct.TILE_SIZE - shrinkX;
        int h = GameStruct.TILE_SIZE;
        getBoundingBox().setBounds((int) x + (shrinkX / 2), (int) y, w, h);
        
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
        // Garantiamo che l'hitbox rimanga ristretta anche durante i controlli dinamici
        int shrinkX = 10;
        return new Rectangle(
            (int) getPosition().getX() + (shrinkX / 2), 
            (int) getPosition().getY(), 
            GameStruct.TILE_SIZE - shrinkX, 
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