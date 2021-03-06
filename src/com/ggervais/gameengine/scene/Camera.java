package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.geometry.CubeGeometry;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.input.InputSensitive;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.render.Viewport;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.ggervais.gameengine.scene.scenegraph.Geometry;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
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

    protected Geometry cameraGeometry;

    private List<Plane> frustumPlanes;
    private List<Point3D> frustumPoints;
    private boolean isFrustumDirty;
	
	public Camera(Point3D position, Vector3D direction, Vector3D up, float fieldOfView, float near, float far) {
		this.position = position;
		this.direction = Vector3D.normalized(direction);
		this.up = up;
        this.fieldOfView = fieldOfView;
        this.near = near;
        this.far = far;
        this.frustumPlanes = new ArrayList<Plane>();
        this.frustumPoints = new ArrayList<Point3D>();
        this.isFrustumDirty = true;
        this.cameraGeometry = new CubeGeometry();
	}
	
	public Camera() {
		this(DEFAULT_POSITION, DEFAULT_DIRECTION, DEFAULT_UP, DEFAULT_FIELD_OF_VIEW, DEFAULT_NEAR, DEFAULT_FAR);
	}

    private void computeFrustumPlanes(Viewport viewport) {

        this.frustumPlanes.clear();

        Point3D nearCenter = Point3D.add(this.position, this.direction.multiplied(this.near));
        Point3D farCenter = Point3D.add(this.position, this.direction.multiplied(this.far));

        Vector3D realUp = this.up.copy();
        Vector3D right;
        if (realUp.equals(direction)) {
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

        frustumPlanes.add(topPlane);
        frustumPlanes.add(bottomPlane);
        frustumPlanes.add(leftPlane);
        frustumPlanes.add(rightPlane);
        frustumPlanes.add(nearPlane);
        frustumPlanes.add(farPlane);

        this.frustumPoints.clear();
        this.frustumPoints.add(nearTopLeft);
        this.frustumPoints.add(nearTopRight);
        this.frustumPoints.add(nearBottomLeft);
        this.frustumPoints.add(nearBottomRight);
        this.frustumPoints.add(farTopLeft);
        this.frustumPoints.add(farTopRight);
        this.frustumPoints.add(farBottomLeft);
        this.frustumPoints.add(farBottomRight);
    }

    public List<Point3D> getFrustumPoints(Viewport viewport) {

        this.frustumPlanes.clear();

        Point3D nearCenter = Point3D.add(this.position, this.direction.multiplied(this.near));
        Point3D farCenter = Point3D.add(this.position, this.direction.multiplied(this.far));

        Vector3D realUp = this.up.copy();
        Vector3D right;
        if (realUp.equals(direction)) {
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

        List<Point3D> points = new ArrayList<Point3D>();

        points.add(nearTopLeft);
        points.add(nearTopRight);
        points.add(nearBottomLeft);
        points.add(nearBottomRight);

        points.add(farTopLeft);
        points.add(farTopRight);
        points.add(farBottomLeft);
        points.add(farBottomRight);

        return points;

    }

    public List<Plane> getPlanes(Viewport viewport) {

        if (this.isFrustumDirty) {
            computeFrustumPlanes(viewport);
            this.isFrustumDirty = false;
        }
        return this.frustumPlanes;
    }

    public List<Point3D> getPoints() {
        return this.frustumPoints;
    }


	public Point3D getLookAt() {
		return Point3D.add(this.position, this.direction);
	}
	
	public Vector3D getUp() {
		return this.up;
	}
	
	public void setPosition(Point3D position) {
		Point3D oldValue = this.position;
        this.position = position;

        if (!oldValue.equals(position)) {
            this.isFrustumDirty = true;
        }
	}

	public Point3D getPosition() {
		return position;
	}

	public void setDirection(Vector3D direction) {
		Vector3D oldValue = this.direction;
        this.direction = direction;

        if (!oldValue.equals(direction)) {
            this.isFrustumDirty = true;
        }
	}

	public Vector3D getDirection() {
		return direction;
	}

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(float fieldOfView) {
        float oldValue = this.fieldOfView;
        this.fieldOfView = fieldOfView;

        if (oldValue != fieldOfView) {
            this.isFrustumDirty = true;
        }
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        float oldValue = this.near;
        this.near = near;

        if (oldValue != near) {
            this.isFrustumDirty = true;
        }
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        float oldValue = this.far;
        this.far = far;

        if (oldValue != far) {
            this.isFrustumDirty = true;
        }
    }

    public Spatial getCameraGeometry() {
        return this.cameraGeometry;
    }

    public boolean isPointInFrustum(Viewport viewport, Point3D point) {

        boolean inside = false;

        if (viewport != null && point != null) {

            this.direction.normalize();
            Point3D pointProjection = Point3D.zero();
            Vector3D cameraPositionToPoint = point.sub(this.position);
            pointProjection.z(cameraPositionToPoint.dotProduct(this.direction));

            if (pointProjection.z() >= this.getNear() && pointProjection.z() <= this.getFar()) {
                // Z is OK, we can continue.

                Vector3D realUp = this.up.copy();
                Vector3D right;
                if (realUp.equals(direction)) {
                    right = DEFAULT_RIGHT;
                } else {
                    right = Vector3D.crossProduct(this.direction, realUp).normalized();
                }
                realUp = Vector3D.crossProduct(right, this.direction);

                pointProjection.x(cameraPositionToPoint.dotProduct(right));
                pointProjection.y(cameraPositionToPoint.dotProduct(realUp));

                float planeHeight = pointProjection.z() * 2f * (float) Math.tan(fieldOfView / 2f);
                float planeHeightHalved = planeHeight / 2f;

                if (pointProjection.y() >= -planeHeightHalved && pointProjection.y() <= planeHeightHalved) {
                    // Y is OK, we can continue.
                    float planeWidth = planeHeight * viewport.getAspectRatio();
                    float planeWidthHalved = planeWidth / 2f;
                    if (pointProjection.x() >= -planeWidthHalved && pointProjection.x() <= planeWidthHalved) {
                        // X is OK, we can continue.
                        // Continuing means the point is in the frustum.
                        inside = true;
                    }
                }

            }
        }

        return inside;
    }

    public abstract void update(long currentTime, InputController inputController, Spatial sceneGraphRoot);
	
}
