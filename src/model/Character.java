package model;

import java.awt.Rectangle;

public abstract class Character implements Entity {

    protected Vector2D position;
    protected Vector2D velocity;
    protected Rectangle boundingBox;
    protected boolean grounded;

    public Character() {
        this.position = new Vector2D(0, 0);
        this.velocity = new Vector2D(0, 0);
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

    @Override
    public abstract void update(Player player);

    public Vector2D getVelocity() {
        return this.velocity;
    }

    public boolean isGrounded() {
        return this.grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }
}

