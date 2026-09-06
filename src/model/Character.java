package model;

import java.awt.Rectangle;

public abstract class Character implements Entity{

	private Vector2D position;
	private Vector2D velocity;
	private Rectangle BoundingBox;
	private boolean grounded;
	
	@Override
	public Vector2D getPosition() {
		return this.position;
	}

	@Override
	public Rectangle getBoundingBox() {
		return this.BoundingBox;
	}

	@Override
	public abstract void update();

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
