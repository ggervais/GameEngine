package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;

public abstract class Camera {
	protected Point3D position;
	protected Vector3D direction;
	protected Vector3D up;
	
	public Camera(Point3D position, Vector3D direction, Vector3D up) {
		this.position = position;
		this.direction = Vector3D.normalized(direction);
		this.up = up;
	}
	
	public Camera() {
		this(Point3D.zero(), new Vector3D(0f, 0f, -1f), new Vector3D(0, 1f, 0));
	}

	public Point3D getLookAt() {
		return Point3D.add(this.position, this.direction);
	}
	
	public Vector3D getUp() {
		return this.up;
	}
	
	public void setPosition(Point3D position) {
		this.position = position;
	}
	public Point3D getPosition() {
		return position;
	}
	public void setDirection(Vector3D direction) {
		this.direction = direction;
	}
	public Vector3D getDirection() {
		return direction;
	}

	public abstract void update();
	
}
