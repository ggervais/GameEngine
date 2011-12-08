package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import org.apache.log4j.Logger;


public class SpatialFollowingCamera extends Camera {

    private static final Logger log = Logger.getLogger(SpatialFollowingCamera.class);

    private Spatial followedObject;
    private Vector3D offsetVector;
    private float springConstant;
    private float dampingConstant;
    private Vector3D velocity;
    private long lastUpdateTime;

    public SpatialFollowingCamera(Spatial followedObject, Vector3D offsetVector) {
        this.followedObject = followedObject;
        this.offsetVector = offsetVector;
        this.springConstant = 16;
        this.dampingConstant = 16;
        this.velocity = Vector3D.zero();
        this.lastUpdateTime = 0;
    }

    @Override
    public void update(long currentTime, InputController inputController, Spatial sceneGraphRoot) {

        if (this.lastUpdateTime == 0) {
            this.lastUpdateTime = currentTime;
        }

        Matrix4x4 worldMatrix = this.followedObject.getWorldTransformation().getMatrix();

        if (worldMatrix != null) {

            Point3D cameraLookAt = worldMatrix.mult(Point3D.zero());
            Point3D idealPosition = worldMatrix.mult(Point3D.add(Point3D.zero(), this.offsetVector));

            Point3D currentPosition = getPosition();
            Vector3D displacement = Point3D.sub(currentPosition, idealPosition);

            long diff = currentTime - this.lastUpdateTime;
            float diffInSeconds = diff / 1000f;

            Vector3D springAcceleration = Vector3D.sub(displacement.multiplied(-this.springConstant), this.velocity.multiplied(this.dampingConstant));

            this.velocity.add(springAcceleration.multiplied(diffInSeconds));
            Point3D cameraPosition = Point3D.add(currentPosition, this.velocity.multiplied(diffInSeconds));

            //cameraPosition = idealPosition;

            setPosition(cameraPosition);
            setDirection(cameraLookAt.sub(cameraPosition).normalized());

            this.lastUpdateTime = currentTime;
        }
    }
}
