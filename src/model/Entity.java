package model;

import java.awt.Rectangle;

public interface Entity {
    Vector2D getPosition();
    Rectangle getBoundingBox();
    void update(Player player);
}