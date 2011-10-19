package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;

import java.awt.*;

public abstract class Camera {

    private static final Point3D DEFAULT_POSITION = Point3D.zero();
    private static final Vector3D DEFAULT_DIRECTION = new Vector3D(0, 0, -1);
    private static final Vector3D DEFAULT_UP = new Vector3D(0, 1, 0);
    private static final float DEFAULT_FIELD_OF_VIEW = (float) Math.toRadians(45);

	protected Point3D position;
	protected Vector3D direction;
	protected Vector3D up;
    protected float fieldOfView;
	
	public Camera(Point3D position, Vector3D direction, Vector3D up, float fieldOfView) {
		this.position = position;
		this.direction = Vector3D.normalized(direction);
		this.up = up;
        this.fieldOfView = fieldOfView;
	}
	
	public Camera() {
		this(DEFAULT_POSITION, DEFAULT_DIRECTION, DEFAULT_UP, DEFAULT_FIELD_OF_VIEW);
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

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    public abstract void update();
	
}
