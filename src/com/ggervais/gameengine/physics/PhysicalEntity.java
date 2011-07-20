package com.ggervais.gameengine.physics;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;

public abstract class PhysicalEntity {
	protected Point3D position;
	protected Vector3D velocity;
	
	public Point3D getPosition() {
		return this.position;
	}
	public void setPosition(Point3D position) {
		this.position= position;
	}
	
	public Vector3D getVelocity() {
		return this.velocity;
	}
	public void setVelocity(Vector3D velocity) {
		this.velocity = velocity;
	}
	
}
