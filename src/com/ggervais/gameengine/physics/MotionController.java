package com.ggervais.gameengine.physics;

import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import com.ggervais.gameengine.timing.Controller;
import org.apache.log4j.Logger;

public class MotionController extends Controller {
    private Logger log = Logger.getLogger(MotionController.class);
    private Vector3D gravity; // In m/s^2
    private Vector3D initialVelocity; // In m/s
    private Transformation initialTransformation;

    public MotionController(Vector3D gravity, float speed, float theta, float phi) {
        this(gravity, Vector3D.createFromPolarCoordinates(speed, theta, phi));
    }

    public MotionController(Vector3D gravity, Vector3D initialVelocity) {
        this.gravity = gravity;
        this.initialVelocity = initialVelocity;
        this.initialTransformation = new Transformation();
    }

    @Override
    public void setControlledObject(Spatial object) {
        super.setControlledObject(object);
        if (object != null) {
            Transformation transformation = object.getLocalTransformation();
            if (transformation != null) {
                this.initialTransformation.setTranslation(transformation.getTranslation().copy());
                this.initialTransformation.setScale(transformation.getScale().copy());

                RotationMatrix copy = new RotationMatrix();
                for (int i = 1; i <= 4; i++) {
                    for (int j = 1; j <= 4; j++) {
                        copy.setElement(i, j, transformation.getRotationMatrix().getElement(i, j));
                    }
                }

                this.initialTransformation.setRotationMatrix(copy);
            }
        }
    }

    @Override
    public void doUpdate(long currentTime) {
        long dt = currentTime - this.startTime;
        float dtSeconds = dt / 1000f;
        Vector3D currentTranslation = Vector3D.zero();

        Vector3D initialTranslation = this.initialTransformation.getTranslation();

        // Newton's laws of motion.
        currentTranslation.x(initialTranslation.x() + this.initialVelocity.x() * dtSeconds + 0.5f * this.gravity.x() * (dtSeconds * dtSeconds));
        currentTranslation.y(initialTranslation.y() + this.initialVelocity.y() * dtSeconds + 0.5f * this.gravity.y() * (dtSeconds * dtSeconds));
        currentTranslation.z(initialTranslation.z() + this.initialVelocity.z() * dtSeconds + 0.5f * this.gravity.z() * (dtSeconds * dtSeconds));

        Vector3D normalizedRotation = currentTranslation.copy().normalized();

        if (this.controlledSpatialObject != null) {
            Transformation transformation = this.controlledSpatialObject.getLocalTransformation();
            if (transformation != null) {
                transformation.setTranslation(currentTranslation.x(), currentTranslation.y(), currentTranslation.z());

                RotationMatrix initialRotation = this.initialTransformation.getRotationMatrix();

                float theta = (float) Math.asin(normalizedRotation.y()); // elevation
                float phi = (float) Math.atan2(normalizedRotation.x(), normalizedRotation.z());

                //float phi = (float) Math.acos(normalizedRotation.x() / (float) Math.cos(theta));   // spin


                RotationMatrix diffMatrix = RotationMatrix.createFromXYZ(0, -phi, theta);

                RotationMatrix tempMatrix = new RotationMatrix();
                tempMatrix.mult(diffMatrix);
                tempMatrix.mult(initialRotation);

                transformation.setRotationMatrix(tempMatrix);
            }
        }
    }
}
