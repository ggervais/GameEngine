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

            Vector3D translation = worldMatrix.mult(this.offsetVector);
            Vector3D position = new Vector3D(worldMatrix.getElement(1, 4), worldMatrix.getElement(2, 4), worldMatrix.getElement(3, 4));

            setPosition(new Point3D(translation.x(), translation.y(), translation.z()));
            setDirection(Vector3D.sub(position, translation));
        }
    }
}
