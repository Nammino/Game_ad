package model;

public class Vector2D {

	private double x;
	private double y;
	
	public Vector2D() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2D(double x,double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public void add(Vector2D v) {
		this.x += v.x;
		this.y += v.y;
	}
	
	public void scale(double factor) {
		this.x *= factor;
		this.y *= factor;
	}
	
	public double scalarProduct(Vector2D v) {
		return this.x*v.x + this.y*v.y;
	}
	public double module() {
		return Math.sqrt(scalarProduct(this));
	}
	
	public Vector2D clone() {
		return new Vector2D(this.x, this.y);
	}
}
