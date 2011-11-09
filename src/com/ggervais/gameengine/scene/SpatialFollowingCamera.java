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

    public SpatialFollowingCamera(Spatial followedObject, Vector3D offsetVector) {
        this.followedObject = followedObject;
        this.offsetVector = offsetVector;
    }

    @Override
    public void update(InputController inputController, Spatial sceneGraphRoot) {
        Matrix4x4 worldMatrix = this.followedObject.getWorldTransformation().getMatrix();

        if (worldMatrix != null) {

            Point3D cameraLookAt = worldMatrix.mult(Point3D.zero());
            Point3D cameraPosition = worldMatrix.mult(Point3D.add(Point3D.zero(), this.offsetVector));

            setPosition(cameraPosition);
            setDirection(cameraLookAt.sub(cameraPosition));
        }
    }
}
