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

        Point3D nearPoint = Point3D.add(cameraPosition, cameraDirection.multiplied(this.near));
        Point3D farPoint = Point3D.add(cameraPosition, cameraDirection.multiplied(this.far));

        Ray viewRay = new Ray(camera.getPosition(), cameraDirection);

        float nearPlaneHeight = 2f * (float) Math.tan(fieldOfView / 2f) * this.near;
        float nearPlaneWidth = nearPlaneHeight * viewport.getAspectRatio();

        float farPlaneHeight = 2f * (float) Math.tan(fieldOfView / 2f) * this.far;
        float farPlaneWidth = farPlaneHeight * viewport.getAspectRatio();

        log.info("Near plane width, height: " + nearPlaneWidth + " " + nearPlaneHeight);
        log.info("Far plane width, height: " + farPlaneWidth + " " + farPlaneHeight);
    }

    public static void main(String[] args) {
        Frustum frustum = new Frustum();
        Camera camera = new FreeFlyCamera();
        Viewport viewport = new Viewport(0, 0, 1024, 768);
        frustum.getPlanes(camera, viewport);
    }
}
