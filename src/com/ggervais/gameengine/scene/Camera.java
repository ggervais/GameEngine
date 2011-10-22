package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.input.InputSensitive;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.render.Viewport;

import java.awt.*;
import java.util.*;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class Camera implements InputSensitive {


    private static final Logger log = Logger.getLogger(Camera.class);
    private static final Point3D DEFAULT_POSITION = Point3D.zero();
    private static final Vector3D DEFAULT_DIRECTION = new Vector3D(0, 0, -1);
    private static final Vector3D DEFAULT_UP = new Vector3D(0, 1, 0);
    private static final float DEFAULT_FIELD_OF_VIEW = (float) Math.toRadians(45);
    private static final float DEFAULT_NEAR = 0.001f;
    private static final float DEFAULT_FAR = 1000f;
    private static final Vector3D DEFAULT_RIGHT = new Vector3D(1, 0, 0);

	protected Point3D position;
	protected Vector3D direction;
	protected Vector3D up;
    protected float fieldOfView;
    protected float near;
    protected float far;
	
	public Camera(Point3D position, Vector3D direction, Vector3D up, float fieldOfView, float near, float far) {
		this.position = position;
		this.direction = Vector3D.normalized(direction);
		this.up = up;
        this.fieldOfView = fieldOfView;
        this.near = near;
        this.far = far;
	}
	
	public Camera() {
		this(DEFAULT_POSITION, DEFAULT_DIRECTION, DEFAULT_UP, DEFAULT_FIELD_OF_VIEW, DEFAULT_NEAR, DEFAULT_FAR);
	}

    public List<Plane> getPlanes(Viewport viewport) {

        List<Plane> planes = new ArrayList<Plane>();

        Point3D nearCenter = Point3D.add(this.position, this.direction.multiplied(this.near));
        Point3D farCenter = Point3D.add(this.position, this.direction.multiplied(this.far));

        Vector3D realUp = this.up.copy();
        Vector3D right;
        if (realUp.x() == direction.x() && realUp.y() == direction.y() && realUp.z() == direction.z()) {
            right = DEFAULT_RIGHT;
        } else {
            right = Vector3D.crossProduct(this.direction, realUp).normalized();
        }
        realUp = Vector3D.crossProduct(right, this.direction);

        float nearPlaneHeight = 2f * (float) Math.tan(fieldOfView / 2f) * this.near;
        float nearPlaneWidth = nearPlaneHeight * viewport.getAspectRatio();

        float farPlaneHeight = 2f * (float) Math.tan(fieldOfView / 2f) * this.far;
        float farPlaneWidth = farPlaneHeight * viewport.getAspectRatio();

        Point3D nearTopLeft = Point3D.sub(Point3D.add(nearCenter, Vector3D.multiply(realUp, nearPlaneHeight / 2f)), Vector3D.multiply(right, nearPlaneWidth / 2f));
        Point3D nearTopRight = Point3D.add(Point3D.add(nearCenter, Vector3D.multiply(realUp, nearPlaneHeight / 2f)), Vector3D.multiply(right, nearPlaneWidth / 2f));
        Point3D nearBottomLeft = Point3D.sub(Point3D.sub(nearCenter, Vector3D.multiply(realUp, nearPlaneHeight / 2f)), Vector3D.multiply(right, nearPlaneWidth / 2f));
        Point3D nearBottomRight = Point3D.add(Point3D.sub(nearCenter, Vector3D.multiply(realUp, nearPlaneHeight / 2f)), Vector3D.multiply(right, nearPlaneWidth / 2f));

        Point3D farTopLeft = Point3D.sub(Point3D.add(farCenter, Vector3D.multiply(realUp, farPlaneHeight / 2f)), Vector3D.multiply(right, farPlaneWidth / 2f));
        Point3D farTopRight = Point3D.add(Point3D.add(farCenter, Vector3D.multiply(realUp, farPlaneHeight / 2f)), Vector3D.multiply(right, farPlaneWidth / 2f));
        Point3D farBottomLeft = Point3D.sub(Point3D.sub(farCenter, Vector3D.multiply(realUp, farPlaneHeight / 2f)), Vector3D.multiply(right, farPlaneWidth / 2f));
        Point3D farBottomRight = Point3D.add(Point3D.sub(farCenter, Vector3D.multiply(realUp, farPlaneHeight / 2f)), Vector3D.multiply(right, farPlaneWidth / 2f));

        Plane topPlane = new Plane(nearTopLeft, Point3D.sub(farTopLeft, nearTopLeft).crossProduct(Point3D.sub(farTopRight, nearTopLeft)).normalized());
        Plane bottomPlane = new Plane(nearBottomLeft, Point3D.sub(farBottomRight, nearBottomLeft).crossProduct(Point3D.sub(farBottomLeft, nearBottomLeft)).normalized());
        Plane leftPlane = new Plane(nearTopLeft, Point3D.sub(farBottomLeft, nearTopLeft).crossProduct(Point3D.sub(farTopLeft, nearTopLeft)).normalized());
        Plane rightPlane = new Plane(nearTopRight, Point3D.sub(farTopRight, nearTopRight).crossProduct(Point3D.sub(farBottomRight, nearTopRight)).normalized());
        Plane nearPlane = new Plane(nearTopLeft, direction.normalized());
        Plane farPlane = new Plane(farTopLeft, direction.normalized().multiplied(-1));

        planes.add(topPlane);
        planes.add(bottomPlane);
        planes.add(leftPlane);
        planes.add(rightPlane);
        planes.add(nearPlane);
        planes.add(farPlane);

        return planes;
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

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public abstract void update(InputController inputController);
	
}
