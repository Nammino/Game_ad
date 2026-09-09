package model;

import java.awt.Rectangle;

public class Goal implements Entity {

    private Vector2D position;
    private Rectangle boundingBox;
    private final int tileSize;

    public Goal(double x, double y) {
        this.tileSize = GameStruct.TILE_SIZE;
        this.position = new Vector2D(x, y);
        this.boundingBox = new Rectangle((int) x, (int) y, tileSize, tileSize);
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void update(Player player) {
    }

    @Override
    public Vector2D getVelocity() {
        return new Vector2D(0, 0);
    }

    @Override
    public boolean isGrounded() {
        return true;
    }

    @Override
    public void setGrounded(boolean grounded) {}
}