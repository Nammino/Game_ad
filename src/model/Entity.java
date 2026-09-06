package model;

import java.awt.Rectangle;

public interface Entity {
	
	public Vector2D getPosition();
	
	public Rectangle getBoundingBox();
	
	public void update();
}
