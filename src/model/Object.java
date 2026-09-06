package model;

import java.awt.Rectangle;

public abstract class Object implements Entity{

	private Vector2D position;
	private Rectangle BoundingBox;
	
	@Override
	public Vector2D getPosition() {
		return this.position;
	}

	@Override
	public Rectangle getBoundingBox() {
		return this.BoundingBox;
	}
}
