package com.ggervais.gameengine.render;

import com.ggervais.gameengine.Subsystem;
import com.ggervais.gameengine.UninitializedSubsystemException;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.scene.Camera;
import org.apache.log4j.Logger;

public class DisplaySubsystem implements Subsystem {

    private static final Logger log = Logger.getLogger(DisplaySubsystem.class);

    private Matrix4x4 modelViewMatrix;
    private Matrix4x4 projectionMatrix;
    private boolean initialized;
    private static DisplaySubsystem instance;
    private Viewport viewport;
    private float near;
    private float far;

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

    private DisplaySubsystem() {

        this.initialized = false;
        this.projectionMatrix = new Matrix4x4();
        this.modelViewMatrix = new Matrix4x4();
        this.viewport = new Viewport();
        this.near = 0.001f;
        this.far = 1000f;
    }

    public static DisplaySubsystem getInstance() {
        if (instance == null) {
            instance = new DisplaySubsystem();
        }
        return instance;
    }

    public void destroy() throws UninitializedSubsystemException {
        if (!this.initialized) {
            throw new UninitializedSubsystemException();
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }


    public void init() {

    }

    public void setProjectionMatrix(Matrix4x4 projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public Matrix4x4 getModelViewMatrix() {
        return modelViewMatrix;
    }

    public void setModelViewMatrix(Matrix4x4 modelViewMatrix) {
        this.modelViewMatrix = modelViewMatrix;
    }

    public Matrix4x4 getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public void update(long currentTime) throws UninitializedSubsystemException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Ray getPickingRay(Camera camera) {
        Ray pickingRay = new Ray();

        Point3D nearPoint = Point3D.copy(this.viewport.getCenter());
        Point3D farPoint = Point3D.copy(this.viewport.getCenter());
        nearPoint.z(0);
        farPoint.z(1);

        Point3D transformedNearPoint = this.viewport.unproject(nearPoint, this.modelViewMatrix, this.projectionMatrix);
        Point3D transformedFarPoint = this.viewport.unproject(farPoint, this.modelViewMatrix, this.projectionMatrix);

        //System.out.println(transformedNearPoint + " " + transformedFarPoint);
        //System.out.println(transformedFarPoint.sub(transformedNearPoint).normalized());
        //System.out.println("=====");

        Point3D rayOrigin = transformedNearPoint.copy();
        Vector3D rayDirection = Point3D.sub(transformedFarPoint, transformedNearPoint).normalized();

        pickingRay.setOrigin(rayOrigin);
        pickingRay.setDirection(rayDirection);

        return pickingRay;
    }
}
