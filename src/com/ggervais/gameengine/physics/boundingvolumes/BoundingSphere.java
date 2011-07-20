package com.ggervais.gameengine.physics.boundingvolumes;

import com.ggervais.gameengine.geometry.Model;
import com.ggervais.gameengine.math.Point3D;

public class BoundingSphere {
	private float radius;
	private Point3D center;
	
	public BoundingSphere(Point3D center, float radius) {
		this.center = center;
        this.radius = radius;
    }
	
	public BoundingSphere() {
		this(Point3D.zero(), 0);
	}
	
	public static BoundingSphere buildFromModel(Model model) {
		return null;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	public float getRadius() {
		return radius;
	}
	public void setCenter(Point3D center) {
		this.center = center;
	}
	public Point3D getCenter() {
		return center;
	}
}
