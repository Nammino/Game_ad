package model;

import java.awt.Rectangle;

public abstract class Object implements Entity {

    private Vector2D position;
    private Rectangle boundingBox;

    public Object() {
        this.position = new Vector2D(0, 0);
        this.boundingBox = new Rectangle(0, 0, GameStruct.TILE_SIZE, GameStruct.TILE_SIZE);
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public Rectangle getBoundingBox() {
        return this.boundingBox;
    }
}