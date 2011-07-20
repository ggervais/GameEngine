package com.ggervais.gameengine.math;

public class Ray {
	private Point3D origin;
	private Vector3D direction;
	
	public Ray(Point3D origin, Vector3D direction) {
		this.origin = origin;
		this.direction = direction.normalized();
	}
	
	public Ray() {
		this(new Point3D(), new Vector3D());
	}
	
	public void setOrigin(Point3D origin) {
		this.origin = origin;
	}
	
	public Point3D getOrigin() {
		return this.origin;
	}
	
	public void setDirection(Vector3D direction) {
		this.direction = direction;
	}
	
	public Vector3D getDirection() {
		return this.direction;
	}

    public String toString() {
        return this.origin + " + " + this.direction + " = " + (Point3D.add(this.origin, this.direction));
    }
}
