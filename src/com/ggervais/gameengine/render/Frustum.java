package com.ggervais.gameengine.render;

import com.ggervais.gameengine.math.Plane;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Ray;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.Camera;
import com.ggervais.gameengine.scene.FreeFlyCamera;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Frustum {

    private static final Logger log = Logger.getLogger(Frustum.class);

    private static final Vector3D DEFAULT_RIGHT = new Vector3D(1, 0, 0);
    private static final float DEFAULT_NEAR = 0.001f;
    private static final float DEFAULT_FAR = 1000;

    private float near;
    private float far;

    public Frustum(float near, float far) {
        this.near = near;
        this.far = far;
    }

    public Frustum() {
        this(DEFAULT_NEAR, DEFAULT_FAR);
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

    public void getPlanes(Camera camera, Viewport viewport) {
        List<Plane> planes = new ArrayList<Plane>();

        float fieldOfView = camera.getFieldOfView();
        Point3D cameraPosition = camera.getPosition();
        Vector3D cameraDirection = camera.getDirection().normalized();

        Vector3D up = camera.getUp();

        Vector3D right;

        if (up.x() == cameraDirection.x() && up.y() == cameraDirection.y() && up.z() == cameraDirection.z()) {
            right = DEFAULT_RIGHT;
        } else {
            right = Vector3D.crossProduct(cameraDirection, up).normalized();
        }

        log.info("direction: " + cameraDirection);
        log.info("up: " + up);
        log.info("right: " + right);
        up = Vector3D.crossProduct(right, cameraDirection).normalized();
        log.info("new up: " + up);

        Point3D nearPoint = Point3D.add(cameraPosition, cameraDirection.multiplied(this.near));
        Point3D farPoint = Point3D.add(cameraPosition, cameraDirection.multiplied(this.far));

        Ray viewRay = new Ray(camera.getPosition(), cameraDirection);

        float nearPlaneHeight = 2f * (float) Math.tan(fieldOfView / 2f) * this.near;
        float nearPlaneWidth = nearPlaneHeight * viewport.getAspectRatio();

        float farPlaneHeight = 2f * (float) Math.tan(fieldOfView / 2f) * this.far;
        float farPlaneWidth = farPlaneHeight * viewport.getAspectRatio();

        log.info("Near plane width, height: " + nearPlaneWidth + " " + nearPlaneHeight);
        log.info("Far plane width, height: " + farPlaneWidth + " " + farPlaneHeight);

        Point3D nearCenter = Point3D.add(cameraPosition, cameraDirection.multiplied(this.near));
        Point3D farCenter = Point3D.add(cameraPosition, cameraDirection.multiplied(this.far));

        log.info("Near center: " + nearCenter);
        log.info("Far center: " + farCenter);

        Point3D nearTopLeft = Point3D.sub(Point3D.add(nearCenter, Vector3D.multiply(up, nearPlaneHeight / 2f)), Vector3D.multiply(right, nearPlaneWidth / 2f));

        Point3D farTopLeft = Point3D.sub(Point3D.add(farCenter, Vector3D.multiply(up, farPlaneHeight / 2f)), Vector3D.multiply(right, farPlaneWidth / 2f));
        Point3D farTopRight = Point3D.add(Point3D.add(farCenter, Vector3D.multiply(up, farPlaneHeight / 2f)), Vector3D.multiply(right, farPlaneWidth / 2f));
        Point3D farBottomLeft = Point3D.sub(Point3D.sub(farCenter, Vector3D.multiply(up, farPlaneHeight / 2f)), Vector3D.multiply(right, farPlaneWidth / 2f));
        Point3D farBottomRight = Point3D.add(Point3D.sub(farCenter, Vector3D.multiply(up, farPlaneHeight / 2f)), Vector3D.multiply(right, farPlaneWidth / 2f));

        log.info("ftl: " + farTopLeft);
        log.info("ftr: " + farTopRight);
        log.info("fbl: " + farBottomLeft);
        log.info("ftr: " + farBottomRight);
    }

    public static void main(String[] args) {
        Frustum frustum = new Frustum();
        Camera camera = new FreeFlyCamera();
        camera.setDirection(new Vector3D(0, 1, 0));
        Viewport viewport = new Viewport(0, 0, 1024, 768);
        frustum.getPlanes(camera, viewport);
    }
}
