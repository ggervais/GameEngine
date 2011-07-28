package com.ggervais.gameengine.physics;

import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import com.ggervais.gameengine.timing.Controller;
import org.apache.log4j.Logger;

public class MotionController extends Controller {
    private Logger log = Logger.getLogger(MotionController.class);
    private Vector3D gravity; // In m/s^2
    private Vector3D initialVelocity; // In m/s
    private Vector3D initialTranslation; // Here, translation stands for position.

    public MotionController(Vector3D gravity, float speed, float theta, float phi) {
        this(gravity, Vector3D.createFromPolarCoordinates(speed, theta, phi));
    }

    public MotionController(Vector3D gravity, Vector3D initialVelocity) {
        this.gravity = gravity;
        this.initialVelocity = initialVelocity;
        this.initialTranslation = Vector3D.zero();
    }

    @Override
    public void setControlledObject(Spatial object) {
        super.setControlledObject(object);
        if (object != null) {
            Transformation transformation = object.getLocalTransformation();
            if (transformation != null) {
                this.initialTranslation = transformation.getTranslation().copy();
            }
        }
    }

    @Override
    public void doUpdate(long currentTime) {
        long dt = currentTime - this.startTime;
        float dtSeconds = dt / 1000f;
        Vector3D currentTranslation = Vector3D.zero();

        // Newton's laws of motion.
        currentTranslation.x(this.initialTranslation.x() + this.initialVelocity.x() * dtSeconds + 0.5f * this.gravity.x() * (dtSeconds * dtSeconds));
        currentTranslation.y(this.initialTranslation.y() + this.initialVelocity.y() * dtSeconds + 0.5f * this.gravity.y() * (dtSeconds * dtSeconds));
        currentTranslation.z(this.initialTranslation.z() + this.initialVelocity.z() * dtSeconds + 0.5f * this.gravity.z() * (dtSeconds * dtSeconds));

        if (this.controlledSpatialObject != null) {
            Transformation transformation = this.controlledSpatialObject.getLocalTransformation();
            if (transformation != null) {
                transformation.setTranslation(currentTranslation.x(), currentTranslation.y(), currentTranslation.z());
            }
        }
    }
}
