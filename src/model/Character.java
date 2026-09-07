package model;

import java.awt.Rectangle;

public abstract class Character implements Entity {

    private Vector2D position;
    private Vector2D velocity;
    private Rectangle boundingBox;
    private boolean grounded;

    public Character() {
        this.position = new Vector2D(0, 0);
        this.velocity = new Vector2D(0, 0);
        this.boundingBox = new Rectangle(0, 0, GameStruct.TILE_SIZE, GameStruct.TILE_SIZE);
        this.grounded = false;
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public Rectangle getBoundingBox() {
        return this.boundingBox;
    }

    // --- MODIFICATO: accetta il Player come l'interfaccia Entity ---
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